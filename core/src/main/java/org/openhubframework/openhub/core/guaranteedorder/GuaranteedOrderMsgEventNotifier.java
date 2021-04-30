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

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.event.CompletedGuaranteedOrderMsgAsynchEvent;
import org.openhubframework.openhub.api.event.EventNotifier;
import org.openhubframework.openhub.api.event.EventNotifierBase;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;


/**
 * Listens to successfully completed message event {@link CompletedGuaranteedOrderMsgAsynchEvent}.
 * <p></p>
 * The following message in order is found using the funnel value and sent for next processing.
 *
 * @author Michal Sabol
 */
@EventNotifier
public class GuaranteedOrderMsgEventNotifier extends EventNotifierBase<CompletedGuaranteedOrderMsgAsynchEvent> {

    @Autowired
    private MessageService messageService;

    private ProducerTemplate producerTemplate;

    private String targetURI = AsynchConstants.URI_ASYNC_MSG;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        this.producerTemplate = applicationContext.getBean(ProducerTemplate.class);
    }

    @Override
    protected void doNotify(CompletedGuaranteedOrderMsgAsynchEvent event) {
        final Message message = event.getExchange().getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);

        final Message nextMessage = messageService.findPostponedMessage(message.getFunnelValue());

        if (nextMessage != null) {
            // try to get lock for the message
            boolean isLock = messageService.setStateInQueueForLock(nextMessage);
            if (!isLock) {
                throw new LockFailureException("Failed to lock message for re-processing: " + nextMessage.toHumanString());
            }

            log.debug("Found following POSTPONED message: {} with funnel value: {} => send for next processing", nextMessage.toHumanString(),
                    nextMessage.getFunnelValue());

            // sends message for next processing
            producerTemplate.sendBodyAndHeader(targetURI, ExchangePattern.InOnly, nextMessage,
                    AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());
        }
    }
}
