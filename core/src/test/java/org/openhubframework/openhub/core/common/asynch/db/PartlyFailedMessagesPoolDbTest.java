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

package org.openhubframework.openhub.core.common.asynch.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.asynch.queue.MessagesPool;
import org.openhubframework.openhub.core.common.asynch.queue.MessagesPoolImpl;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;
import org.openhubframework.openhub.spi.node.NodeService;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link MessagesPoolImpl}.
 *
 * @author Petr Juza
 */
@Transactional
public class PartlyFailedMessagesPoolDbTest extends AbstractCoreDbTest {

    @Autowired
    private MessagesPool messagesPool;

    @Autowired
    private NodeService nodeService;

    private TransactionTemplate transactionTemplate;

    @Before
    public void prepareMessage() throws Exception {
        // setup transactionTemplate
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    };

    @Before
    public void prepareData() {
        // set failed limit
        setPrivateField(messagesPool, "partlyFailedInterval", new FixedConfigurationItem<>(Seconds.ZERO));
    }

    @Test
    public void testGetNextMessage() {
        // add one message and try to lock it
        transactionTemplate.execute(status -> {
            insertNewMessage("1234_4567", MsgStateEnum.PARTLY_FAILED);
            return null;
        });

        Message nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, notNullValue());
        assertThat(nextMsg.getState(), is(MsgStateEnum.IN_QUEUE));
        assertThat(nextMsg.getNodeId(), is(nodeService.getActualNode().getNodeId()));
        assertThat(nextMsg.getStartProcessTimestamp(), nullValue());
        assertThat(nextMsg.getStartInQueueTimestamp(), notNullValue());
        assertThat(nextMsg.getLastUpdateTimestamp(), notNullValue());

        // try again
        nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, nullValue());
    }

    @Test
    public void testGetNextMessage_noNextMessage() {
        Message nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, nullValue());
    }

    private void insertNewMessage(String correlationId, MsgStateEnum state) {
        Instant currDate = Instant.now();

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

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();
    }
}
