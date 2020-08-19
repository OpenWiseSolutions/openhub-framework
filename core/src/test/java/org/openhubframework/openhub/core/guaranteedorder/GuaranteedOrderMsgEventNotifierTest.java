/*
 * Copyright 2020 the original author or authors.
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

package org.openhubframework.openhub.core.guaranteedorder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.event.AsynchEventHelper;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link GuaranteedOrderMsgEventNotifier}.
 *
 * @author Michal Sabol
 */
public class GuaranteedOrderMsgEventNotifierTest extends AbstractCoreDbTest {

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private GuaranteedOrderMsgEventNotifier eventNotifier;

    @Before
    public void prepareData() {
        setPrivateField(eventNotifier, "targetURI", "mock:test");

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                insertNewMessage("1234_second", MsgStateEnum.POSTPONED, Instant.now());
                insertNewMessage("1234_first", MsgStateEnum.POSTPONED, Instant.now().minusSeconds(150));
            }
        });
    }

    @Test
    public void testDoNotify_ok() throws Exception {
        final Message inputMsg = getMessage("1234", MsgStateEnum.OK, Instant.now());

        // prepare exchange for event
        final ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(producerTemplate.getCamelContext());
        final Exchange exchange = exchangeBuilder.build();
        exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, inputMsg);

        mock.expectedMessageCount(1);

        // publish event
        AsynchEventHelper.notifyGuaranteedOrderMsgCompleted(exchange);

        mock.assertIsSatisfied();

        // verify message
        Message nextMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(nextMsg, notNullValue());
        assertThat(nextMsg.getCorrelationId(), is("1234_first"));
        assertThat(nextMsg.getPayload(), is("xml"));
        assertThat(nextMsg.getSourceSystem().getSystemName(), is("CRM"));
        assertThat(nextMsg.getService().getServiceName(), is("CUSTOMER"));
        assertThat(nextMsg.getOperationName(), is("setCustomer"));
        assertThat(nextMsg.getFunnelValue(), is("funnelValue"));
        assertThat(nextMsg.isGuaranteedOrder(), is(true));
    }

    @Test
    public void testDoNotify_noNextMsg() throws Exception {
        final Message inputMsg = getMessage("1234", MsgStateEnum.OK, Instant.now());
        inputMsg.setFunnelValue("anotherFunnel");

        // prepare exchange for event
        final ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(producerTemplate.getCamelContext());
        final Exchange exchange = exchangeBuilder.build();
        exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, inputMsg);

        mock.expectedMessageCount(0);

        // publish event
        AsynchEventHelper.notifyGuaranteedOrderMsgCompleted(exchange);

        mock.assertIsSatisfied();
    }

    private Message insertNewMessage(String correlationId, MsgStateEnum state, Instant msgTimestamp) {
        final Message msg = getMessage(correlationId, state, msgTimestamp);

        em.persist(msg);
        em.flush();

        return msg;
    }

    private Message getMessage(String correlationId, MsgStateEnum state, Instant msgTimestamp) {
        final Instant currDate = Instant.now();

        final Message msg = new Message();
        msg.setState(state);
        msg.setMsgTimestamp(msgTimestamp);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);
        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);
        msg.setFunnelValue("funnelValue");
        msg.setGuaranteedOrder(true);
        msg.setPayload("xml");

        return msg;
    }
}
