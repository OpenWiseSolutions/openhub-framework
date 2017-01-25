/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.asynch.queue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.core.common.asynch.stop.StopService;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link PartlyFailedMessagesPoolRoute}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@ActiveRoutes(classes = PartlyFailedMessagesPoolRoute.class)
public class PartlyFailedMessagesPoolRouteTest extends AbstractCoreTest {

    @Autowired
    private StopService stopService;

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Configuration
    public static class TestContextConfig {
        // note: activation of Profile.PROD has negative side effects

        @Bean(name = PartlyFailedMessagesPoolRoute.ROUTE_BEAN)
        @Primary
        public PartlyFailedMessagesPoolRoute route() {
            return new PartlyFailedMessagesPoolRoute();
        }
    }

    @Before
    public void adjustRoute() throws Exception {
        getCamelContext().getRouteDefinition(PartlyFailedMessagesPoolRoute.ROUTE_ID)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:test");

                        weaveAddLast().to("mock:test");
                    }
                });
    }

    @Test
    public void testStoppingMode() throws Exception {
        assertThat(stopService.isStopping(), is(false));

        mock.expectedMessageCount(1);

        producer.sendBody("bodyContent");

        mock.assertIsSatisfied();
    }

    @Test
    public void testNotStoppingMode() throws Exception {
        stopService.stop();
        assertThat(stopService.isStopping(), is(true));

        mock.expectedMessageCount(1);

        producer.sendBody("bodyContent");

        mock.assertIsSatisfied();
    }
}
