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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.annotation.Nullable;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.StopDefinition;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.camel.util.concurrent.SynchronousExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.msg.ChildMessage;
import org.openhubframework.openhub.api.asynch.msg.MessageSplitterCallback;
import org.openhubframework.openhub.api.asynch.msg.MsgSplitter;
import org.openhubframework.openhub.api.entity.*;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.event.AbstractAsynchEvent;
import org.openhubframework.openhub.api.event.CompletedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.FailedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.PartlyFailedMsgAsynchEvent;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.asynch.msg.MessageSplitterImpl;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;
import org.openhubframework.openhub.test.TestCamelUtils;
import org.openhubframework.openhub.test.data.EntityTypeTestEnum;
import org.openhubframework.openhub.test.data.ErrorTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link AsynchMessageRoute}.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
@Transactional
public class AsynchMessageRouteTest extends AbstractCoreDbTest {

    private static final String REQUEST_XML =
              "  <cus:setCustomerRequest xmlns=\"http://openhubframework.org/ws/Customer-v1\""
            + "         xmlns:cus=\"http://openhubframework.org/ws/CustomerService-v1\">"
            + "         <cus:customer>"
            + "            <externalCustomerID>12</externalCustomerID>"
            + "            <customerNo>23</customerNo>"
            + "            <customerTypeID>2</customerTypeID>"
            + "            <lastName>Juza</lastName>"
            + "            <firstName>Petr</firstName>"
            + "         </cus:customer>"
            + "  </cus:setCustomerRequest>";

    private static final String SEDA_URI = "seda:test_seda?queueFactory=#" + AsynchConstants.PRIORITY_QUEUE_FACTORY;

    @Produce(uri = AsynchConstants.URI_ASYNC_MSG)
    private ProducerTemplate producerAsyncIn;

    @Produce(uri = AsynchMessageRoute.URI_SYNC_MSG)
    private ProducerTemplate producerSyncMsg;

    @Produce(uri = SEDA_URI)
    private ProducerTemplate producerSeda;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private TransactionTemplate transactionTemplate;

    private Message msg;

    private String subRouteUri;

    private String msg1SubRouteUri;

    private String msg2SubRouteUri;

    private List<ChildMessage> childMessages;

    private MsgEventNotifierSupport eventListener = new MsgEventNotifierSupport();

    @Autowired
    private AsynchMessageRoute asynchMessageRoute;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NodeService nodeService;

    @Before
    public void prepareData() throws Exception {
        // message
        Instant currDate = Instant.now();

        msg = new Message();
        msg.setState(MsgStateEnum.IN_QUEUE);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload(REQUEST_XML);
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);

        subRouteUri = "direct:" + msg.getService().getServiceName() + "_" + msg.getOperationName()
                + AbstractBasicRoute.OUT_ROUTE_SUFFIX;

        // create child messages
        childMessages = new ArrayList<ChildMessage>();

        ChildMessage msg1 = new ChildMessage(msg, ServiceTestEnum.ACCOUNT, "setAccount", "body1");
        childMessages.add(msg1);

        msg1SubRouteUri = "direct:" + msg1.getService() + "_" + msg1.getOperationName()
                + AbstractBasicRoute.OUT_ROUTE_SUFFIX;

        ChildMessage msg2 = new ChildMessage(msg, ServiceTestEnum.CUSTOMER, "setCustomerExt", "body2");
        childMessages.add(msg2);

