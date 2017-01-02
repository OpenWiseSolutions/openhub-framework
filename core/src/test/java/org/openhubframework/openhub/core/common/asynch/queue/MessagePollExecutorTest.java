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

package org.openhubframework.openhub.core.common.asynch.queue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.annotation.Nullable;
import javax.persistence.Query;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.joda.time.Seconds;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link MessagePollExecutor}.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
public class MessagePollExecutorTest extends AbstractCoreDbTest {

    private static final String FUNNEL_VALUE = "774724557";

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private MessagesPool messagesPool;

    @Autowired
    private MessagePollExecutor messagePollExecutor;

    @Before
    public void prepareData() {
        // set failed limit
        setPrivateField(messagesPool, "partlyFailedInterval", new FixedConfigurationItem<>(Seconds.ZERO));
        setPrivateField(messagesPool, "postponedInterval", new FixedConfigurationItem<>(Seconds.ZERO));
        setPrivateField(messagePollExecutor, "targetURI", "mock:test");
        setPrivateField(messagePollExecutor, "postponedIntervalWhenFailed", new FixedConfigurationItem<>(Seconds.ZERO));

        // firstly commit messages to DB (we can commit because we have embedded DB for tests only)
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                insertNewMessage("1234_4567", MsgStateEnum.POSTPONED, null, false);
                insertNewMessage("1234_4567_8", MsgStateEnum.PARTLY_FAILED, FUNNEL_VALUE, false);
                insertNewMessage("1234_4567_9", MsgStateEnum.PARTLY_FAILED, "somethingElse", false);
            }
        });
    }

    private Message insertNewMessage(String correlationId, MsgStateEnum state, @Nullable String funnelValue,
            boolean guaranteedOrder) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(state);

        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);
        msg.setFunnelValue(funnelValue);
        msg.setGuaranteedOrder(guaranteedOrder);

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();

        return msg;
    }

    @Test
    public void testGetNextMessage_moreThreads() throws InterruptedException {
        // prepare threads
        int threads = 5;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    messagePollExecutor.run();
                } finally {
                    latch.countDown();
                }
            }
        };

        mock.expectedMessageCount(3);

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        mock.assertIsSatisfied();

        // verify messages
        Message msg = findMessage("1234_4567");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));

        msg = findMessage("1234_4567_8");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));

        msg = findMessage("1234_4567_9");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private Message findMessage(String correlationId) {
        String jSql = "SELECT m "
                + "FROM Message m "
                + "WHERE m.correlationId = ?1";

        Query q = em.createQuery(jSql);
        q.setParameter (1, correlationId);
        q.setMaxResults(1);
        List<Message> messages = (List<Message>) q.getResultList();

        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));  //postponedIntervalWhenFailed=0

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_onlyOneMessage() throws InterruptedException {
        // prepare only one message => continue processing
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_msgIsNotGuaranteed() throws InterruptedException {
        // prepare messages that is not guaranteed => continue processing
        insertNewMessage("id1", MsgStateEnum.PROCESSING, FUNNEL_VALUE, false);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_differentFunnelValues() throws InterruptedException {
        // prepare messages that is not guaranteed => continue processing
        insertNewMessage("id1", MsgStateEnum.PROCESSING, "someValue", true);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_failedMessage() throws InterruptedException {
        // prepare message that should fail
        insertNewMessage("id1", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), -10)); //postponedIntervalWhenFailed=0

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.FAILED));
    }
}
