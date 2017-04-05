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

package org.openhubframework.openhub.core.common.asynch;

import static org.openhubframework.openhub.api.asynch.AsynchConstants.*;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_CONCURRENT_CONSUMERS;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_COUNT_PARTLY_FAILS_BEFORE_FAILED;

import java.util.Map;
import javax.annotation.Nullable;

import org.apache.camel.*;
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.confirm.ConfirmationCallback;
import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.api.exception.StoppingException;
import org.openhubframework.openhub.api.extcall.ExtCallComponentParams;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.log.LogContextFilter;
import org.openhubframework.openhub.core.common.asynch.confirm.ConfirmationService;
import org.openhubframework.openhub.core.common.event.AsynchEventHelper;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.spi.node.NodeService;

/**
 * Route definition that processes asynchronous message taken from message queue.
 * <p>
 * Some "well-known" URIs:
 * <ul>
 *     <li>{@link AsynchConstants#URI_ERROR_FATAL}
 *     <li>{@link AsynchConstants#URI_ASYNC_MSG}
 *     <li>{@link AsynchConstants#URI_ERROR_HANDLING}
 * </ul>
 *
 * @author Petr Juza
 */
@CamelConfiguration(value = AsynchMessageRoute.ROUTE_BEAN)
public class AsynchMessageRoute extends AbstractBasicRoute {

    private static final Logger LOG = LoggerFactory.getLogger(AsynchMessageRoute.class);

    public static final String ROUTE_BEAN = "msgRouteBean";

    /**
     * The main route for processing message from the queue.
     */
    public static final String ROUTE_ID_SYNC = "syncProcessOut" + ROUTE_SUFFIX;

    /**
     * The route that polls the message queue for processing a new message.
     */
    public static final String ROUTE_ID_ASYNC = "asyncProcess" + ROUTE_SUFFIX;

    /**
     * The route that gets message from SEDA and processing it.
     */
    private static final String ROUTE_ID_ASYNC_OUT = "asyncProcessOut" + ROUTE_SUFFIX;

    /**
     * Route for error handling.
     */
    public static final String ROUTE_ID_ASYNCH_ERROR_HANDLING = "asynchProcessOutErrHandling" + ROUTE_SUFFIX;

    /**
     * Route for processing FATAL error.
     */
    public static final String ROUTE_ID_ERROR_FATAL = "asynchProcessOutErrFatal" + ROUTE_SUFFIX;

    /**
     * Route for post-processing when FATAL error occurs.
     */
    public static final String ROUTE_ID_POST_PROCESS_AFTER_FAILED = "postProcessAfterFatal" + ROUTE_SUFFIX;

    /**
     * Route for post-processing when FATAL error occurs.
     */
    public static final String ROUTE_ID_POST_PROCESS_AFTER_OK = "postProcessAfterOK" + ROUTE_SUFFIX;

    /**
     * Route for ensuring the provided message status confirmation to the source system.
     */
    public static final String ROUTE_ID_CONFIRM_MESSAGE = "asynchConfirm" + ROUTE_SUFFIX;

    /**
     * URI for asynchronous message processing (with SEDA).
     */
    public static final String URI_ASYNC_PROCESSING_MSG = "seda:asynch_message_route"
            + "?concurrentConsumers={{" + ASYNCH_CONCURRENT_CONSUMERS + "}}&waitForTaskToComplete=Never"
            + "&blockWhenFull=true&queueFactory=#" + PRIORITY_QUEUE_FACTORY;

    /**
     * URI for synchronous message processing.
     */
    public static final String URI_SYNC_MSG = "direct:sync_message_route";

    /**
     * Count of partly fails before message will be marked as completely FAILED.
     */
    @ConfigurableValue(key = ASYNCH_COUNT_PARTLY_FAILS_BEFORE_FAILED)
    private ConfigurationItem<Integer> countPartlyFailsBeforeFailed;


