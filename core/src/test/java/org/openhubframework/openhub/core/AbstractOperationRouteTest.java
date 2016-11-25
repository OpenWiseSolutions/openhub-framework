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

package org.openhubframework.openhub.core;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.model.TraceHeader;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.core.common.asynch.TraceHeaderProcessor;
import org.openhubframework.openhub.core.common.asynch.queue.MessagePollExecutor;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Helper abstract parent for assisted testing of operation routes. This helper provides:
 * <ul>
 * <li>ability to easily convert JAXB POJO requests to XML requests, and vice-versa for responses</li>
 * <li>sender method to send XML payload to async IN route (as if it's a new incoming message from WS)</li>
 * <li>sender method to send XML payload to async OUT route (as if pumping persisted message from DB)</li>
 * <li>resend method to resent the last sent message to async OUT route (e.g., to resend party failed messages)</li>
 * <li>mock URI method for mocking URIs of unstarted routes (to mock WS uris without actually starting WS out routes)</li>
 * </ul>
 */
@ActiveRoutes(classes = {AsynchMessageRoute.class})
public abstract class AbstractOperationRouteTest extends AbstractCoreDbTest {

    public static final String URI_ASYNC_IN_ROUTE = "direct:testAsyncInRoute";
    public static final String URI_SYNC_ROUTE = "direct:testSyncRoute";

    @Autowired
    protected MessageDao msgDao;

    @Produce
    protected ProducerTemplate producer;

    protected Message lastMessage;

    @Override
    @After
    public void printEntities() {
        // override to add @After
        super.printEntities();
    }

    /**
     * @param testRequest the request JAXB POJO
     * @return a valid request XML that will be used to test the route
     */
    protected String getRequestXML(Object testRequest) throws Exception {
        return marshalFragment(testRequest, null);
    }

    /**
     * @param testRequest      the request JAXB POJO
     * @param testRequestQName the request QName (in case JAXB POJO is not annotated with {@link XmlRootElement})
     * @return a valid request XML that will be used to test the route
     */
    protected String getRequestXML(Object testRequest, QName testRequestQName) throws JAXBException {
        return marshalFragment(testRequest, testRequestQName);
    }

    /**
     * @return the external system that normally calls the operation route to be tested
     */
    protected abstract ExternalSystemExtEnum getSourceSystem();

    /**
     * @return the service of the operation route under test
     */
    protected abstract ServiceExtEnum getService();

    /**
     * @return the operation name of the operation route under test
     */
    protected abstract String getOperationName();

    /**
     * The route ID that acts as the IN input to the asynchronous operation,
     * responsible for delivering synchronous validation response to the caller,
     * but not response for actually executing the asynchronous operation,
     * e.g., what is returned by {@link AbstractBasicRoute#getInRouteId(ServiceExtEnum, String)}.
     *
     * @return the async IN route id.
     */
    protected String getAsyncInRouteId() {
        return AbstractBasicRoute.getInRouteId(getService(), getOperationName());
    }

    /**
     * The route ID that acts as the OUT input to the asynchronous operation,
     * responsible for actually executing the asynchronous operation,
     * but not responsible for delivering synchronous response to the caller.
     * The asynchronous operation success is reported via confirmation mechanism instead.
     * e.g., what is returned by {@link AbstractBasicRoute#getInRouteId(ServiceExtEnum, String)}.
     *
     * @return the async IN route id.
     */
    protected String getAsyncOutRouteId() {
        return AbstractBasicRoute.getOutRouteId(getService(), getOperationName());
    }

    /**
     * The route ID that acts as the input/output to the synchronous operation,
     * e.g., what is returned by {@link AbstractBasicRoute#getRouteId(ServiceExtEnum, String)}.
     *
     * @return the sync route id.
     */
    protected String getSyncRouteId() {
        return AbstractBasicRoute.getRouteId(getService(), getOperationName());
    }

    @Before
    public void connectProducers() throws Exception {
        boolean sync = replaceFrom(getSyncRouteId(), URI_SYNC_ROUTE);
        boolean async = replaceFrom(getAsyncInRouteId(), URI_ASYNC_IN_ROUTE);
        if (!sync && !async) {
            throw new IllegalArgumentException(String.format(
                    "Neither Sync, nor Async route ID is known based on Service %s and Operation Name %s" +
                            " - didn't find route with ID %s or %s",
                    getService(), getOperationName(), getSyncRouteId(), getAsyncInRouteId()));
        }
    }

    @Before
    public void redirectAsyncRoute() throws Exception {
        getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(AsynchConstants.URI_ASYNC_MSG)
                        .routeId(AsynchMessageRoute.ROUTE_ID_ASYNC)
                        .log(LoggingLevel.WARN, "Ignoring Message: ${body}");
            }
        });
    }

    /**
     * Sends the test request to the Sync route (as if it was received via Spring WS).
     *
     * @param requestXML the request payload (XML) to send
     * @return the result as an exchange with getOut() containing the response message
     */
    protected Exchange sendSyncMessage(final String requestXML) throws Exception {
        return producer.request(URI_SYNC_ROUTE, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(requestXML);
            }
        });
    }

    /**
     * Sends the test request to the Sync route (as if it was received via Spring WS).
     *
     * @param requestXML    the request payload (XML) to send
     * @param responseClass {@link String}.class to get the response body as String,
     *                      or the class to unmarshal the response body to using JAXB
     * @return the result as the specified class
     */
    protected <T> T sendSyncMessage(String requestXML, Class<T> responseClass) throws Exception {
        Exchange result = sendSyncMessage(requestXML);
        Exception exception = result.getException();
        if (exception != null) {
            throw exception;
        }
        String responseXML = result.getOut().getMandatoryBody(String.class);
        if (responseClass.isAssignableFrom(String.class)) {
            return responseClass.cast(responseXML);
        }
        return unmarshalFragment(responseXML, responseClass);
    }

    /**
     * Sends the test request to the IN route (as if it was received via Spring WS).
     *
     * @param requestXML    the request payload (XML) to send
     * @param finalState    the final state of the {@link Message} created in the DB - for automatic verification
     * @param responseClass the response class to parse response XML as
     * @return the response parsed from XML as the specified responseClass
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected <T> T sendAsyncInMessage(String requestXML, MsgStateEnum finalState, Class<T> responseClass) throws Exception {
        String correlationID = UUID.randomUUID().toString();
        return sendAsyncInMessage(correlationID, Instant.now(), requestXML, getMessageStateVerifier(finalState), responseClass);
    }

    /**
     * Sends the test request to the IN route (as if it was received via Spring WS).
     *
     * @param requestXML      the request payload (XML) to send
     * @param messageVerifier the processor that can verify the {@link Message} created in the DB
     * @param responseClass   the response class to parse response XML as
     * @return the response parsed from XML as the specified responseClass
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected <T> T sendAsyncInMessage(String requestXML, MessageCallback messageVerifier, Class<T> responseClass) throws Exception {
        String correlationID = UUID.randomUUID().toString();
        return sendAsyncInMessage(correlationID, Instant.now(), requestXML, messageVerifier, responseClass);
    }

    /**
     * Sends the test request to the IN route (as if it was received via Spring WS).
     *
     * @param correlationID   the new message correlation ID
     * @param msgTimestamp    the new message timestamp
     * @param requestXML      the request payload (XML) to send
     * @param messageVerifier the processor that can verify the {@link Message} created in the DB
     * @param responseClass   the response class to parse response XML as
     * @return the response parsed from XML as the specified responseClass
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected <T> T sendAsyncInMessage(String correlationID, Instant msgTimestamp, String requestXML,
                                       MessageCallback messageVerifier, Class<T> responseClass) throws Exception {
        Exchange result = sendAsyncInMessage(correlationID, msgTimestamp, requestXML);
        Exception exception = result.getException();
        if (exception != null) {
            throw exception;
        }

        verifyMessage(getSourceSystem(), correlationID, messageVerifier);
        String responseXML = result.getOut().getMandatoryBody(String.class);
        if (responseClass.isAssignableFrom(String.class)) {
            return responseClass.cast(responseXML);
        }
        return unmarshalFragment(responseXML, responseClass);
    }

    /**
     * Sends the test request to the IN route
     *
     * @param requestXML    the request payload (XML) to send
     * @param responseClass the response class to parse response XML as
     * @return the response parsed from XML as the specified responseClass
     */
    protected <T> T sendAsyncInMessage(String requestXML, Class<T> responseClass) throws Exception {
        return sendAsyncInMessage(requestXML, (MessageCallback) null, responseClass);
    }

    /**
     * Sends the test request to the IN route
     *
     * @param correlationID the new message correlation ID
     * @param msgTimestamp  the new message timestamp
     * @param requestXML    the request payload (XML) to send
     * @param responseClass the response class to parse response XML as
     * @return the response parsed from XML as the specified responseClass
     */
    protected <T> T sendAsyncInMessage(String correlationID, Instant msgTimestamp, String requestXML, Class<T> responseClass) throws Exception {
        return sendAsyncInMessage(correlationID, msgTimestamp, requestXML, getMessageStateVerifier(null), responseClass);
    }

    /**
     * Sends the test request to the IN route.
     *
     * @return the result as an exchange with getOut() containing the output message
     */
    protected Exchange sendAsyncInMessage(final String correlationID, final Instant timestamp, final String payload) {
        Exchange result = producer.request(URI_ASYNC_IN_ROUTE, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(payload);
                exchange.getIn().setHeaders(createTraceHeader(correlationID, timestamp));
            }
        });
        lastMessage = msgDao.findByCorrelationId(correlationID, getSourceSystem());
        return result;
    }

    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param requestXML the request payload (XML) to send
     * @param finalState the final state of the {@link Message} created in the DB - for automatic verification
     * @return the {@link Message} that was sent and processed
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected Message sendAsyncOutMessage(String requestXML, MsgStateEnum finalState) throws Exception {
        lastMessage = createAndSaveMessage(requestXML);
        producer.requestBody(AsynchMessageRoute.URI_SYNC_MSG, lastMessage);
        verifyMessage(lastMessage.getSourceSystem(), lastMessage.getCorrelationId(), getMessageStateVerifier(finalState));
        return lastMessage;
    }

    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param correlationID the new message correlation ID
     * @param msgTimestamp  the new message timestamp
     * @param requestXML    the request payload (XML) to send
     * @param finalState    the final state of the {@link Message} created in the DB - for automatic verification
     * @return the {@link Message} that was sent and processed
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected Message sendAsyncOutMessage(final String correlationID, final Instant msgTimestamp,
            final String requestXML, MsgStateEnum finalState) throws Exception {
        return sendAsyncOutMessage(new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setMsgTimestamp(msgTimestamp);
                message.setCorrelationId(correlationID);
                message.setPayload(requestXML);
            }
        }, finalState);
    }


    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param initializer the message initializer that will set message fields as necessary
     * @return the {@link Message} that was sent and processed
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected Message sendAsyncOutMessage(MessageCallback initializer) throws Exception {
        return sendAsyncOutMessage(initializer, null);
    }

    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param initializer the message initializer that will set message fields as necessary
     * @param finalState  the final state of the {@link Message} created in the DB - for automatic verification
     * @return the {@link Message} that was sent and processed
     * @throws AssertionError if the message state doesn't match the specified state
     */
    protected Message sendAsyncOutMessage(final MessageCallback initializer, MsgStateEnum finalState) throws Exception {
        lastMessage = createAndSaveMessage(new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) throws Exception {
                message.setSourceSystem(getSourceSystem());
                message.setService(getService());
                message.setOperationName(getOperationName());
                message.setMsgTimestamp(Instant.now());
                message.setReceiveTimestamp(Instant.now());
                message.setCorrelationId(UUID.randomUUID().toString());

                initializer.beforeInsert(message, 0); // let the provided initializer init the other fields
            }
        });
        producer.requestBody(AsynchMessageRoute.URI_SYNC_MSG, lastMessage);
        verifyMessage(lastMessage.getSourceSystem(), lastMessage.getCorrelationId(), getMessageStateVerifier(finalState));
        return lastMessage;
    }

    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param requestXML the request payload (XML) to send
     * @return the {@link Message} that was sent and processed
     */
    protected Message sendAsyncOutMessage(String requestXML) throws Exception {
        return sendAsyncOutMessage(requestXML, null);
    }

    /**
     * Sends a new message with the test request to the OUT route,
     * similarly to what {@link MessagePollExecutor} does.
     *
     * @param correlationID the new message correlation ID
     * @param msgTimestamp  the new message timestamp
     * @param requestXML    the request payload (XML) to send
     * @return the {@link Message} that was sent and processed
     */
    protected Message sendAsyncOutMessage(String correlationID, Instant msgTimestamp, String requestXML) throws Exception {
        return sendAsyncOutMessage(correlationID, msgTimestamp, requestXML, null);
    }

    /**
     * Re-sends the last sent message, first setting it's state to {@link MsgStateEnum#PROCESSING}.
     *
     * @return the last {@link Message}, after it was re-sent and re-processed
     */
    protected Message resendAsyncOutMessage(MsgStateEnum finalState) throws Exception {
        return resendAsyncOutMessage(lastMessage, finalState);
    }

    /**
     * Re-sends the specified message, first setting it's state to {@link MsgStateEnum#PROCESSING}.
     *
     * @return the same {@link Message}, after it was re-sent and re-processed
     */
    protected Message resendAsyncOutMessage(Message msg, MsgStateEnum finalState) throws Exception {
        msg.setState(MsgStateEnum.PROCESSING);
        producer.requestBody(AsynchMessageRoute.URI_SYNC_MSG, msg);
        verifyMessage(msg.getSourceSystem(), msg.getCorrelationId(), getMessageStateVerifier(finalState));
        return msg;
    }

    /**
     * Re-sends the last sent message, first setting it's state to {@link MsgStateEnum#PROCESSING}
     *
     * @return the last {@link Message}, after it was re-sent and re-processed
     */
    protected Message resendAsyncOutMessage() throws Exception {
        return resendAsyncOutMessage(lastMessage, null);
    }

    /**
     * Re-sends the specified message, first setting it's state to {@link MsgStateEnum#PROCESSING}
     *
     * @return the same {@link Message}, after it was re-sent and re-processed
     */
    protected Message resendAsyncOutMessage(Message msg) throws Exception {
        return resendAsyncOutMessage(msg, null);
    }

    @Nullable
    protected MessageCallback getMessageStateVerifier(@Nullable final MsgStateEnum finalState) {
        return finalState == null ? null : new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) throws Exception {
                String stateFailReason = String.format(
                        "Message doesn't have the expected state. failedErrorCode=%s, failedErrorDesc=%s, businessError=%s",
                        message.getFailedErrorCode(), message.getFailedDesc(), message.getBusinessError());
                assertThat(stateFailReason, message.getState(), is(finalState));
            }
        };
    }

    private void verifyMessage(ExternalSystemExtEnum sourceSystem, String correlationID, MessageCallback msgVerifier) throws Exception {
        if (msgVerifier != null) {
            Message message = msgDao.findByCorrelationId(correlationID, sourceSystem);

            String msgMissingReason = String.format(
                    "No message found for sourceSystem=%s and correlationID=%s", sourceSystem, correlationID);

            assertThat(msgMissingReason, message, notNullValue());
            msgVerifier.beforeInsert(message, 0);
        }
    }

    private Map<String, Object> createTraceHeader(String correlationID, Instant timestamp) {
        TraceIdentifier traceId = new TraceIdentifier();
        traceId.setCorrelationID(correlationID);
        traceId.setApplicationID(getApplicationID());
        traceId.setTimestamp(OffsetDateTime.ofInstant(timestamp, ZoneId.systemDefault()));

        TraceHeader traceHeader = new TraceHeader();
        traceHeader.setTraceIdentifier(traceId);

        final Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(TraceHeaderProcessor.TRACE_HEADER, traceHeader);
        return headers;
    }

    protected Message createAndSaveMessage(String requestXML) throws Exception {
        return createAndSaveMessage(getSourceSystem(), getService(), getOperationName(), requestXML);
    }

    protected Message createAndSaveMessage(MessageCallback initializer) throws Exception {
        return createAndSaveMessages(1, initializer)[0];
    }

    protected Message createMessage(String requestXML) {
        return createMessage(getSourceSystem(), getService(), getOperationName(), requestXML);
    }

    public Map<String, Object> createHeaders(String correlationID, String applicationID, Instant timestamp) {
        TraceIdentifier traceId = new TraceIdentifier();
        traceId.setCorrelationID(correlationID);
        traceId.setApplicationID(applicationID);
        traceId.setTimestamp(OffsetDateTime.ofInstant(timestamp, ZoneId.systemDefault()));

        TraceHeader traceHeader = new TraceHeader();
        traceHeader.setTraceIdentifier(traceId);

        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put(TraceHeaderProcessor.TRACE_HEADER, getTraceHeader());
        return headers;
    }

    /**
     * Gets the applicationID that corresponds to {@link #getSourceSystem()}.
     */
    protected String getApplicationID() {
        return getSourceSystem().getSystemName();
    }

    protected <T> T unmarshalFragment(String responseXML, Class<T> fragmentClass) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(fragmentClass).createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StringSource(responseXML), fragmentClass);
        return jaxbElement.getValue();
    }

    protected <T> String marshalFragment(T request, QName qName) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = JAXBContext.newInstance(request.getClass()).createMarshaller();
        Object element = request;
        if (qName != null) {
            element = new JAXBElement<T>(qName, (Class<T>) request.getClass(), request);
        }
        marshaller.marshal(element, stringWriter);
        return stringWriter.toString();
    }

    private boolean replaceFrom(String routeId, final String uri) throws Exception {
        RouteDefinition routeDefinition = getCamelContext().getRouteDefinition(routeId);
        if (routeDefinition != null) {
            routeDefinition.adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    replaceFromWith(uri);
                }
            });
        }
        return routeDefinition != null;
    }

    /**
     * Mocks a hand-over-type endpoint (direct, direct-vm, seda or vm)
     * by simply providing the other (consumer=From) side connected to a mock.
     * <p/>
     * There should be no consumer existing, i.e., the consumer route should not be started.
     *
     * @param uri the URI a new mock should consume from
     * @return the mock that is newly consuming from the URI
     */
    protected MockEndpoint mockDirect(final String uri) throws Exception {
        return mockDirect(uri, null);
    }

    /**
     * Same as {@link #mockDirect(String)}, except with route ID to be able to override an existing route with the mock.
     *
     * @param uri     the URI a new mock should consume from
     * @param routeId the route ID for the new mock route
     *                (existing route with this ID will be overridden by this new route)
     * @return the mock that is newly consuming from the URI
     */
    protected MockEndpoint mockDirect(final String uri, final String routeId) throws Exception {
        // precaution: check that URI can be mocked by just providing the other side:
        Assert.assertThat(uri, anyOf(startsWith("direct:"), startsWith("direct-vm:"), startsWith("seda:"), startsWith("vm:")));
        // create the mock:
        final MockEndpoint createCtidMock = getCamelContext().getEndpoint("mock:" + uri, MockEndpoint.class);
        // redirect output to this mock:
        getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                RouteDefinition routeDef = from(uri);

                if (routeId != null) {
                    routeDef.routeId(routeId);
                }

                routeDef.to(createCtidMock);
            }
        });
        return createCtidMock;
    }
}
