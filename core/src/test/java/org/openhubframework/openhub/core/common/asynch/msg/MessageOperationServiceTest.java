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

package org.openhubframework.openhub.core.common.asynch.msg;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;


/**
 * Test suite for {@link MessageOperationService}.
 *
 * @author Petr Juza
 */
public class MessageOperationServiceTest extends AbstractCoreDbTest {

    @Autowired
    private MessageOperationService operationService;

    @Test
    public void testRestartForFailedMessage() {
        // prepare message in FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.FAILED);
            }
        });

        // cancel message
        operationService.restartMessage(messages[0].getMsgId(), false);

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testRestartForFailedMessage_multiThreaded() throws Exception {
        // prepare message in FAILED state
        final Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.FAILED);
            }
        });

        // prepare threads
        int threads = 2;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    // cancel message
                    operationService.restartMessage(messages[0].getMsgId(), false);
                } finally {
                    latch.countDown();
                }
            }
        };

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testRestartForCancelMessage() {
        // prepare message in FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.CANCEL);
            }
        });

        // cancel message
        operationService.restartMessage(messages[0].getMsgId(), false);

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testCancelForNEW() {
        // prepare message in NEW state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.NEW);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test
    public void testCancelForPARTLY_FAILED() {
        // prepare message in PARTLY_FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.PARTLY_FAILED);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test
    public void testCancelForPOSTPONED() {
        // prepare message in POSTPONED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.POSTPONED);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongCancel() {
        // prepare message in NEW state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.PROCESSING);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());
    }
}