    /**
     * Route for processing {@link Message messages} from the queue.
     * <p>
     * Prerequisite: Message in the body in the state {@link MsgStateEnum#PROCESSING}
     * <p>
     * Output: correctly processed message
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doConfigure() throws Exception {

        // handle WS faults as exceptions
        getContext().setHandleFault(true);

        // log every exception at ERROR level
        // note: logging handler extends DefaultErrorHandler
        getContext().setErrorHandlerBuilder(loggingErrorHandler("org.openhubframework.openhub"));

        onException(Exception.class)
            .handled(true)
            .log(LoggingLevel.DEBUG, "Asynch. routes - unspecified exception caught: ${property."
                    + Exchange.EXCEPTION_CAUGHT + ".message}.")
            // forcefully mark current external call as unsuccessful
            .setProperty(ExtCallComponentParams.EXTERNAL_CALL_SUCCESS, constant(false))
            .to(URI_ERROR_HANDLING);

        // route for input asynchronous processing
        from(AsynchConstants.URI_ASYNC_MSG)
                .routeId(ROUTE_ID_ASYNC)
                .bean(ROUTE_BEAN, "changeStateMessageToInQueue")
                .to(URI_ASYNC_PROCESSING_MSG);

        // route for output asynchronous processing from SEDA
        from(URI_ASYNC_PROCESSING_MSG)
                .routeId(ROUTE_ID_ASYNC_OUT)
                .bean(ROUTE_BEAN, "setLogContextParams")
                .to(URI_SYNC_MSG);


        // route for synchronous processing
        from(URI_SYNC_MSG)
                .routeId(ROUTE_ID_SYNC)

                // set flag that it's asynchronous processing
                .setHeader(ASYNCH_MSG_HEADER, constant(Boolean.TRUE))

                // check Message
                .validate(body().isInstanceOf(Message.class))
                .validate((Predicate) simple(
                        "${body.state} == ${type:org.openhubframework.openhub.api.entity.MsgStateEnum.IN_QUEUE}"))

                .bean(this, "logStartProcessing")

                // check if node process existing message
                .bean(ROUTE_BEAN, "isAbleToHandleExistingMessage")

                .choice()
                    .when().method(ROUTE_BEAN, "isMessageObsolete")
                        .log(LoggingLevel.WARN,
                                "Message ${body.toHumanString} was obsolete, stopped further processing.")
                        .stop()
                .end()

                // set objectId and objectType (it's set in first message processing but no for next ones)
                .bean(this, "setEntityInfo")

                // save Message into header and change body into payload
                .setHeader(MSG_HEADER, body())
                .transform(simple("${body.payload}"))

                // reset TRACE_HEADER for all cases because it's available for first try only
                .removeHeader(TraceHeaderProcessor.TRACE_HEADER)
                // also remove inbound Spring WS SOAP header
                .removeHeader(SpringWebserviceConstants.SPRING_WS_SOAP_HEADER)

                // notify that message is being processed
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        AsynchEventHelper.notifyMsgProcessing(exchange);
                    }
                })

                // redirect message to next route
                .routingSlip(method(ROUTE_BEAN, "nextRoute"))

                .choice()
                    .when(header(NO_EFFECT_PROCESS_HEADER).isEqualTo(Boolean.TRUE))
                         // mark message as PARTLY_FAILED but without increasing error count
                        .bean(MessageService.BEAN, "setStatePartlyFailedWithoutError")

                    .when().method(ROUTE_BEAN, "checkParentMessage")
                        // it's a parent message
                        .bean(MessageService.BEAN, "setStateWaiting")

                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Message msg = (Message) exchange.getIn().getHeader(MSG_HEADER);

                                // check current state (it's possible that parent message has been already finished)
                                if (msg.getState() == MsgStateEnum.WAITING) {
                                    AsynchEventHelper.notifyMsgWaiting(exchange);
                                }
                            }
                        })

                    .otherwise()
                        // everything OK => mark processing as successful
                        .bean(MessageService.BEAN, "setStateOk")

                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                AsynchEventHelper.notifyMsgCompleted(exchange);
                            }
                        })

                        .to(AsynchConstants.URI_POST_PROCESS_AFTER_OK)
                    .end()
                .end();


        // route for error handling
        from(URI_ERROR_HANDLING)
            .routeId(ROUTE_ID_ASYNCH_ERROR_HANDLING)

            .onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error while handling error: ${exception.stacktrace}")
            .end()

            .validate(exchangeProperty(Exchange.EXCEPTION_CAUGHT).isNotNull())

            .log(LoggingLevel.WARN,
                    "Error occurred during route processing: ${property." + Exchange.EXCEPTION_CAUGHT + "}")

            .to("log:" + AsynchMessageRoute.class.getPackage().getName()
                    + "?level=WARN&showCaughtException=true&showStackTrace=true&multiline=true")

            .choice()
                // note: I don't know why but the same version with simple and spel language doesn't work
                // .when(simple("${header[" + MSG_HEADER + "].failedCount} == " + countPartlyFailsBeforeFailed))
                // .when().spel("#{request.headers['" + MSG_HEADER + "'].failedCount} >= " + countPartlyFailsBeforeFailed)

                .when().method(ROUTE_BEAN, "checkMessageFailed")
                    .to(URI_ERROR_FATAL)

                .otherwise()
                    // => mark as PARTLY FAILED
                    .bean(MessageService.BEAN, "setStatePartlyFailed")

                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            AsynchEventHelper.notifyMsgPartlyFailed(exchange);
                        }
                    })
            .end()

            // stop next processing - update message state and stop
            .stop();


        // route for FATAL result
        from(URI_ERROR_FATAL)
            .routeId(ROUTE_ID_ERROR_FATAL)

            .onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error while handling error: ${exception.stacktrace}")
            .end()

            // => completely FAILED
            .bean(MessageService.BEAN, "setStateFailed")

            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    AsynchEventHelper.notifyMsgFailed(exchange);
                }
            })

            .to(AsynchConstants.URI_POST_PROCESS_AFTER_FAILED)
            // stop next processing - update message state and stop
            .stop();


        from(AsynchConstants.URI_POST_PROCESS_AFTER_OK)
            .routeId(ROUTE_ID_POST_PROCESS_AFTER_OK)
            .validate(header(MSG_HEADER).isNotNull())
            .to(AsynchConstants.URI_CONFIRM_MESSAGE);


        from(AsynchConstants.URI_POST_PROCESS_AFTER_FAILED)
            .routeId(ROUTE_ID_POST_PROCESS_AFTER_FAILED)
            .validate(header(MSG_HEADER).isNotNull())
            .to(AsynchConstants.URI_CONFIRM_MESSAGE)
            .bean(ROUTE_BEAN, "sendMailToAdmin").id("sendEmail");


        // confirmation route
        from(AsynchConstants.URI_CONFIRM_MESSAGE)
            .routeId(ROUTE_ID_CONFIRM_MESSAGE)

            .onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "exception during confirmation caught "
                        + "- '${property." + Exchange.EXCEPTION_CAUGHT + ".message}'")
                .choice()
                    .when(body().isInstanceOf(ExternalCall.class))
                        // existing confirmation external call - just update it to failed state
                        .bean(ConfirmationService.BEAN, "confirmationFailed")
                    .otherwise()
                        // no existing confirmation external call - record a new one with failed state
                        .bean(ConfirmationService.BEAN, "insertFailedConfirmation")
                .end()
            .end()

            .choice()
                .when(header(MSG_HEADER).isNull())
                    // confirmation route can be called from scheduler => load message
                    .validate(body().isInstanceOf(ExternalCall.class))
                    .setHeader(MSG_HEADER).mvel("request.body.message")
                .endChoice()
            .end()

            // note: if there it's child message then it's without confirmation (need to do it)
            .bean(ConfirmationCallback.BEAN, "confirm")

            .filter(body().isInstanceOf(ExternalCall.class))
                // for successful confirmations only record if it's a repeating attempt
                .bean(ConfirmationService.BEAN, "confirmationComplete")
            .end();
    }

    /**
     * Set log context parameters.
     *
     * @param message the message
     * @param requestId the request ID
     * @see LogContextHelper#setLogContextParams(Message, String)
     */
    @Handler
    public void setLogContextParams(@Body Message message,
            @Header(LogContextFilter.CTX_REQUEST_ID) @Nullable String requestId) {
        LogContextHelper.setLogContextParams(message, requestId);
    }

