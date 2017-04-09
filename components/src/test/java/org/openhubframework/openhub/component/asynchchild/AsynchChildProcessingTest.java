package org.openhubframework.openhub.component.asynchchild;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.StopDefinition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.component.AbstractComponentsDbTest;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.test.data.EntityTypeTestEnum;
import org.openhubframework.openhub.test.data.ErrorTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;

/**
 * Test for processing async child message.
 *
 * @author Roman Havlicek
 * @since 2.0
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
public class AsynchChildProcessingTest extends AbstractComponentsDbTest {

    @Produce(uri = AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AsynchMessageRoute asynchMessageRoute;

    private Message msg;

    @Before
    public void prepareMessage() throws Exception {
        Instant currDate = Instant.now();

        msg = new Message();
        msg.setState(MsgStateEnum.NEW);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.ACCOUNT);
        msg.setOperationName("newAccount");
        msg.setPayload("payload");
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);

        messageService.insertMessage(msg);
        messageService.setStateInQueueForLock(msg);
    }

    /**
     * Test for processing message with one child messages.
     *
     * @throws Exception all errors
     */
    @Test
    public void testOneChildMessageSuccess() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_SYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });

        RouteBuilder routeBuilder = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "newAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":deleteAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "deleteAccount"))
                        .delay(300)
                        .log(LoggingLevel.INFO, "Success");
            }
        };
        getCamelContext().addRoutes(routeBuilder);

        mock.setExpectedMessageCount(2);

        // send message
        producer.sendBodyAndHeader(AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG, msg, AsynchConstants.MSG_HEADER, msg);

        countDownLatch.await();

        assertIsSatisfied(mock);

        Message childMessage = messageService.findEagerMessageById(2l);
        assertThat(childMessage.getState(), is(MsgStateEnum.OK));

        Message parentMessage = messageService.findEagerMessageById(msg.getMsgId());
        assertThat(parentMessage.getState(), is(MsgStateEnum.OK));
    }

    /**
     * Test for processing message with two child messages.
     *
     * @throws Exception all errors
     */
    @Test
    public void testTwoChildMessageSuccess() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_SYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast()
                                .to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });

        RouteBuilder routeBuilder = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "newAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":deleteAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "deleteAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":createAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "createAccount"))
                        .delay(300)
                        .log(LoggingLevel.INFO, "Success");
            }
        };
        getCamelContext().addRoutes(routeBuilder);

        mock.setExpectedMessageCount(3);

        // send message
        producer.sendBodyAndHeader(AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG, msg, AsynchConstants.MSG_HEADER, msg);

        countDownLatch.await();

        assertIsSatisfied(mock);

        Message firstChidlMessage = messageService.findEagerMessageById(2l);
        assertThat(firstChidlMessage.getState(), is(MsgStateEnum.OK));

        Message secondChildMessage = messageService.findEagerMessageById(3l);
        assertThat(secondChildMessage.getState(), is(MsgStateEnum.OK));

        Message parentMessage = messageService.findEagerMessageById(msg.getMsgId());
        assertThat(parentMessage.getState(), is(MsgStateEnum.OK));
    }

    /**
     * Test for processing message with three child messages.
     *
     * @throws Exception all errors
     */
    @Test
    public void testThreeChildMessageSuccess() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(4);

        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_SYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast()
                                .to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });

        RouteBuilder routeBuilder = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "newAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":deleteAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "deleteAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":createAccount?bindingType=HARD")
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":findAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "createAccount"))
                        .delay(300)
                        .log(LoggingLevel.INFO, "Success");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "findAccount"))
                        .delay(600)
                        .log(LoggingLevel.INFO, "Success");
            }
        };
        getCamelContext().addRoutes(routeBuilder);

        mock.setExpectedMessageCount(4);

        // send message
        producer.sendBodyAndHeader(AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG, msg, AsynchConstants.MSG_HEADER, msg);

        countDownLatch.await();

        assertIsSatisfied(mock);

        Message firstChidlMessage = messageService.findEagerMessageById(2l);
        assertThat(firstChidlMessage.getState(), is(MsgStateEnum.OK));

        Message secondChildMessage = messageService.findEagerMessageById(3l);
        assertThat(secondChildMessage.getState(), is(MsgStateEnum.OK));

        Message thirdChildMessage = messageService.findEagerMessageById(4l);
        assertThat(thirdChildMessage.getState(), is(MsgStateEnum.OK));

        Message parentMessage = messageService.findEagerMessageById(msg.getMsgId());
        assertThat(parentMessage.getState(), is(MsgStateEnum.OK));
    }

    /**
     * Test for processing message with two child messages and parent message failed.
     *
     * @throws Exception all errors
     */
    @Test
    public void testTwoChildMessageFailed() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        setPrivateField(asynchMessageRoute, "countPartlyFailsBeforeFailed", new FixedConfigurationItem<>(0));

        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_SYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast()
                                .to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });
        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_ASYNCH_ERROR_HANDLING)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast()
                                .to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });
        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_ERROR_FATAL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByType(StopDefinition.class).before()
                                .to(mock)
                                .process(new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                });

        RouteBuilder routeBuilder = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "newAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":deleteAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "deleteAccount"))
                        .to("asynch-child:" + ServiceTestEnum.ACCOUNT + ":createAccount?bindingType=HARD");

                from("direct:" + getOutRouteId(ServiceTestEnum.ACCOUNT, "createAccount"))
                        .delay(300)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                throw new IntegrationException(ErrorTestEnum.E200, "Processing message failed");
                            }
                        });
            }
        };
        getCamelContext().addRoutes(routeBuilder);

        mock.setExpectedMessageCount(3);

        // send message
        producer.sendBodyAndHeader(AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG, msg, AsynchConstants.MSG_HEADER, msg);

        countDownLatch.await();

        assertIsSatisfied(mock);

        Message firstChidlMessage = messageService.findEagerMessageById(2l);
        assertThat(firstChidlMessage.getState(), is(MsgStateEnum.FAILED));

        Message secondChildMessage = messageService.findEagerMessageById(3l);
        assertThat(secondChildMessage.getState(), is(MsgStateEnum.FAILED));

        Message parentMessage = messageService.findEagerMessageById(msg.getMsgId());
        assertThat(parentMessage.getState(), is(MsgStateEnum.FAILED));
    }
}
