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

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.BeanDefinition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;
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
    private NodeService nodeService;

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @EndpointInject(uri = "mock:starterPooling")
    private MockEndpoint mockStarterPooling;

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
                        weaveByType(BeanDefinition.class).replace().to(mockStarterPooling);
                    }
                });
    }

    @Test
    public void testRunningMode() throws Exception {
        assertThat(nodeService.getActualNode().isAbleToHandleNewMessages(), is(true));
        assertThat(nodeService.getActualNode().isAbleToHandleExistingMessages(), is(true));
        assertThat(nodeService.getActualNode().isStopped(), is(false));

        mock.expectedMessageCount(1);
        mockStarterPooling.expectedMessageCount(1);

        producer.sendBody("bodyContent");

        assertIsSatisfied(mock, mockStarterPooling);
    }

    @Test
    public void testProcessExistingMessage() throws Exception {
        nodeService.update(nodeService.getActualNode(), new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setHandleOnlyExistingMessageState();
            }
        });

        assertThat(nodeService.getActualNode().isAbleToHandleNewMessages(), is(false));
        assertThat(nodeService.getActualNode().isAbleToHandleExistingMessages(), is(true));
        assertThat(nodeService.getActualNode().isStopped(), is(false));

        mock.expectedMessageCount(1);
        mockStarterPooling.expectedMessageCount(1);

        producer.sendBody("bodyContent");

        assertIsSatisfied(mock, mockStarterPooling);
    }

    @Test
    public void testStopped() throws Exception {
        nodeService.update(nodeService.getActualNode(), new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setStoppedState();
            }
        });

        assertThat(nodeService.getActualNode().isAbleToHandleNewMessages(), is(false));
        assertThat(nodeService.getActualNode().isAbleToHandleExistingMessages(), is(false));
        assertThat(nodeService.getActualNode().isStopped(), is(true));

        mock.expectedMessageCount(1);
        mockStarterPooling.expectedMessageCount(0);

        producer.sendBody("bodyContent");

        assertIsSatisfied(mock, mockStarterPooling);
    }
}
