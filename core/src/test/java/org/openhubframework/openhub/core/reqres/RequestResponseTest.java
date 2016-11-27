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

package org.openhubframework.openhub.core.reqres;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.util.StringUtils.hasText;

import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.xml.namespace.QName;
import javax.xml.soap.*;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.api.entity.Response;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;


/**
 * Test suite for {@link RequestSendingEventNotifier} and {@link ResponseReceiveEventNotifier}.
 *
 * @author Petr Juza
 */
//TODO PJUZA correct it when Camel is in version 2.18.0
//note: we have to use Spring XML configuration of CamelContext because of this error:
//  https://issues.apache.org/jira/browse/CAMEL-10109
@ContextConfiguration(locations = "classpath:/org/openhubframework/openhub/core/reqres/test-context.xml")
public class RequestResponseTest extends AbstractCoreDbTest {

    private static final String REQUEST = "request";

    private static final String RESPONSE = "response";

    private static final String TARGET_URI = "direct://target";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private RequestSendingEventNotifier reqSendingEventNotifier;

    @Autowired
    private ResponseReceiveEventNotifier resReceiveEventNotifier;


    @Before
    public void prepareConfiguration() {
        setPrivateField(reqSendingEventNotifier, "enable", Boolean.TRUE);
        setPrivateField(reqSendingEventNotifier, "endpointFilter", "^(direct.*target).*$");
        reqSendingEventNotifier.compilePattern();

        setPrivateField(resReceiveEventNotifier, "enable", Boolean.TRUE);
        setPrivateField(resReceiveEventNotifier, "endpointFilter", "^(direct.*target).*$");
        resReceiveEventNotifier.compilePattern();
    }

