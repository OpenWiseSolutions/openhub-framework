/*
 * Copyright 2018 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.finalmessage;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.Test;
import org.mockito.InOrder;
import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessagesProcessingService;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Simple test suite for {@link FinalMessagesProcessingServiceImpl}.
 *
 * @author Karel Kovarik
 */
@TestPropertySource(properties = {
        "ohf.asynch.finalMessages.ok.saveTimeInSec=14400",
        "ohf.asynch.finalMessages.failed.saveTimeInSec=28800",
        "ohf.asynch.finalMessages.cancel.saveTimeInSec=-1",
        "ohf.asynch.finalMessages.processingEnabled=true",
        "ohf.asynch.finalMessages.iterationMessageLimit=1000"
})
public class FinalMessagesProcessingServiceImplTest extends AbstractCoreDbTest {

    @MockBean
    @Qualifier("defaultFinalMessageProcessor")
    private FinalMessageProcessor finalMessageProcessor;

    @MockBean
    @Qualifier("additionalFinalMessageProcessor")
    private FinalMessageProcessor additionalMessageProcessor;

    @Autowired
    private MessageService messageService;

    // tested
    @Autowired
    private FinalMessagesProcessingService messagesProcessingService;

    @Value("${ohf.asynch.finalMessages.failed.saveTimeInSec}")
    private int failedSaveTimeInSec;

    @Value("${ohf.asynch.finalMessages.ok.saveTimeInSec}")
    private int okSaveTimeInSec;

    @Value("${ohf.asynch.finalMessages.cancel.saveTimeInSec}")
    private int cancelSaveTimeInSec;

    @Test
    public void test_timing() {
        createAndSaveMessages(3, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 2:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec));
                    break;
                case 3:
                    message.setState(MsgStateEnum.OK);
                    // updated too recently, should not be processed yet
                    message.setLastUpdateTimestamp(Instant.now());
                    break;
            }
        });

        messagesProcessingService.processMessages();

        verify(finalMessageProcessor, times(2)).processMessage(any(Message.class));
        verify(additionalMessageProcessor, times(2)).processMessage(any(Message.class));
    }

    @Test
    public void test_split() {

        createAndSaveMessages(2, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 2:
                    message.setState(MsgStateEnum.FAILED);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(failedSaveTimeInSec + 1));
                    break;
            }
        });

        messagesProcessingService.processMessages();

        verify(finalMessageProcessor, times(2)).processMessage(any(Message.class));
        verify(additionalMessageProcessor, times(2)).processMessage(any(Message.class));
    }

    @Test
    public void test_state_ignoring() {
        // none of the messages should be processed
        createAndSaveMessages(5, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.NEW);
                    break;
                case 2:
                    message.setState(MsgStateEnum.IN_QUEUE);
                    break;
                case 3:
                    message.setState(MsgStateEnum.PROCESSING);
                    break;
                case 4:
                    message.setState(MsgStateEnum.WAITING);
                    break;
                case 5:
                    message.setState(MsgStateEnum.PARTLY_FAILED);
                    break;
            }
        });

        messagesProcessingService.processMessages();
    }

    @Test
    public void test_technicalCountLimit() {

        // set technicalLimit
        ReflectionTestUtils.setField(messagesProcessingService, "messagesTechnicalLimit", new FixedConfigurationItem<>(2L));

        createAndSaveMessages(3, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 2:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 3:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
            }
        });

        messagesProcessingService.processMessages();

        // verify only two of the three eligible messages were processed, as technical limit is reached
        verify(finalMessageProcessor, times(2)).processMessage(any(Message.class));
    }

    @Test
    public void test_errorsAreSkipped() {
        createAndSaveMessages(3, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 2:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
                case 3:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
            }
        });

        // processor will throw an exception
        doThrow(new RuntimeException("General exception")).when(finalMessageProcessor).processMessage(any(Message.class));

        messagesProcessingService.processMessages();

        // verify all messages were processed regardless
        verify(finalMessageProcessor, times(3)).processMessage(any(Message.class));
    }

    @Test
    public void test_processorOrder() {
        when(finalMessageProcessor.getOrder()).thenReturn(0);
        when(additionalMessageProcessor.getOrder()).thenReturn(1); // should be second

        createAndSaveMessages(1, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.OK);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(okSaveTimeInSec + 1));
                    break;
            }
        });

        // call tested service
        messagesProcessingService.processMessages();

        InOrder inOrder = inOrder(finalMessageProcessor, additionalMessageProcessor);

        inOrder.verify(finalMessageProcessor, times(1)).processMessage(any(Message.class));
        inOrder.verify(additionalMessageProcessor, times(1)).processMessage(any(Message.class));
    }

    @Test
    public void test_keepIndefinitely() {
        createAndSaveMessages(2, (message, order) -> {
            switch (order) {
                case 1:
                    message.setState(MsgStateEnum.CANCEL);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(1_000_000));
                    break;
                case 2:
                    message.setState(MsgStateEnum.CANCEL);
                    message.setLastUpdateTimestamp(Instant.now().minusSeconds(42));
                    break;
            }
        });

        messagesProcessingService.processMessages();

        verify(finalMessageProcessor, times(0)).processMessage(any(Message.class));
        verify(additionalMessageProcessor, times(0)).processMessage(any(Message.class));
    }
}
