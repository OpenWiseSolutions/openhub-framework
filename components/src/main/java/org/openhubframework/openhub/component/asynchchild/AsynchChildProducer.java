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

package org.openhubframework.openhub.component.asynchchild;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.msg.ChildMessage;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.spi.msg.MessageService;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Producer for {@link AsynchChildComponent asynch-child} component.
 *
 * @author Petr Juza
 */
public class AsynchChildProducer extends DefaultProducer {

    public static final String DEFAULT_EXTERNAL_SYSTEM = "IP";

    /**
     * Creates new producer.
     *
     * @param endpoint the endpoint
     */
    public AsynchChildProducer(AsynchChildEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public final void process(Exchange exchange) throws Exception {
        Message parentMsg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
        String body = exchange.getIn().getBody(String.class);

        Assert.hasText(body, "the body must not be empty");

        final AsynchChildEndpoint endpoint = (AsynchChildEndpoint) getEndpoint();

        String correlationId = (StringUtils.isNotEmpty(endpoint.getCorrelationId())
                                ? endpoint.getCorrelationId() : generateCorrelationId());

        Log.debug("Creates child message from " + (parentMsg != null ? "synchronous" : "asynchronous") + " message.");

        ServiceExtEnum serviceExt = new ServiceExtEnum() {
            @Override
            public String getServiceName() {
                return endpoint.getService();
            }
        };

        ExternalSystemExtEnum externalSystemExt = new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return StringUtils.isNotEmpty(endpoint.getSourceSystem())
                       ? endpoint.getSourceSystem() : DEFAULT_EXTERNAL_SYSTEM;
            }
        };

        Message newMsg;

        if (parentMsg != null) {
            // for asynchronous parent message - creates child message
            ChildMessage childMessage = new ChildMessage(parentMsg, endpoint.getBindingType(), serviceExt,
                    endpoint.getOperationName(), body, endpoint.getObjectId(), null, endpoint.getFunnelValue());

            newMsg = ChildMessage.createMessage(childMessage);

            if (StringUtils.isNotEmpty(endpoint.getSourceSystem())) {
                newMsg.setSourceSystem(externalSystemExt);
            }
        } else {
            // for synchronous message
            newMsg = createNewMessage(serviceExt, externalSystemExt, endpoint.getOperationName(), body,
                    endpoint.getObjectId());
        }

        // set correlationID
        newMsg.setCorrelationId(correlationId);

        // save message
        try {
            insertMessage(newMsg);
        } catch (Exception ex) {
            if (parentMsg != null) {
                // it's better to reset parent flag
                parentMsg.setParentMessage(false);
            }
        }

        // send message for next processing
        sendForNextProcessing(newMsg);
    }

    /**
     * Creates the {@link Message} object that represents asynchronous message to next step processing.
     *
     * @param service       the calling service
     * @param externalSystem the external system
     * @param operationName the name of operation
     * @param payload       the original payload
     * @param objectId      the object ID
     * @return the {@link Message} object that represents asynchronous message to next step processing
     */
    private Message createNewMessage(ServiceExtEnum service, ExternalSystemExtEnum externalSystem,
            String operationName, String payload, String objectId) {

        Date currDate = DateTime.now().toDate();

        Message msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setStartProcessTimestamp(currDate);
        msg.setCorrelationId(""); // will be set later
        msg.setLastUpdateTimestamp(currDate);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(externalSystem);
        msg.setService(service);
        msg.setOperationName(operationName);
        msg.setPayload(payload);
        msg.setObjectId(objectId);

        return msg;
    }

    /**
     * Generates unique ID via {@link UUID#randomUUID()}.
     *
     * @return unique ID
     */
    protected String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Inserts new messages into DB.
     *
     * @param msg the message
     */
    protected void insertMessage(Message msg) {
        getMessageService().insertMessages(Arrays.asList(msg));
    }

    /**
     * Gets {@link MessageService} instance from Camel Context.
     *
     * @return MessageService
     * @throws IllegalStateException when there is no MessageService
     */
    protected MessageService getMessageService() {
        if (!isStarted() && !isStarting()) {
            throw new IllegalStateException(getClass().getName() + " is not started so far!");
        }

        Set<MessageService> services = getEndpoint().getCamelContext().getRegistry().findByType(MessageService.class);
        Assert.state(services.size() >= 1, "MessageService must be at least one.");

        return services.iterator().next();
    }

    /**
     * Sends message for next asynchronous processing.
     *
     * @param msg the message
     */
    protected void sendForNextProcessing(Message msg) {
        try {
            getProducerTemplate().sendBodyAndHeader(AsynchConstants.URI_ASYNC_MSG, ExchangePattern.InOnly, msg,
                    AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());
        } catch (CamelExecutionException ex) {
            Log.error("Error occurred in message " + msg.toHumanString() + " processing", ex);
            throw ex;
        }
    }

    /**
     * Gets {@link ProducerTemplate} default instance from Camel Context.
     *
     * @return ProducerTemplate
     * @throws IllegalStateException when there is no ProducerTemplate
     */
    protected ProducerTemplate getProducerTemplate() {
        if (!isStarted() && !isStarting()) {
            throw new IllegalStateException(getClass().getName() + " is not started so far!");
        }

        Set<ProducerTemplate> templates = getEndpoint().getCamelContext().getRegistry().findByType(ProducerTemplate.class);
        Assert.state(templates.size() >= 1, "ProducerTemplate must be at least one.");

        return templates.iterator().next();
    }
}