    @Before
    public void prepareRoute() throws Exception {
        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to(TARGET_URI)
                        .to("mock:test");
            }
        };

        getCamelContext().addRoutes(route);
    }

    /**
     * Test saving synchronous request/response with correct calling target endpoint.
     * Then also verify two scenarios - change pattern for filtering URI and disable saving mechanism at all.
     */
    @Test
    public void testSavingRequest() throws Exception {
        // prepare target route
        prepareTargetRoute(TARGET_URI, null);

        // action
        mock.expectedMessageCount(1);
        producer.sendBody(REQUEST);

        assertRequestResponse(null, null, null);


        // try it again but change pattern for filtering
        setPrivateField(reqSendingEventNotifier, "endpointFilter", "^(noUrl).*$");
        reqSendingEventNotifier.compilePattern();

        setPrivateField(resReceiveEventNotifier, "endpointFilter", "^(noUrl).*$");
        resReceiveEventNotifier.compilePattern();

        producer.sendBody(REQUEST);

        TypedQuery<Request> queryReq = em.createQuery("FROM " + Request.class.getName(), Request.class);
        List<Request> requests = queryReq.getResultList();
        assertThat(requests.size(), is(1));

        TypedQuery<Response> queryRes = em.createQuery("FROM " + Response.class.getName(), Response.class);
        List<Response> responses = queryRes.getResultList();
        assertThat(responses.size(), is(1));


        // try it again but disable saving at all
        setPrivateField(reqSendingEventNotifier, "enable", Boolean.FALSE);
        setPrivateField(resReceiveEventNotifier, "enable", Boolean.FALSE);

        producer.sendBody(REQUEST);

        queryReq = em.createQuery("FROM " + Request.class.getName(), Request.class);
        requests = queryReq.getResultList();
        assertThat(requests.size(), is(1));

        queryRes = em.createQuery("FROM " + Response.class.getName(), Response.class);
        responses = queryRes.getResultList();
        assertThat(responses.size(), is(1));

        // identical response
        assertThat(responses.get(0), is(requests.get(0).getResponse()));
    }

    /**
     * Test saving response only, there is no request.
     */
    @Test
    public void testSavingResponseOnly() throws Exception {
        setPrivateField(reqSendingEventNotifier, "enable", Boolean.FALSE);

        // prepare target route
        prepareTargetRoute(TARGET_URI, null);

        // action
        mock.expectedMessageCount(1);
        producer.sendBody(REQUEST);

        // verify
        TypedQuery<Request> queryReq = em.createQuery("FROM " + Request.class.getName(), Request.class);
        List<Request> requests = queryReq.getResultList();
        assertThat(requests.size(), is(0));

        TypedQuery<Response> queryRes = em.createQuery("FROM " + Response.class.getName(), Response.class);
        List<Response> responses = queryRes.getResultList();
        assertThat(responses.size(), is(1));
    }

    /**
     * Test saving response with message only, there is no request.
     */
    @Test
    @Transactional
    public void testSavingResponseWithMsgOnly() throws Exception {
        setPrivateField(reqSendingEventNotifier, "enable", Boolean.FALSE);

        // prepare target route
        prepareTargetRoute(TARGET_URI, null);

        // action
        mock.expectedMessageCount(1);

        Message msg = insertMessage();

        producer.sendBodyAndHeader(REQUEST, AsynchConstants.MSG_HEADER, msg);

        // verify
        TypedQuery<Response> queryRes = em.createQuery("FROM " + Response.class.getName(), Response.class);
        List<Response> responses = queryRes.getResultList();
        assertThat(responses.size(), is(1));
    }

    /**
     * Test saving asynchronous request/response with correct calling target endpoint.
     */
    @Test
    @Transactional
    public void testSavingRequestWithAsynchMessage() throws Exception {
        // prepare target route
        prepareTargetRoute(TARGET_URI, null);

        // action
        mock.expectedMessageCount(1);

        Message msg = insertMessage();

        producer.sendBodyAndHeader(REQUEST, AsynchConstants.MSG_HEADER, msg);

        assertRequestResponse(msg, null, null);
    }

    private Message insertMessage() {
        Message msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(new Date());
        msg.setReceiveTimestamp(new Date());
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");
        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("request");
        msg.setLastUpdateTimestamp(new Date());
        em.persist(msg);
        em.flush();
        return msg;
    }

    /**
     * Test saving synchronous request/response with correct calling target endpoint
     * but with different exchange (SAVE_REQ_HEADER is removed).
     */
    @Test
    public void testSavingRequestWithDifferentExchange() throws Exception {
        // prepare target route
        prepareTargetRoute(TARGET_URI, null);

        // action
        mock.expectedMessageCount(1);
        producer.sendBody(REQUEST);

        assertRequestResponse(null, null, null);
    }

    /**
     * Test saving synchronous request/response where response is failed "normal" exception.
     */
    @Test
    public void testSavingRequestWithFailedResponse() throws Exception {
        // prepare target route
        prepareTargetRoute(TARGET_URI, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new IllegalStateException("err"));
            }
        });

        // action
        mock.expectedMessageCount(0);

        try {
            producer.sendBody(REQUEST);
            fail("Target route was thrown exception.");
        } catch (CamelExecutionException ex) {
            assertRequestResponse(null, "java.lang.IllegalStateException: err", null);
        }
    }

    /**
     * Test saving synchronous request/response where response is failed SOAP fault exception.
     */
    @Test
    public void testSavingRequestWithSOAPFaultResponse() throws Exception {

        final String soapFault = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "     <faultcode>SOAP-ENV:Server</faultcode>"
                + "     <faultstring>There is one error</faultstring>"
                + "</SOAP-ENV:Fault>";

        // prepare target route
        prepareTargetRoute(TARGET_URI, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getOut().setBody(soapFault);

                // create SOAP Fault message
                SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
                SOAPPart soapPart = soapMessage.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                SOAPBody soapBody = soapEnvelope.getBody();

                // Set fault code and fault string
                SOAPFault fault = soapBody.addFault();
                fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server"));
                fault.setFaultString("There is one error");
                SoapMessage message = new SaajSoapMessage(soapMessage);
                throw new SoapFaultClientException(message);
            }
        });

        // action
        mock.expectedMessageCount(0);

        try {
            producer.sendBody(REQUEST);
            fail("Target route was thrown exception.");
        } catch (CamelExecutionException ex) {
            assertRequestResponse(null,
                    "org.springframework.ws.soap.client.SoapFaultClientException: There is one error", soapFault);
        }
    }

    /**
     * Prepares target route to test saving of requests and responses from external system.
     *
     * @param targetUri the target URI
     * @param processor the callback response processor
     */
    private void prepareTargetRoute(final String targetUri, final @Nullable Processor processor) throws Exception {
        Assert.hasText(targetUri, "the targetUri must not be empty");

        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                Processor callbackProcessor = processor;

                if (callbackProcessor == null) {
                    callbackProcessor = new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            // result of communication with external system is in OUT part of Message
                            exchange.getOut().setBody(RESPONSE);
                        }
                    };
                }

                from(targetUri)
                        // simulates changes in exchange - it must be irrelevant
                        .removeHeader(RequestSendingEventNotifier.SAVE_REQ_HEADER)
                        .process(callbackProcessor);
            }
        };

        getCamelContext().addRoutes(route);
    }

    private void assertRequestResponse(@Nullable Message msg, @Nullable String failedRes,
                        @Nullable String resEnvelope) throws Exception {

        mock.assertIsSatisfied();

        Exchange exchange = null;
        if (!mock.getExchanges().isEmpty()) {
            exchange = mock.getExchanges().get(0);
        }

        // verify request/response in DB
        TypedQuery<Request> queryReq = em.createQuery("FROM " + Request.class.getName(), Request.class);
        List<Request> requests = queryReq.getResultList();

        assertThat(requests.size(), is(1));
        Request request = requests.get(0);
        assertThat(request.getUri(), is(TARGET_URI));
        assertThat(request.getRequest(), is(REQUEST));
        assertThat(request.getMessage(), is(msg));
        if (msg != null) {
            assertThat(request.getResponseJoinId(), is(msg.getCorrelationId()));
        } else if (exchange != null) {
            assertThat(request.getResponseJoinId(), is(exchange.getExchangeId()));
        }

        TypedQuery<Response> queryRes = em.createQuery("FROM " + Response.class.getName(), Response.class);
        List<Response> responses = queryRes.getResultList();

        assertThat(responses.size(), is(1));
        Response response = responses.get(0);
        if (failedRes == null) {
            assertThat(response.isFailed(), is(false));
            assertThat(response.getResponse(), is(RESPONSE));
        } else {
            assertThat(response.isFailed(), is(true));

            if (hasText(resEnvelope)) {
                Diff myDiff = new Diff(response.getResponse(), resEnvelope);
                assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
            } else {
                assertThat(response.getResponse(), nullValue());
            }

            assertThat(response.getFailedReason(), startsWith(failedRes));
        }

        assertThat(response.getRequest(), is(request));
    }
}
