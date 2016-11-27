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

import java.util.Date;
import java.util.UUID;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.test.EntityTypeTestEnum;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link AsynchInMessageRoute} - specific for guaranteed order delivery.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = AsynchInMessageRoute.class)
@Transactional
public class AsynchInMessageRouteGuaranteedOrderTest extends AbstractCoreDbTest {

    @Produce(uri = AsynchInMessageRoute.URI_GUARANTEED_ORDER_ROUTE)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private static final String FUNNEL_VALUE = "774724557";

    private Message firstMsg;

    @Before
    public void prepareMessage() throws Exception {
        firstMsg = createMessage(FUNNEL_VALUE);
        firstMsg.setGuaranteedOrder(true);

        em.persist(firstMsg);
        em.flush();
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
        Date currDate = new Date();

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
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBody(msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
        Assert.assertThat(msg.getProcessingPriority(), CoreMatchers.is(AsynchInMessageRoute.NEW_MSG_PRIORITY));
    }

    @Test
    public void testGuaranteedOrder_onlyCurrentMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        // send one message only
        producer.sendBodyAndHeader(firstMsg, AsynchConstants.MSG_HEADER, firstMsg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, firstMsg.getMsgId()).getState(),
                CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testGuaranteedOrder_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testGuaranteedOrder_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }
}