    /**
     * Checks if current message wasn't converted to other state.
     * It can happen when message is long time in queue that repairing process converts message back
     * to PARTLY_FAILED state and evenly message can start with duplicate processing.
     *
     * @param msg the message
     * @return {@code true} when message is in wrong state {@code false} otherwise
     */
    @Handler
    public boolean isMessageObsolete(@Body Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        MessageService messageService = getBean(MessageService.class);

        return !messageService.setStateProcessingForLock(msg);
    }

    @Handler
    public void logStartProcessing(@Body Message msg,
            @Nullable @Header(AsynchConstants.MSG_QUEUE_INSERT_HEADER) Long msgInsertTime) {

        LOG.debug("Starts processing of the message {}, waited in queue for {} ms", msg.toHumanString(),
                msgInsertTime != null ? (System.currentTimeMillis() - msgInsertTime) : "-");
    }

    /**
     * Sets {@link AsynchConstants#OBJECT_ID_HEADER} and {@link AsynchConstants#ENTITY_TYPE_HEADER}
     * headers if there are available corresponding values in message.
     *
     * @param msg the message
     * @param headers the headers
     */
    @Handler
    public void setEntityInfo(@Body Message msg, @Headers Map<String, Object> headers) {
        Assert.notNull(msg, "the msg must not be null");

        if (msg.getObjectId() != null) {
            headers.put(OBJECT_ID_HEADER, msg.getObjectId());
        }
        if (msg.getEntityType() != null) {
            headers.put(ENTITY_TYPE_HEADER, msg.getEntityType());
        }
    }

