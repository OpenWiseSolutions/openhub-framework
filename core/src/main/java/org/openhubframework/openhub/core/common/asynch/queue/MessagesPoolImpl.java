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

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_PARTLY_FAILED_INTERVAL_SEC;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_POSTPONED_INTERVAL_SEC;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.spi.msg.MessageService;


/**
 * Default implementation of {@link MessagesPool} interface.
 *
 * @author Petr Juza
 */
@Service
public class MessagesPoolImpl implements MessagesPool {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesPoolImpl.class);

    @Autowired
    private MessageService messageService;

    /**
     * Interval (in seconds) between two tries of partly failed messages.
     */
    @ConfigurableValue(key = ASYNCH_PARTLY_FAILED_INTERVAL_SEC)
    private ConfigurationItem<Seconds> partlyFailedInterval;

    /**
     * Interval (in seconds) after that can be postponed message processed again.
     */
    @ConfigurableValue(key = ASYNCH_POSTPONED_INTERVAL_SEC)
    private ConfigurationItem<Seconds> postponedInterval;

    @Nullable
    public Message getNextMessage() {
        // is there next message for processing?

        // firstly try postponed messages
        Message msg = findPostponedMessage();

        // then partly failed messages
        if (msg == null) {
            msg = findPartlyFailedMessage();
        }

        if (msg == null) {
            LOG.debug("No POSTPONED and PARTLY_FAILED message found for re-processing.");
            return null;
        }

        // try to get lock for the message
        boolean isLock = messageService.setStateInQueueForLock(msg);
        if (!isLock) {
            throw new LockFailureException("Failed to lock message for re-processing: " + msg.toHumanString());
        }

        return msg;
    }

    @Nullable
    private Message findPostponedMessage() {
        return messageService.findPostponedMessage(postponedInterval.getValue().toDuration());
    }

    @Nullable
    private Message findPartlyFailedMessage() {
        return messageService.findPartlyFailedMessage(partlyFailedInterval.getValue().toDuration());
    }
}
