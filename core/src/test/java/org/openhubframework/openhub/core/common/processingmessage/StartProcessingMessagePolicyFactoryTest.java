package org.openhubframework.openhub.core.common.processingmessage;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.openhubframework.openhub.api.configuration.CoreProps.URI_INPUT_PATTERN_FILTER;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.core.common.asynch.ExceptionTranslationRoute;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;

/**
 * Test for {@link StartProcessingMessagePolicyFactory}.
 *
 * @author Roman Havlicek
 * @see StartProcessingMessagePolicyFactory
 * @since 2.0
 */
@ActiveRoutes(classes = ExceptionTranslationRoute.class)
@TestPropertySource(properties = {URI_INPUT_PATTERN_FILTER + "=direct:inputUri"})
public class StartProcessingMessagePolicyFactoryTest extends AbstractCoreDbTest {

    @EndpointInject(uri = "mock:inputRoute")
    private MockEndpoint inputRouteMock;

    @EndpointInject(uri = "mock:inputUri")
    private MockEndpoint inputUriMock;

    @EndpointInject(uri = "mock:outputRoute")
    private MockEndpoint outputRouteMock;

    @Produce
    private ProducerTemplate producer;

    @Autowired
    private NodeService nodeService;

    /**
     * Init routes for test.
     *
     * @throws Exception all errors
     */
    @Before
    public void init() throws Exception {
        getCamelContext().addRoutes(new AbstractBasicRoute() {
            @Override
            protected void doConfigure() throws Exception {
                from("direct:inputRoute")
                        .routeId(getInRouteId(ServiceTestEnum.ACCOUNT, "input"))
                        .to(inputRouteMock);

                from("direct:inputUri")
                        .routeId(getRouteId(ServiceTestEnum.CUSTOMER, "create"))
                        .to(inputUriMock);

                from("direct:outputRoute")
                        .routeId(getExternalRouteId(ExternalSystemTestEnum.BILLING, "output"))
                        .to(outputRouteMock);
            }
        });
    }

    /**
     * Test when actual node is running.
     *
     * @throws Exception all errors
     */
    @Test
    public void testNodeRun() throws Exception {
        inputRouteMock.setExpectedMessageCount(1);
        inputUriMock.setExpectedMessageCount(1);
        outputRouteMock.setExpectedMessageCount(1);

        producer.sendBody("direct:inputRoute", "Body");
        producer.sendBody("direct:inputUri", "Body");
        producer.sendBody("direct:outputRoute", "Body");

        assertIsSatisfied(inputRouteMock, inputUriMock, outputRouteMock);
    }

    /**
     * Test when actual node is stopped.
     *
     * @throws Exception all errors
     */
    @Test
    public void testNodeStop() throws Exception {
        nodeService.update(nodeService.getActualNode(), new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setStoppedState();
            }
        });

        inputRouteMock.setExpectedMessageCount(0);
        inputUriMock.setExpectedMessageCount(0);
        outputRouteMock.setExpectedMessageCount(1);

        try {
            producer.sendBody("direct:inputRoute", "Body");
        } catch (Exception e) {
            assertThat(e.getCause(), instanceOf(IntegrationException.class));
        }
        try {
            producer.sendBody("direct:inputUri", "Body");
        } catch (Exception e) {
            assertThat(e.getCause(), instanceOf(IntegrationException.class));
        }
        producer.sendBody("direct:outputRoute", "Body");

        assertIsSatisfied(inputRouteMock, inputUriMock, outputRouteMock);
    }
}