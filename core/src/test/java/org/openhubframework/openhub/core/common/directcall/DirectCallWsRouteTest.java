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

package org.openhubframework.openhub.core.common.directcall;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.test.ActiveRoutes;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test suite for {@link DirectCallWsRoute}.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = DirectCallWsRoute.class)
public class DirectCallWsRouteTest extends AbstractCoreTest {

    private static final String CALL_ID = "callId";

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private DirectCallRegistry callRegistry;

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(DirectCallWsRoute.ROUTE_ID_DIRECT_CALL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:test");

                        weaveById(DirectCallWsRoute.ROUTING_SLIP_ID).replace().to("mock:test");
                    }
                });
    }

    @Before
    public void prepareCallRegistry() {
        callRegistry.addParams(CALL_ID, new DirectCallParams("body", "uri", "senderRef", "soapAction", "header"));
    }

    @Test
    public void testExternalCall() throws Exception {
        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", DirectCallWsRoute.CALL_ID_HEADER, CALL_ID);

        mock.assertIsSatisfied();

        // verify response
        Exchange exchange = mock.getExchanges().get(0);
        assertThat((String)exchange.getIn().getBody(), is("body"));

        try {
            callRegistry.getParams(CALL_ID);
            fail("There should not be params anymore.");
        } catch (IllegalStateException ex) {}
    }

    @Test
    public void testExternalCallWithWrongCallId() throws Exception {
        try {
            producer.sendBodyAndHeader("empty", DirectCallWsRoute.CALL_ID_HEADER, "23");
            fail("Call ID is wrong");
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause().getCause(), instanceOf(IllegalStateException.class));
        }
    }
}
