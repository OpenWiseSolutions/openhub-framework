/*
 * Copyright 2014-2020 the original author or authors.
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

package org.openhubframework.openhub.core.common.event;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.event.*;


/**
 * Default implementation of {@link AsynchEventFactory} interface.
 *
 * @author Petr Juza
 */
public class DefaultAsynchEventFactory implements AsynchEventFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsynchEventFactory.class);

    @Override
    public CompletedMsgAsynchEvent createCompletedMsgEvent(Exchange exchange) {
        CompletedMsgAsynchEvent event = new CompletedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public PartlyFailedMsgAsynchEvent createPartlyFailedMsgEvent(Exchange exchange) {
        PartlyFailedMsgAsynchEvent event = new PartlyFailedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public FailedMsgAsynchEvent createFailedMsgEvent(Exchange exchange) {
        FailedMsgAsynchEvent event = new FailedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public WaitingMsgAsynchEvent createWaitingMsgEvent(Exchange exchange) {
        WaitingMsgAsynchEvent event = new WaitingMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public ProcessingMsgAsynchEvent createProcessingMsgEvent(Exchange exchange) {
        ProcessingMsgAsynchEvent event = new ProcessingMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public PostponedMsgAsynchEvent createPostponedMsgEvent(Exchange exchange) {
        PostponedMsgAsynchEvent event = new PostponedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public CompletedGuaranteedOrderMsgAsynchEvent createCompletedGuaranteedOrderMsgEvent(Exchange exchange) {
        CompletedGuaranteedOrderMsgAsynchEvent event = new CompletedGuaranteedOrderMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        LOG.debug("New event was created: {}", event);

        return event;
    }

    private Message getMsgFromExchange(Exchange exchange) {
        return (Message) exchange.getIn().getHeader(AsynchConstants.MSG_HEADER);
    }
}
