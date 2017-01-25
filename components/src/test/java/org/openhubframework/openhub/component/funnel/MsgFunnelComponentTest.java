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

package org.openhubframework.openhub.component.funnel;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

import java.util.Date;
import java.util.UUID;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
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
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.component.AbstractComponentsDbTest;
import org.openhubframework.openhub.test.data.EntityTypeTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;


/**
 * Test suite for {@link MsgFunnelComponent}.
 *
 * @author Petr Juza
 */
@Transactional
public class MsgFunnelComponentTest extends AbstractComponentsDbTest {

    private static final String MSG_BODY = "some body";
    private static final String FUNNEL_VALUE = "774724557";
    private static final String FUNNEL_ID = "myFunnelId";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Produce(uri = "direct:startGuaranteed")
    private ProducerTemplate producerForGuaranteed;

    @Produce(uri = "direct:startGuaranteedWithoutFailed")
    private ProducerTemplate producerForGuaranteedWithoutFailed;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message firstMsg;

    @Before
    public void prepareMessage() throws Exception {
        firstMsg = createMessage(FUNNEL_VALUE);

        em.persist(firstMsg);
        em.flush();
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
        msg.setFunnelComponentId(FUNNEL_ID);

        return msg;
    }

    @Before
    public void prepareRoutes() throws Exception {
        RouteBuilder defaultRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("msg-funnel:default?idleInterval=50&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(defaultRoute);

        RouteBuilder guaranteedRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startGuaranteed")
                    .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedRoute);

        RouteBuilder guaranteedWithoutFailedRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startGuaranteedWithoutFailed")
                    .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&excludeFailedState=true&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedWithoutFailedRoute);
    }

    @Test
    public void testFunnel() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(FUNNEL_VALUE);
        em.persist(msg);
        em.flush();

        // send message with same funnel value => postpone it
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testFunnel_waitingForResponse() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setStartProcessTimestamp(DateUtils.addSeconds(new Date(), -MsgFunnelEndpoint.DEFAULT_IDLE_INTERVAL - 100));
        msg.setState(MsgStateEnum.WAITING_FOR_RES);
        em.persist(msg);
        em.flush();

        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(),
                CoreMatchers.is(MsgStateEnum.WAITING_FOR_RES));
    }

    @Test
    public void testFunnel_noFilter() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage("777123456");

        // send message with different funnel value
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();
    }

    @Test
    public void testWithoutFunnel() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(null);

        // input message doesn't have funnel value
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();
    }

    @Test
    public void testFunnelForGuaranteedOrder_onlyCurrentMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        // send one message only
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, firstMsg);

        assertIsSatisfied(mock);
    }

    @Test
    public void testFunnelForGuaranteedOrder_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testFunnelForGuaranteedOrder_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testFunnelForGuaranteedOrder_excludeFailedState() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message, but FAILED
        msg.setState(MsgStateEnum.FAILED);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message but in FAILED state
        //  that is excluded => continue
        producerForGuaranteedWithoutFailed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.FAILED));
    }
}
