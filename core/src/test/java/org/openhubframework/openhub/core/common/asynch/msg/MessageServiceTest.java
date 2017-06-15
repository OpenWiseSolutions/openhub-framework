/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Test;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.test.data.ErrorTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;


/**
 * Test suite for {@link MessageService}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class MessageServiceTest extends AbstractCoreDbTest {

    @Autowired
    private MessageService messageService;

    @Test
    public void testSetStateProcessing() throws Exception {
        MessageCallback processor = new MessageCallback() {
            @Override
            public void beforeInsert(Message msg, int order) throws Exception {
                messageService.setStateProcessing(msg);
            }
        };

        assertSetWrongState(MsgStateEnum.NEW, processor);
        assertSetWrongState(MsgStateEnum.PROCESSING, processor);
        assertSetState(MsgStateEnum.WAITING_FOR_RES, processor, MsgStateEnum.PROCESSING);
    }

    @Test
    public void testSetStateWaiting() throws Exception {
        Message[] messages = createAndSaveMessages(1, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setParentMessage(true);
                message.setState(MsgStateEnum.PROCESSING);
            }
        });

        messageService.setStateWaiting(messages[0]);

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.WAITING));
    }

    @Test
    public void testSetStateInQueueForLock() throws Exception {
        MessageCallback processor = new MessageCallback() {
            @Override
            public void beforeInsert(Message msg, int order) throws Exception {
                messageService.setStateInQueueForLock(msg);
            }
        };

        assertSetWrongState(MsgStateEnum.FAILED, processor);
        assertSetWrongState(MsgStateEnum.PROCESSING, processor);
        assertSetState(MsgStateEnum.NEW, processor, MsgStateEnum.IN_QUEUE);
    }

    @Test
    public void testSetStateWaitingForResponse() throws Exception {
        MessageCallback processor = new MessageCallback() {
            @Override
            public void beforeInsert(Message msg, int order) throws Exception {
                messageService.setStateWaitingForResponse(msg);
            }
        };

        assertSetWrongState(MsgStateEnum.NEW, processor);
        assertSetState(MsgStateEnum.PROCESSING, processor, MsgStateEnum.WAITING_FOR_RES);
        assertSetWrongState(MsgStateEnum.WAITING_FOR_RES, processor);
    }

    @Test
    public void testSetStatePartlyFailedWithoutError() throws Exception {
        MessageCallback processor = new MessageCallback() {
            @Override
            public void beforeInsert(Message msg, int order) throws Exception {
                messageService.setStatePartlyFailedWithoutError(msg);
            }
        };

        assertSetState(MsgStateEnum.NEW, processor, MsgStateEnum.PARTLY_FAILED);
        assertSetState(MsgStateEnum.PROCESSING, processor, MsgStateEnum.PARTLY_FAILED);
    }

    @Test
    public void testSetStateFailed() throws Exception {
        MessageCallback processor = new MessageCallback() {
            @Override
            public void beforeInsert(Message msg, int order) throws Exception {
                messageService.setStateFailed(msg, ErrorTestEnum.E200, "error desc");
            }
        };

        assertSetState(MsgStateEnum.NEW, processor, MsgStateEnum.FAILED);
        assertSetState(MsgStateEnum.PROCESSING, processor, MsgStateEnum.FAILED);
        assertSetState(MsgStateEnum.PARTLY_FAILED, processor, MsgStateEnum.FAILED);
    }

    @Test
    public void testFindMessagesByContent() throws Exception {
        // prepare message
        createAndSaveMessages(2, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                if (order == 1) {
                    message.setPayload("payload");
                } else if (order == 2) {
                    message.setPayload("different");
                }
            }
        });

        List<Message> dbMessages = messageService.findMessagesByContent("payload");
        assertThat(dbMessages.size(), is(1));
        assertThat(dbMessages.get(0).getPayload(), is("payload"));
    }

    @Test
    public void testGetCountMessages() throws Exception {
        // prepare message
        createAndSaveMessages(2, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setState(MsgStateEnum.CANCEL);
            }
        });

        assertThat(messageService.getCountMessages(MsgStateEnum.CANCEL, null), is(2));
        assertThat(messageService.getCountMessages(MsgStateEnum.PROCESSING, null), is(0));
        assertThat(messageService.getCountMessages(MsgStateEnum.CANCEL, Seconds.of(60).toDuration()), is(2));
    }

    @Test
    public void testFindMessageByCorrelationId() throws Exception {
        final String correlationId = "3478934j3";

        // prepare message
        createAndSaveMessages(2, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                if (order == 1) {
                    message.setCorrelationId(correlationId);
                } else if (order == 2) {
                    message.setCorrelationId("3498738947b");
                }
                message.setSourceSystem(ExternalSystemTestEnum.BILLING);
            }
        });

        // verify
        Message dbMsg = messageService.findMessageByCorrelationId(correlationId, null);
        assertThat(dbMsg, notNullValue());
        assertThat(dbMsg.getCorrelationId(), is(correlationId));

        dbMsg = messageService.findMessageByCorrelationId(correlationId, ExternalSystemTestEnum.BILLING);
        assertThat(dbMsg, notNullValue());
    }

    @Test
    public void testGetCountProcessingMessagesForFunnel() throws Exception {
        // prepare message
        createAndSaveMessages(1, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setFunnelValue("funnel");
                message.setFunnelComponentId("funnelComp");
                message.setState(MsgStateEnum.WAITING);
                message.setStartProcessTimestamp(Instant.now());
            }
        });

        // verify
        int count = messageService.getCountProcessingMessagesForFunnel("funnel", Seconds.of(60).toDuration(), "funnelComp");
        assertThat(count, is(1));
    }


    @Test
    public void testFindMessagesByFilter_minimalOk() throws Exception {
        // prepare message
        createAndSaveMessages(1, (message, order) -> {
            if (order == 1) {
                message.setPayload("message-payload");
            }
        });

        final MessageFilter filter = new MessageFilter();
        filter.setReceivedFrom(LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC));
        List<Message> dbMessages = messageService.findMessagesByFilter(filter);
        assertThat(dbMessages.size(), is(1));
        assertThat(dbMessages.get(0).getPayload(), is("message-payload"));
    }

    @Test
    public void testFindMessagesByFilter_fullFilter() throws Exception {
        // prepare message
        createAndSaveMessages(10, (message, order) -> {
            findByFilter_messageFill(message);
            switch (order) {
                case 1:
                    message.setReceiveTimestamp(LocalDateTime.of(2016,5,27,19,17,10).toInstant(ZoneOffset.UTC));
                    break;
                case 2:
                    message.setLastUpdateTimestamp(LocalDateTime.of(2016,5,27,19,17,10).toInstant(ZoneOffset.UTC));
                    break;
                case 3:
                    message.setSourceSystem(ExternalSystemTestEnum.BILLING);
                    break;
                case 4:
                    message.setProcessId("987-654-321");
                    break;
                case 5:
                    message.setState(MsgStateEnum.FAILED);
                    break;
                case 6:
                    message.setFailedErrorCode(ErrorTestEnum.E200);
                    break;
                case 7:
                    message.setService(ServiceTestEnum.ACCOUNT);
                    break;
                case 8:
                    message.setOperationName("fakeOperation");
                    break;
                case 9:
                    message.setPayload("another payload");
                    break;

            }
        });

        final MessageFilter filter = new MessageFilter();
        filter.setReceivedFrom(LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC));
        filter.setReceivedTo(LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC));
        filter.setLastChangeFrom(LocalDateTime.of(2017,5,27,20,17,10).toInstant(ZoneOffset.UTC));
        filter.setLastChangeTo(LocalDateTime.of(2017,5,27,20,17,10).toInstant(ZoneOffset.UTC));
        filter.setSourceSystem(ExternalSystemTestEnum.CRM.getSystemName());
        filter.setProcessId("123-456-789");
        filter.setState(MsgStateEnum.PROCESSING);
        filter.setErrorCode("E300");
        filter.setServiceName(ServiceTestEnum.CUSTOMER.getServiceName());
        filter.setOperationName("testOperation");
        filter.setFulltext("test payload");
        assertThat(messageService.findMessagesByFilter(filter).size(), is(1));
    }

    @Test
    public void testFindMessagesByFilter_correlationId() throws Exception {
        // prepare message
        createAndSaveMessages(10, (message, order) -> {
            if (order == 1) {
                message.setCorrelationId("11111-22222-33333");
            }
        });

        final MessageFilter filter = new MessageFilter();
        filter.setReceivedFrom(LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC));
        filter.setCorrelationId("11111-22222-33333");
        List<Message> dbMessages = messageService.findMessagesByFilter(filter);
        assertThat(dbMessages.size(), is(1));
    }

    @Test
    public void testFindMessagesByFilter_fullText() throws Exception {
        // prepare message
        createAndSaveMessages(4, (message, order) -> {
            if (order == 1) {
                message.setPayload("car");
            } else if (order == 2) {
                message.setPayload("carrot");
            } else if (order == 3) {
                message.setPayload("scar");
            }
        });

        final MessageFilter filter = new MessageFilter();
        filter.setReceivedFrom(LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC));
        filter.setFulltext("car");
        assertThat(messageService.findMessagesByFilter(filter).size(), is(3));
    }

    private static void findByFilter_messageFill(final Message msg) {
        Instant received = LocalDateTime.of(2017,5,27,19,17,10).toInstant(ZoneOffset.UTC);
        Instant lastUpdate = LocalDateTime.of(2017,5,27,20,17,10).toInstant(ZoneOffset.UTC);
        msg.setFailedErrorCode(ErrorTestEnum.E300);
        msg.setProcessId("123-456-789");
        msg.setReceiveTimestamp(received);
        msg.setLastUpdateTimestamp(lastUpdate);
    }

    private void assertSetState(final MsgStateEnum initState, MessageCallback processor, final MsgStateEnum expState)
            throws Exception {
        // prepare message
        Message[] messages = createAndSaveMessages(1, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setState(initState);
            }
        });

        assertThat(messages.length, is(1));

        // action
        processor.beforeInsert(messages[0], 0);

        // verify
        Message dbMsg = em.find(Message.class, messages[0].getMsgId());
        assertThat(dbMsg, notNullValue());
        assertThat(dbMsg.getState(), is(expState));
    }

    private void assertSetWrongState(final MsgStateEnum initState, MessageCallback processor) {
        // prepare message
        Message[] messages = createAndSaveMessages(1, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setState(initState);
            }
        });

        assertThat(messages.length, is(1));

        try {
            processor.beforeInsert(messages[0], 0);
            fail("Change from state '" + initState + "' is not allowed");
        } catch (Exception ex) {
            // it's ok
        }
    }
}
