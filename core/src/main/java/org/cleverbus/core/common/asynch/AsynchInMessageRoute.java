/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.core.common.asynch;

import static org.cleverbus.api.asynch.AsynchConstants.ERR_CALLBACK_RES_PROP;
import static org.cleverbus.api.asynch.AsynchConstants.OPERATION_HEADER;
import static org.cleverbus.api.asynch.AsynchConstants.SERVICE_HEADER;
import static org.cleverbus.api.asynch.AsynchConstants.URI_ASYNCH_IN_MSG;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.AsynchResponseProcessor;
import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.asynch.model.ConfirmationTypes;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.StoppingException;
import org.cleverbus.api.exception.ThrottlingExceededException;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.common.log.LogContextFilter;
import org.cleverbus.core.common.asynch.msg.MessageTransformer;
import org.cleverbus.core.common.asynch.stop.StopService;
import org.cleverbus.core.common.event.AsynchEventHelper;
import org.cleverbus.core.common.exception.ExceptionTranslator;
import org.cleverbus.core.common.validator.TraceIdentifierValidator;
import org.cleverbus.spi.msg.MessageService;
import org.cleverbus.spi.throttling.ThrottleScope;
import org.cleverbus.spi.throttling.ThrottlingProcessor;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Route definition that processes incoming asynchronous message and make the following steps:
 * <ol>
 *     <li>parse trace (SOAP) header from the request
 *     <li>creates {@link Message} entity
 *     <li>check throttling
 *     <li>saves Message into db
 *     <li>creates OK/FAIL response
 * </ol>
 *
 * If everything works fine then the message is asynchronously redirected for next processing
 * without need to take it from message queue.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see AsynchResponseProcessor
 */