        msg2SubRouteUri = "direct:" + msg2.getService() + "_" + msg2.getOperationName()
                + AbstractBasicRoute.OUT_ROUTE_SUFFIX;
    }

    @Before
    public void prepareTransactionTemplate() throws Exception {
        // setup transactionTemplate
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * Successful processing {@link Message} with input into {@link AsynchMessageRoute#URI_SYNC_MSG}.
     */
    @Test
    public void testSyncOutOk() throws Exception {
        // setCustomer route definition
        RouteBuilder setCustomerRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                addEventNotifier(eventListener);

                from(subRouteUri)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            // verify object ID
                            assertThat((String) exchange.getIn().getHeader(AsynchConstants.OBJECT_ID_HEADER),
                                    is(msg.getObjectId()));
                            assertThat((EntityTypeExtEnum) exchange.getIn().getHeader(AsynchConstants.ENTITY_TYPE_HEADER),
                                    is(msg.getEntityType()));

                            // nothing to do = successful processing

                            exchange.setProperty(AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX,
                                    new IntegrationException(ErrorTestEnum.E300));
                        }
                    });
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(msgDB.getBusinessError(), containsString(ErrorTestEnum.E300.getErrDesc()));
        assertThat(eventListener.getEvent(), instanceOf(CompletedMsgAsynchEvent.class));
    }

    /**
     * Successful processing {@link Message} with input into {@link AsynchConstants#URI_ASYNC_MSG}.
     */
    @Test
    public void testAsyncInOk() throws Exception {
        msg.setState(MsgStateEnum.NEW);
        // save message into DB
        messageService.insertMessage(msg);

        // send message
        producerAsyncIn.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        transactionTemplate.execute(status -> {
            // verify message
            Message msgDB = em.find(Message.class, msg.getMsgId());
            assertThat(msgDB, notNullValue());
            assertThat(msgDB.getState(), is(MsgStateEnum.IN_QUEUE));
            assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
            assertThat(msgDB.getStartInQueueTimestamp(), notNullValue());
            assertThat(msgDB.getStartProcessTimestamp(), nullValue());
            assertThat(msgDB.getBusinessError(), nullValue());
            assertThat(eventListener.getEvent(), nullValue());

            return null;
        });
    }

    /**
     * Partly failed - unexpected exception.
     */
    @Test
    public void testPartlyFailed() throws Exception {
        final String customData = "customData";

        // setCustomer route definition
        RouteBuilder setCustomerRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                addEventNotifier(eventListener);

                from(subRouteUri)
                    .errorHandler(noErrorHandler())
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            exchange.setProperty(AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX,
                                    new IntegrationException(ErrorTestEnum.E300));

                            exchange.setProperty(AsynchConstants.CUSTOM_DATA_PROP, customData);

                            throw new IntegrationException(ErrorTestEnum.E300, null,
                                    new IllegalArgumentException("validation error"));
                        }
                    });
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(msgDB.getFailedErrorCode().getErrorCode(), is(ErrorTestEnum.E300.getErrorCode()));
        assertThat(msgDB.getFailedDesc(), notNullValue());
        assertThat(msgDB.getFailedCount(), is(1));
        assertThat(msgDB.getCustomData(), is(customData));
        assertThat(msgDB.getBusinessError(), containsString(ErrorTestEnum.E300.getErrDesc()));
        assertThat(eventListener.getEvent(), instanceOf(PartlyFailedMsgAsynchEvent.class));
    }

    /**
     * Failed - unexpected exception that exceeds limit for next processing.
     */
    @Test
    public void testFailed() throws Exception {
        // set failed limit
        setPrivateField(asynchMessageRoute, "countPartlyFailsBeforeFailed", new FixedConfigurationItem<>(2));

        // route definition that throws exception
        RouteBuilder exRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                addEventNotifier(eventListener);

                from(subRouteUri)
                    .errorHandler(noErrorHandler())
                    .process(TestCamelUtils.throwException(new IntegrationException(ErrorTestEnum.E300)));
            }
        };

        getCamelContext().addRoutes(exRoute);

        getCamelContext().getRouteDefinition(AsynchMessageRoute.ROUTE_ID_ERROR_FATAL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByType(StopDefinition.class).before().to(mock);
                    }
                });

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        mock.expectedMessageCount(0);
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);
        mock.assertIsSatisfied();

        Message msgDB = em.find(Message.class, msg.getMsgId());
        msgDB.setState(MsgStateEnum.IN_QUEUE);

        mock.expectedMessageCount(0);
        producerSyncMsg.sendBodyAndHeader(msgDB, AsynchConstants.MSG_HEADER, msgDB);
        mock.assertIsSatisfied();

        msgDB = em.find(Message.class, msg.getMsgId());
        msgDB.setState(MsgStateEnum.IN_QUEUE);

        mock.expectedMessageCount(1);
        producerSyncMsg.sendBodyAndHeader(msgDB, AsynchConstants.MSG_HEADER, msgDB);
        mock.assertIsSatisfied();

        // verify message
        msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.FAILED));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(msgDB.getFailedErrorCode().getErrorCode(), is(ErrorTestEnum.E300.getErrorCode()));
        assertThat(msgDB.getFailedDesc(), notNullValue());
        assertThat(msgDB.getFailedCount(), is(3));
        assertThat(eventListener.getEvent(), instanceOf(FailedMsgAsynchEvent.class));
    }

    /**
     * Failed - validation exception occurs.
     */
    @Test
    public void testFailed_ValidationException() throws Exception {
        // setCustomer route definition
        class SetCustomerRouteBuilder extends AbstractBasicRoute {

            @Override
            protected void doConfigure() throws Exception {
                from(subRouteUri)
                    .process(TestCamelUtils.throwException(new org.openhubframework.openhub.api.exception.validation.ValidationException(
                            InternalErrorEnum.E102)));
            }
        };

        getCamelContext().addRoutes(new SetCustomerRouteBuilder());

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertErrorCode(msgDB.getFailedErrorCode(), InternalErrorEnum.E102);
        assertThat(msgDB.getFailedDesc(), notNullValue());
        assertThat(msgDB.getFailedCount(), is(1));
    }

    /**
     * Partly failed - IO exception with redelivery.
     */
    @Test
    public void testPartlyFailedWithRedelivery() throws Exception {
        // set failed limit
        setPrivateField(asynchMessageRoute, "countPartlyFailsBeforeFailed", new FixedConfigurationItem<>(2));

        // setCustomer route definition
        RouteBuilder setCustomerRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(subRouteUri)
                    .errorHandler(noErrorHandler())
                    .process(TestCamelUtils.throwException(new IOException()));
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertErrorCode(msgDB.getFailedErrorCode(), InternalErrorEnum.E103);
        assertThat(msgDB.getFailedDesc(), notNullValue());
        assertThat(msgDB.getFailedCount(), is(1));
    }

    /**
     * Successful processing - parent message is divided into child messages.
     */
    @Test
    public void testOk_messageSplitter() throws Exception {
        // setCustomer route definition
        RouteBuilder setCustomerRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                MessageSplitterCallback splitterCallback = new MessageSplitterCallback() {
                    @Override
                    public List<ChildMessage> getChildMessages(Message parentMsg, Object body) {
                        return childMessages;
                    }
                };

                MsgSplitter messageSplitter = new MessageSplitterImpl(messageService, getCamelContext(), splitterCallback);
                setPrivateField(messageSplitter, "executor", new SynchronousExecutorService());

                from(subRouteUri)
                    .bean(messageSplitter);
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // route definitions for child messages
        RouteBuilder msg1Route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(msg1SubRouteUri)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            // nothing to do
                            exchange.setProperty(AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX,
                                    new IntegrationException(ErrorTestEnum.E300));
                        }
                    });
            }
        };

        getCamelContext().addRoutes(msg1Route);

        RouteBuilder msg2Route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(msg2SubRouteUri)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            // nothing to do
                        }
                    });
            }
        };

        getCamelContext().addRoutes(msg2Route);


        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(msgDB.getBusinessError(), containsString(ErrorTestEnum.E300.getErrDesc()));
    }


    /**
     * Failed processing - parent message is divided into child messages.
     */
    @Test
    public void testFailed_messageSplitter() throws Exception {
        // set failed limit
        setPrivateField(asynchMessageRoute, "countPartlyFailsBeforeFailed", new FixedConfigurationItem<>(0));

        // setCustomer route definition
        RouteBuilder setCustomerRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                MessageSplitterCallback splitterCallback = new MessageSplitterCallback() {
                    @Override
                    public List<ChildMessage> getChildMessages(Message parentMsg, Object body) {
                        return childMessages;
                    }
                };

                MsgSplitter messageSplitter = new MessageSplitterImpl(messageService, getCamelContext(), splitterCallback);
                setPrivateField(messageSplitter, "executor", new SynchronousExecutorService());

                from(subRouteUri)
                    .bean(messageSplitter);
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // route definitions for child messages
        RouteBuilder msg1Route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(msg1SubRouteUri)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            // nothing to do
                        }
                    });
            }
        };

        getCamelContext().addRoutes(msg1Route);

        RouteBuilder msg2Route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(msg2SubRouteUri)
                    .errorHandler(noErrorHandler())
                    .process(TestCamelUtils.throwException(new IntegrationException(ErrorTestEnum.E300)));
            }
        };

        getCamelContext().addRoutes(msg2Route);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.FAILED));
        assertThat(msgDB.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(msgDB.getFailedErrorCode().getErrorCode(), is(ErrorTestEnum.E300.getErrorCode()));
        assertThat(msgDB.getFailedDesc(), notNullValue());
        assertThat(msgDB.getFailedCount(), is(1));
    }

    /**
     * Test stopping of processing obsolete message.
     */
    @Test
    public void testStopProcessingObsoleteMessage() throws Exception {
        RouteBuilder route = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                addEventNotifier(eventListener);

                from(subRouteUri)
                    .log("Starts test route ...");
            }
        };

        getCamelContext().addRoutes(route);

        // save message into DB
        msg.setState(MsgStateEnum.PARTLY_FAILED);
        em.persist(msg);
        em.flush();
        em.clear();

        // send message
        msg.setState(MsgStateEnum.IN_QUEUE);
        msg.setLastUpdateTimestamp(Instant.now());
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // check there is no processing event
        assertThat(eventListener.getEvent(), nullValue());
    }

    /**
     * Test SEDA with PriorityBlockingQueue and comparator.
     */
    @Test
    public void testSeda() throws Exception {
        RouteBuilder route = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from(SEDA_URI)
                    .log(LoggingLevel.DEBUG, "SEDA priority: ${body.processingPriority}");
            }
        };

        getCamelContext().addRoutes(route);

        for (int i = 0; i < 5; i++) {
            Message msg = new Message();
            msg.setProcessingPriority(i);

            producerSeda.sendBody(msg);
        }
    }

    /**
     * Test when actual node process only existing message ({@link NodeState#HANDLES_EXISTING_MESSAGES}).
     */
    @Test
    public void testNodeProcessExistingMessage() throws Exception {
        nodeService.update(nodeService.getActualNode(), new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setHandleOnlyExistingMessageState();
            }
        });

        RouteBuilder setCustomerRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from(subRouteUri)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                            }
                        });
            }
        };

        getCamelContext().addRoutes(setCustomerRoute);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK));
        assertThat(msgDB.getFailedErrorCode(), nullValue());
    }

    /**
     * Test when actual node is stopped ({@link NodeState#STOPPED}).
     */
    @Test
    public void testNodeStopped() throws Exception {
        nodeService.update(nodeService.getActualNode(), new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setStoppedState();
            }
        });

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producerSyncMsg.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.FAILED));
        assertThat(msgDB.getFailedErrorCode().getErrorCode(), containsString(InternalErrorEnum.E119.getErrorCode()));
    }


    /**
     * Event listener.
     */
    public static class MsgEventNotifierSupport extends EventNotifierSupport {

        private AbstractAsynchEvent event;

        @Override
        public void notify(EventObject event) throws Exception {
            this.event = (AbstractAsynchEvent) event;
        }

        @Override
        public boolean isStarted() {
            return true;
        }

        @Override
        public boolean isEnabled(EventObject event) {
            return event instanceof AbstractAsynchEvent;
        }

        @Nullable
        public AbstractAsynchEvent getEvent() {
            return event;
        }
    }
}
