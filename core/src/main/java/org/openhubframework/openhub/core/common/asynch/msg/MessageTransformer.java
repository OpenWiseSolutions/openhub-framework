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

package org.openhubframework.openhub.core.common.asynch.msg;

import java.time.Instant;
import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.component.spring.ws.SpringWebserviceMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringResult;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.model.TraceHeader;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.api.entity.*;
import org.openhubframework.openhub.core.common.asynch.AsynchInMessageRoute;
import org.openhubframework.openhub.core.common.asynch.TraceHeaderProcessor;


/**
 * Processes input message and transform it to {@link Message} entity.
 * <p>
 * Prerequisite: exchange with several header values - see input params of {@link #createMessage}
 * (call {@link AsynchInMessageRoute} before)
 * <p>
 * Output: {@link Message} entity in the state {@link MsgStateEnum#PROCESSING} because we want to process
 * the message immediately
 *
 * @author Petr Juza
 */
public final class MessageTransformer {

    private static MessageTransformer instance;

    private MessageTransformer() {
    }

    public static MessageTransformer getInstance() {
        if (instance == null) {
            instance = new MessageTransformer();
        }

        return instance;
    }

    /**
     * Creates message entity.
     *
     * @param exchange the exchange
     * @param traceHeader the trace header
     * @param payload the message payload
     * @param service the source service
     * @param operationName the source operation name
     * @param objectId the object ID
     * @param entityType the entity type
     * @param funnelValue the funnel value
     * @param guaranteedOrder the flag if order is guaranteed or not
     * @param excludeFailedState the exclude failed state flag
     * @return new message
     */
    @Handler
    public Message createMessage(Exchange exchange,
            @Header(value = TraceHeaderProcessor.TRACE_HEADER) TraceHeader traceHeader,
            @Body String payload,
            @Header(value = AsynchConstants.SERVICE_HEADER) ServiceExtEnum service,
            @Header(value = AsynchConstants.OPERATION_HEADER) String operationName,
            @Header(value = AsynchConstants.OBJECT_ID_HEADER) @Nullable String objectId,
            @Header(value = AsynchConstants.ENTITY_TYPE_HEADER) @Nullable EntityTypeExtEnum entityType,
            @Header(value = AsynchConstants.FUNNEL_VALUE_HEADER) @Nullable String funnelValue,
            @Header(value = AsynchConstants.GUARANTEED_ORDER_HEADER) @Nullable Boolean guaranteedOrder,
            @Header(value = AsynchConstants.EXCLUDE_FAILED_HEADER) @Nullable Boolean excludeFailedState) {

        // validate input params (trace header is validated in TraceHeaderProcessor)
        Assert.notNull(exchange, "the exchange must not be null");
        Assert.notNull(traceHeader, "the traceHeader must not be null");
        Assert.notNull(payload, "the payload must not be null");
        Assert.notNull(service, "the service must not be null");
        Assert.notNull(operationName, "the operationName must not be null");

        Instant currDate = Instant.now();

        Message msg = new Message();
        msg.setState(MsgStateEnum.NEW);
        msg.setStartProcessTimestamp(currDate);

        // params from trace header
        final TraceIdentifier traceId = traceHeader.getTraceIdentifier();
        msg.setMsgTimestamp(traceId.getTimestamp().toInstant());
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return StringUtils.upperCase(traceId.getApplicationID());
            }
        });
        msg.setCorrelationId(traceId.getCorrelationID());
        msg.setProcessId(traceId.getProcessID());

        msg.setService(service);
        msg.setOperationName(operationName);
        msg.setObjectId(objectId);
        msg.setEntityType(entityType);

        msg.setFunnelValue(funnelValue);
        msg.setGuaranteedOrder(BooleanUtils.isTrue(guaranteedOrder));
        msg.setExcludeFailedState(BooleanUtils.isTrue(excludeFailedState));

        msg.setPayload(payload);
        msg.setEnvelope(getSOAPEnvelope(exchange));

        msg.setLastUpdateTimestamp(currDate);

        return msg;
    }

    /**
     * Gets original SOAP envelope.
     *
     * @param exchange the exchange
     * @return envelope as string or {@code null} if input message isn't Spring web service message
     */
    @Nullable
    public static String getSOAPEnvelope(Exchange exchange) {
        if (! (exchange.getIn() instanceof SpringWebserviceMessage)) {
            return null;
        }

        try {
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();

            SpringWebserviceMessage inMsg = (SpringWebserviceMessage) exchange.getIn();
            Source source = ((SaajSoapMessage) inMsg.getWebServiceMessage()).getEnvelope().getSource();

            StringResult strRes = new StringResult();
            aTransformer.transform(source, strRes);

            return strRes.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Error occurred during conversion SOAP envelope to string", ex);
        }
    }
}