    /**
     * Returns {@code true} if failed count exceeds limit for failing.
     *
     * @param msg the message
     * @param ex  error in processing {@link Message}, {@code NULL} - no error
     * @return {@code true} when limit was exceeded, otherwise {@code false}
     */
    @Handler
    public boolean checkMessageFailed(@Header(MSG_HEADER) Message msg, @Nullable Exception ex) {
        Assert.notNull(msg, "the msg must not be null");

        boolean result = msg.getFailedCount() >= countPartlyFailsBeforeFailed.getValue();
        if (!result && ex != null && ExceptionUtils.indexOfThrowable(ex, StoppingException.class) >= 0) {
            LOG.warn("ESB not processing existing message. Message " + ex.getMessage()
                    + " is changed to failed state.");
            result = true;
        }
        return result;
    }

    @Handler
    public boolean checkParentMessage(@Header(MSG_HEADER) Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        return msg.isParentMessage();
    }

    /**
     * Sends notification mail to admin(s).
     * 
     * @param msg as context of process
     */
    @Handler
    public void sendMailToAdmin(@Header(MSG_HEADER) Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        EmailService emailService = lookup(EmailService.BEAN, EmailService.class);

        String body = "The following message " + msg.toHumanString() + " FAILED.\n\nBody:\n" + msg.getEnvelope();

        emailService.sendEmailToAdmins("Notification about FAILED message", body);
   }

    /**
     * Gets URI of the next route.
     * The URI is in the following format: "direct:SERVICE_operationName{@link AbstractBasicRoute#OUT_ROUTE_SUFFIX}"
     *
     * @param msg the message
     * @return URI of next route
     */
    @Handler
    public String nextRoute(@Header(MSG_HEADER) Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        return "direct:" + msg.getService().getServiceName() + "_" + msg.getOperationName() + OUT_ROUTE_SUFFIX;
    }

    /**
     * Change state of message into {@link MsgStateEnum#IN_QUEUE}.
     *
     * @param msg message in which will be changed state
     * @throws LockFailureException if change state into {@link MsgStateEnum#IN_QUEUE} fails
     */
    @Handler
    public void changeStateMessageToInQueue(@Body Message msg) throws LockFailureException {
        Assert.notNull(msg, "msg must not be null");

        LOG.debug("Change state of message '{}' into '{}.'", msg.toHumanString(), MsgStateEnum.IN_QUEUE);

        if (!getBean(MessageService.class).setStateInQueueForLock(msg)) {
            throw new LockFailureException("Failed to lock message for change state to '" + MsgStateEnum.IN_QUEUE
                    + "': " + msg.toHumanString());
        }
    }

    /**
     * Checks if actual node handles existing message.
     *
     * @throws StoppingException if node not handles existing message
     */
    @Handler
    public void isAbleToHandleExistingMessage() throws StoppingException {
        NodeService nodeService = getApplicationContext().getBean(NodeService.class);

        if (!nodeService.getActualNode().isAbleToHandleExistingMessages()) {
            throw new StoppingException("ESB has been stopped...");
        }
    }
}
