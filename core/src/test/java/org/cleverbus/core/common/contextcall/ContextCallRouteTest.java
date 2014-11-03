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

package org.cleverbus.core.common.contextcall;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.cleverbus.api.exception.NoDataFoundException;
import org.cleverbus.core.AbstractCoreTest;
import org.cleverbus.test.ActiveRoutes;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test suite for {@link ContextCallRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = ContextCallRoute.class)
@ContextConfiguration(locations = {"classpath:/org/cleverbus/core/camel/common/contextcall/test-context.xml"})
public class ContextCallRouteTest extends AbstractCoreTest {

    private static final String CALL_ID = "callId";

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private ContextCallRegistry callRegistry;

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(ContextCallRoute.ROUTE_ID_CONTEXT_CALL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:test");

                        weaveAddLast().to("mock:test");
                    }
                });
    }

    @Before
    public void prepareCallRegistry() {
        callRegistry.addParams(CALL_ID, new ContextCallParams(TestService.class, "getGreeting", "Petr"));
    }

    @Test
    public void testCallWithParams() throws Exception {
        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, CALL_ID);

        mock.assertIsSatisfied();

        // verify response
        assertThat(callRegistry.getResponse(CALL_ID, String.class), is("Hello Petr"));
    }

    @Test
    public void testCallWithNoParams() throws Exception {
        String callId = "callId2";
        callRegistry.addParams(callId, new ContextCallParams(TestService.class, "getDefaultGreeting"));

        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, callId);

        mock.assertIsSatisfied();

        // verify response
        assertThat(callRegistry.getResponse(callId, String.class), is("Hello CleverBus"));
    }

    @Test
    public void testCallWithWrongCallId() throws Exception {
        try {
            producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, "23");
            fail("Call ID is wrong");
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause().getCause(), instanceOf(NoDataFoundException.class));
        }
    }
}