@CamelConfiguration(value = AsynchInMessageRoute.ROUTE_BEAN)
public class AsynchInMessageRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "inMsgRouteBean";

    /**
     * The main route for processing incoming asynchronous messages.
     */
    public static final String ROUTE_ID_ASYNC = "asyncProcessIn" + AbstractBasicRoute.ROUTE_SUFFIX;

    static final int NEW_MSG_PRIORITY = 10;

    static final String URI_GUARANTEED_ORDER_ROUTE = "direct:guaranteedOrderRoute";

    static final String ROUTE_ID_GUARANTEED_ORDER = "guaranteedOrder" + AbstractBasicRoute.ROUTE_SUFFIX;

    @Autowired
    private ThrottlingProcessor throttlingProcessor;

    // list of validator for trace identifier is not mandatory
    @Autowired(required = false)
    private List<TraceIdentifierValidator> validatorList;

    /**
     * Route for incoming asynchronous message input operation.
     * <p/>
     * Prerequisite: defined message headers {@link AsynchConstants#SERVICE_HEADER}, {@link AsynchConstants#OPERATION_HEADER}
     *      and optional {@link AsynchConstants#OBJECT_ID_HEADER}
     * <p/>
     * Output: {@link CallbackResponse} for OK message or fill "{@value AsynchConstants#ERR_CALLBACK_RES_PROP}" exchange property
     *      if error occurred
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doConfigure() throws Exception {

        from(URI_ASYNCH_IN_MSG)
            .routeId(ROUTE_ID_ASYNC)

            .doTry()

                // check headers existence
                .validate(header(SERVICE_HEADER).isNotNull())
                .validate(header(OPERATION_HEADER).isNotNull())

                // check if ESB is not stopping?
                .beanRef(ROUTE_BEAN, "checkStopping").id("stopChecking")

                // extract trace header, trace header is mandatory
                .process(new TraceHeaderProcessor(true, validatorList))
                // remove inbound Spring WS SOAP header, so it isn't added to outbound SOAP messages
                .removeHeader(SpringWebserviceConstants.SPRING_WS_SOAP_HEADER)

                // create Message (state = PROCESSING)
                .bean(MessageTransformer.getInstance(), "createMessage")

                // throttling
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Message msg = exchange.getIn().getBody(Message.class);

                        Assert.notNull(msg, "the msg must not be null");

                        ThrottleScope throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(),
                                msg.getOperationName());

                        throttlingProcessor.throttle(throttleScope);
                    }
                }).id("throttleProcess")

                // save it to DB
                .to("jpa:" + Message.class.getName() + "?usePersist=true")

                // check guaranteed order
//                .to(ExchangePattern.InOnly, URI_GUARANTEED_ORDER_ROUTE)
                //TODO (juza) finish in 1.1 version
                .to(URI_GUARANTEED_ORDER_ROUTE)

                // create OK response
                .beanRef(ROUTE_BEAN, "createOkResponse")

            .endDoTry()

            .doCatch(ThrottlingExceededException.class)
                // we want to throw exception, not return fail response
                .log(LoggingLevel.ERROR, "Incoming route - throttling rules were exceeded: ${property."
                        + Exchange.EXCEPTION_CAUGHT + ".message}.")

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    }
                })

            .doCatch(StoppingException.class)
                // we want to throw exception, not return fail response
                .log(LoggingLevel.INFO, "Incoming route - asynchronous message was rejected because ESB was stopping.")

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    }
                })

            .doCatch(SQLException.class, Exception.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Exception ex = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                        Log.error("Incoming route - error during saving incoming message: ", ex);
                    }
                })

                // create FAIL response
                .bean(AsynchInMessageRoute.class, "createFailResponse")

            .end()

            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    // nothing to do - it's because correct running unit tests
                }
            });


        // check guaranteed order
        from(URI_GUARANTEED_ORDER_ROUTE)
                .routeId(ROUTE_ID_GUARANTEED_ORDER)
                .errorHandler(noErrorHandler())

                .validate(body().isInstanceOf(Message.class))

                // for case when exception is thrown - message has been already saved into DB
                //  => mark it as PARTLY_FAILED and process it later in standard way
//                .setHeader(AsynchConstants.ASYNCH_MSG_HEADER, constant(true))
                //TODO (juza) finish in 1.1 version + delete errorHandler

                .choice()
                    .when().method(ROUTE_BEAN, "isMsgInGuaranteedOrder")
                        // no guaranteed order or message in the right order => continue

                        .beanRef(ROUTE_BEAN, "saveLogContextParams")

                        .beanRef(ROUTE_BEAN, "setInsertQueueTimestamp")

                        .beanRef(ROUTE_BEAN, "setMsgPriority")

                        // redirect message asynchronously for next processing
                        .to(ExchangePattern.RobustInOnly, AsynchConstants.URI_ASYNC_MSG).id("toAsyncRoute")

                    .otherwise()

                        // message isn't in right guaranteed order => postpone
                        .beanRef(ROUTE_BEAN, "postponeMessage")
                .end()

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        // nothing to do - it's because correct running unit tests
                    }
                });
    }

    /**
     * Checks if specified message should be processed in guaranteed order and if yes
     * then checks if the message is in the right order.
     *
     * @param msg the asynchronous message
     * @return {@code true} if message's order is ok otherwise {@code false}
     */
    @Handler
    public boolean isMsgInGuaranteedOrder(@Body Message msg) {
        if (!msg.isGuaranteedOrder()) {
            // no guaranteed order => continue
            return true;
        } else {
            // guaranteed order => is the message in the right order?
            List<Message> messages = getBean(MessageService.class)
                    .getMessagesForGuaranteedOrderForRoute(msg.getFunnelValue(), msg.isExcludeFailedState());

            if (messages.size() == 1) {
                Log.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                        + " => continue");

                return true;

            // is specified message first one for processing?
            } else if (messages.get(0).equals(msg)) {
                Log.debug("Processing message (msg_id = {}, funnel value = '{}') is the first one"
                        + " => continue", msg.getMsgId(), msg.getFunnelValue());

                return true;

            } else {
                Log.debug("There is at least one processing message with funnel value '{}'"
                        + " before current message (msg_id = {}); message {} will be postponed.",
                        msg.getFunnelValue(), msg.getMsgId(), msg.toHumanString());

                return false;
            }
        }
    }

    @Handler
    public void postponeMessage(Exchange exchange, @Body Message msg) {
        // set Message to header because of event notification
        exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, msg);

        // change state
        getBean(MessageService.class).setStatePostponed(msg);

        // generates event
        AsynchEventHelper.notifyMsgPostponed(exchange);
    }

    /**
     * Checks if ESB goes down or not. If yes then {@link StopService} is thrown.
     */
    @Handler
    public void checkStopping() {
        StopService stopService = getApplicationContext().getBean(StopService.class);

        if (stopService.isStopping()) {
            throw new StoppingException("ESB is stopping ...");
        }
    }

    /**
     * Saves log request ID into header {@link LogContextFilter#CTX_REQUEST_ID}.
     * It's because child threads don't inherits this information from parent thread automatically.
     *
     * @param msg the message
     * @param headers the incoming message headers
     */
    @Handler
    public void saveLogContextParams(@Body Message msg, @Headers Map<String, Object> headers) {
        // request ID should be set from LogContextFilter#initContext
        Map contextMap = MDC.getCopyOfContextMap();

        String requestId = null;
        if (contextMap != null && contextMap.get(LogContextFilter.CTX_REQUEST_ID) != null) {
            requestId = (String) contextMap.get(LogContextFilter.CTX_REQUEST_ID);
            headers.put(LogContextFilter.CTX_REQUEST_ID, requestId);
        }

        LogContextHelper.setLogContextParams(msg, requestId);
    }

    @Handler
    public void setInsertQueueTimestamp(@Headers Map<String, Object> headers) {
        headers.put(AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());
    }

    @Handler
    public void setMsgPriority(@Body Message msg) {
        // new messages will be processed earlier then PARTLY_FAILED or POSTPONED messages
        msg.setProcessingPriority(NEW_MSG_PRIORITY);
    }

    /**
     * Creates OK response.
     *
     * @param exchange the exchange
     * @return CallbackResponse
     */
    @Handler
    public CallbackResponse createOkResponse(Exchange exchange) {
        CallbackResponse callbackResponse = new CallbackResponse();
        callbackResponse.setStatus(ConfirmationTypes.OK);

        return callbackResponse;
    }

    /**
     * Creates FAIL response {@link CallbackResponse}
     * and saves it into "{@value AsynchConstants#ERR_CALLBACK_RES_PROP}" exchange property.
     *
     * @param exchange the exchange
     */
    @Handler
    public void createFailResponse(Exchange exchange) {
        // can be more errors during processing
        if (exchange.getProperty(ERR_CALLBACK_RES_PROP) != null) {
            return;
        }

        CallbackResponse callbackResponse = new CallbackResponse();
        callbackResponse.setStatus(ConfirmationTypes.FAIL);

        // creates error message
        Exception ex = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        String additionalInfo;
        if (ex instanceof IntegrationException) {
            additionalInfo = ((IntegrationException) ex).getError() + ": " + ex.getMessage();
        } else {
            additionalInfo = ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E106, ex);
        }
        callbackResponse.setAdditionalInfo(additionalInfo);

        exchange.setProperty(ERR_CALLBACK_RES_PROP, callbackResponse);
    }
}
