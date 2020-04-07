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

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

import java.time.Instant;
import java.util.UUID;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.test.data.EntityTypeTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link AsynchInMessageRoute} - specific for guaranteed order delivery.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = {AsynchInMessageRoute.class, AsynchMessageRoute.class})
public class AsynchInMessageRouteGuaranteedOrderTest extends AbstractCoreDbTest {

    @Produce(uri = AsynchInMessageRoute.URI_GUARANTEED_ORDER_ROUTE)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private MessageService messageService;

    private static final String FUNNEL_VALUE = "774724557";

    private TransactionTemplate transactionTemplate;

    private Message firstMsg;

    @Before
    public void prepareMessage() throws Exception {
        // setup transactionTemplate
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        firstMsg = createMessage(FUNNEL_VALUE);
        firstMsg.setGuaranteedOrder(true);

        transactionTemplate.execute(status -> {
            em.persist(firstMsg);
            em.flush();
            return null;
        });
    }

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_GUARANTEED_ORDER)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });
    }

    private Message createMessage(String funnelValue) {
        Instant currDate = Instant.now();

        Message msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(UUID.randomUUID().toString());
        msg.setStartProcessTimestamp(currDate);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("payload");
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);
        msg.setFunnelValue(funnelValue); //=MSISDN

        return msg;
    }

    @Test
    public void testNoGuaranteedOrder() throws Exception {
        mock.setExpectedMessageCount(1);
        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(firstMsg.getMsgTimestamp().plusSeconds(100)); // be after "first" message
        msg.setState(MsgStateEnum.NEW);

        transactionTemplate.execute(status -> {
            em.persist(msg);
            em.flush();
            return null;
        });

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBody(msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.IN_QUEUE));
        Assert.assertThat(msg.getProcessingPriority(), CoreMatchers.is(AsynchInMessageRoute.NEW_MSG_PRIORITY));
    }

    @Test
    public void testGuaranteedOrder_onlyCurrentMessage() throws Exception {
        //set new state of message (message is new)
        firstMsg.setState(MsgStateEnum.NEW);

        transactionTemplate.execute(status -> {
            em.merge(firstMsg);
            return null;
        });

        mock.setExpectedMessageCount(1);

        // send one message only
        producer.sendBodyAndHeader(firstMsg, AsynchConstants.MSG_HEADER, firstMsg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, firstMsg.getMsgId()).getState(),
                CoreMatchers.is(MsgStateEnum.IN_QUEUE));
    }

    @Test
    public void testGuaranteedOrder_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(firstMsg.getMsgTimestamp().minusSeconds(100)); // be before "first" message
        msg.setState(MsgStateEnum.NEW);
        msg.setGuaranteedOrder(true);
        transactionTemplate.execute(status -> {
            em.persist(msg);
            em.flush();
            return null;
        });

        // send message has "msgTimestamp" before another processing message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.IN_QUEUE));
    }

    @Transactional
    @Test
    public void testGuaranteedOrder_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(firstMsg.getMsgTimestamp().plusSeconds(100)); // be after "first" message
        msg.setState(MsgStateEnum.NEW);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    private void executeInTransaction() {

    }
}
