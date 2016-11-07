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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.msg.ChildMessage;
import org.openhubframework.openhub.api.asynch.msg.MessageSplitterCallback;
import org.openhubframework.openhub.api.asynch.msg.MsgSplitter;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.spi.msg.MessageService;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.model.ModelCamelContext;
import org.springframework.util.Assert;


/**
 * Implementation of {@link MsgSplitter} interface.
 *
 * @author Petr Juza
 */
public final class MessageSplitterImpl implements MsgSplitter {

    private final ModelCamelContext camelCtx;

    private final MessageService messageService;

    private final ExecutorService executor;

    private final MessageSplitterCallback splitterCallback;

    /**
     * Creates new message splitter.
     *
     * @param messageService the message service
     * @param camelCtx the Camel context
     * @param splitterCallback the callback for getting split messages
     */
    public MessageSplitterImpl(MessageService messageService, ModelCamelContext camelCtx,
            MessageSplitterCallback splitterCallback) {

        Assert.notNull(messageService, "the messageService must not be null");
        Assert.notNull(camelCtx, "the camelCtx must not be null");
        Assert.notNull(splitterCallback, "the splitterCallback must not be null");

        this.camelCtx = camelCtx;
        this.messageService = messageService;
        this.splitterCallback = splitterCallback;
        this.executor = camelCtx.getExecutorServiceManager().newThreadPool(this, "MessageSplitter", 1, 3);
    }

    @Override
    @Handler
    public final void splitMessage(@Header(AsynchConstants.MSG_HEADER) Message parentMsg, @Body Object body) {
        Assert.notNull(parentMsg, "the parentMsg must not be null");
        Assert.isTrue(parentMsg.getState() == MsgStateEnum.PROCESSING && parentMsg.getFailedCount() == 0,
                "only new message can be split to child messages");

        // get child messages
        List<ChildMessage> childMessages = splitterCallback.getChildMessages(parentMsg, body);

        Log.debug("Count of child messages: " + childMessages.size());

        // create messages
        final List<Message> messages = new ArrayList<Message>(childMessages.size());
        for (int i = 0; i < childMessages.size(); i++) {
            ChildMessage childMsg = childMessages.get(i);

            Message message = ChildMessage.createMessage(childMsg);

            // correlation ID is unique - add order to distinguish it
            message.setCorrelationId(parentMsg.getCorrelationId() + "_" + i);
            messages.add(message);
        }

        // mark original message as parent message
        parentMsg.setParentMessage(true);

        // save all messages at once
        messageService.insertMessages(messages);

        final ProducerTemplate msgProducer = camelCtx.createProducerTemplate();

        try {
            // process messages separately one by one
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (Message msg : messages) {
                        Log.debug("Message " + msg.toHumanString() + " will be processed ...");

                        // send to process (wait for reply and then process next child message); it's new exchange
                        msgProducer.requestBody(AsynchMessageRoute.URI_SYNC_MSG, msg);

                        Log.debug("Message " + msg.toHumanString() + " was successfully processed.");
                    }
                }
            });
        } finally {
            if (msgProducer != null) {
                try {
                    msgProducer.stop();
                } catch (Exception ex) {
                    Log.error("error occurred during stopping producerTemplate", ex);
                }
            }
        }
    }

}
