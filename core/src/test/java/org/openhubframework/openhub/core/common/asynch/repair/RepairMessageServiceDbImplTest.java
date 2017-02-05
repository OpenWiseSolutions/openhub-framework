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

package org.openhubframework.openhub.core.common.asynch.repair;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;

/**
 * Tests {@link RepairMessageServiceDbImpl}
 */
@Transactional
public class RepairMessageServiceDbImplTest extends AbstractCoreDbTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepairMessageServiceDbImplTest.class);

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RepairMessageServiceDbImpl messageService;

    @Test
    public void testRepairProcessingMessagesMany() throws Exception {
        List<Message> messages = new LinkedList<>();
        messages.addAll(Arrays.asList(createAndSaveMessages(70, MsgStateEnum.PROCESSING)));
        messages.addAll(Arrays.asList(createAndSaveMessages(30, MsgStateEnum.NEW)));
        messages.addAll(Arrays.asList(createAndSaveMessages(28, MsgStateEnum.IN_QUEUE)));

        // 3 times because MAX_MESSAGES_IN_ONE_QUERY=50
        messageService.repairProcessingMessages();
        messageService.repairProcessingMessages();
        messageService.repairProcessingMessages();

        for (Message message : messages) {
            LOG.info("Verifying message {}", message);
            Message found = messageDao.findMessage(message.getMsgId());
            assertThat(found, notNullValue());
            assertThat(found.getState(), is(MsgStateEnum.PARTLY_FAILED));
        }
    }

    @Test
    public void testRepairProcessingMessagesToFailed() throws Exception {
        setPrivateField(messageService, "countPartlyFailsBeforeFailed", new FixedConfigurationItem<>(2));

        RouteBuilder failedRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(AsynchConstants.URI_ERROR_FATAL)
                    .to("mock:test");
            }
        };
        getCamelContext().addRoutes(failedRoute);

        MockEndpoint failedMock = getCamelContext().getEndpoint("mock:test", MockEndpoint.class);
        failedMock.expectedMessageCount(1);

        Message message = createAndSaveMessages(1, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setState(MsgStateEnum.PROCESSING);
                message.setFailedCount(2);
                message.setMsgTimestamp(DateTime.now().minusHours(1).toDate());
                message.setStartProcessTimestamp(DateTime.now().minusHours(1).toDate());
                message.setLastUpdateTimestamp(DateTime.now().minusHours(1).toDate());
            }
        })[0];

        messageService.repairProcessingMessages();

        failedMock.assertIsSatisfied();

        Message found = messageDao.findMessage(message.getMsgId());
        assertThat(found, notNullValue());
        assertThat(found.getState(), is(MsgStateEnum.PROCESSING)); //"failed" route is mocked, state hasn't been changed

        // check
        Exchange exchange = failedMock.getExchanges().get(0);
        assertThat(exchange.getIn().getBody(), nullValue());
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT), notNullValue());
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT), instanceOf(IntegrationException.class));
        assertThat((Message)exchange.getIn().getHeader(AsynchConstants.MSG_HEADER), is(found));
    }

    @Test
    public void testRepairProcessingMessagesOne() throws Exception {
        Message message = createAndSaveMessages(1, MsgStateEnum.PROCESSING)[0];

        messageService.repairProcessingMessages();

        Message found = messageDao.findMessage(message.getMsgId());
        assertThat(found, notNullValue());
        assertThat(found.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    private Message[] createAndSaveMessages(int messageCount, final MsgStateEnum state) {
        return createAndSaveMessages(messageCount, new MessageCallback() {
            @Override
            public void beforeInsert(Message message, int order) {
                message.setState(state);
                message.setMsgTimestamp(DateTime.now().minusHours(1).toDate());
                message.setStartProcessTimestamp(DateTime.now().minusHours(1).toDate());
                message.setLastUpdateTimestamp(DateTime.now().minusHours(1).toDate());
            }
        });
    }
}
