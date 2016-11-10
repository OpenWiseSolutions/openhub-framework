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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

import org.openhubframework.openhub.api.asynch.model.TraceHeader;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.ValidationIntegrationException;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.core.common.validator.TraceIdentifierValidator;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Test suite for {@link TraceHeaderProcessor}.
 *
 * @author Petr Juza
 */
public class TraceHeaderProcessorTest extends AbstractCoreTest {

    @Produce(uri = "direct:testRoute")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    public void prepareRoute(final TraceHeaderProcessor processor) throws Exception {

        RouteBuilder testRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:testRoute")
                    .process(processor)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(testRoute);
    }

    @Test
    public void testParsingTraceHeaderFromBody() throws Exception {
        prepareRoute(new TraceHeaderProcessor(true, null));

        String request = "<notifyCollectionStepRequest xmlns=\"http://openhubframework.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://openhubframework.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        mock.expectedMessageCount(1);

        // send message
        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), notNullValue());

        TraceHeader header = exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER, TraceHeader.class);
        assertThat(header.getTraceIdentifier().getCorrelationID(), is("da793349-b486-489a-9180-200789b7007f"));
        assertThat(header.getTraceIdentifier().getApplicationID(), is("ERP"));
        assertThat(header.getTraceIdentifier().getProcessID(), is("process123"));
    }

    @Test
    public void testValidateTraceIdNotAllowedValues() throws Exception {
        final TraceHeaderProcessor processor = new TraceHeaderProcessor(true,
                Collections.<TraceIdentifierValidator>singletonList(
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return false;
                            }
                        }));
        prepareRoute(processor);

        String request = "<notifyCollectionStepRequest xmlns=\"http://openhubframework.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://openhubframework.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        // send message
        try {
            producer.sendBody(request);
            fail("request must be rejected since traceId does not have the valid value");
        } catch (Exception ex) {
            Throwable origExp = ex.getCause();
            assertThat(origExp, instanceOf(ValidationIntegrationException.class));
            assertThat(((ValidationIntegrationException) origExp).getError().getErrorCode(),
                    is(InternalErrorEnum.E120.getErrorCode()));
        }
    }

    @Test
    public void testValidateTraceIdAllowedValues() throws Exception {
        final TraceHeaderProcessor processor = new TraceHeaderProcessor(true,
                Collections.<TraceIdentifierValidator>unmodifiableList(Arrays.asList(
                        // all is invalid
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return false;
                            }
                        },
                        // all is valid
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return true;
                            }
                        }
                )));
        prepareRoute(processor);

        String request = "<notifyCollectionStepRequest xmlns=\"http://openhubframework.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://openhubframework.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        // send message
        mock.expectedMessageCount(1);

        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), notNullValue());

    }

    @Test
    public void testParsingNoTraceHeader() throws Exception {
        prepareRoute(new TraceHeaderProcessor(false, null));

        String request = "<notifyCollectionStepRequest xmlns=\"http://openhubframework.org/ws/NotificationsService-v1\">"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "        </notifyCollectionStepRequest>";

        mock.expectedMessageCount(1);

        // send message
        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), nullValue());
    }
}
