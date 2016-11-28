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

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.core.common.dao.MessageDao;


/**
 * DB implementation of {@link MessagesPool} interface.
 *
 * @author Petr Juza
 */
public class MessagesPoolDbImpl implements MessagesPool {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesPoolDbImpl.class);

    @Autowired
    private MessageDao messageDao;

    private TransactionTemplate transactionTemplate;

    /**
     * Interval (in seconds) between two tries of partly failed messages.
     */
    @Value("${asynch.partlyFailedInterval}")
    private int partlyFailedInterval;

    /**
     * Interval (in seconds) after that can be postponed message processed again.
     */
    @Value("${asynch.postponedInterval}")
    private int postponedInterval;

    @Required
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

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
        boolean isLock = lockMessage(msg);
        if (!isLock) {
            throw new LockFailureException("Failed to lock message for re-processing: " + msg.toHumanString());
        }

        return msg;
    }

    @Nullable
    private Message findPostponedMessage() {
        return transactionTemplate.execute(new TransactionCallback<Message>() {
            @Override
            public Message doInTransaction(final TransactionStatus transactionStatus) {
                return messageDao.findPostponedMessage(postponedInterval);
            }
        });
    }

    @Nullable
    private Message findPartlyFailedMessage() {
        return transactionTemplate.execute(new TransactionCallback<Message>() {
            @Override
            public Message doInTransaction(final TransactionStatus transactionStatus) {
                return messageDao.findPartlyFailedMessage(partlyFailedInterval);
            }
        });
    }

    private boolean lockMessage(final Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        boolean isLock;
        try {
            isLock = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(final TransactionStatus transactionStatus) {
                    return messageDao.updateMessageForLock(msg);
                }
            });
        } catch (DataAccessException ex) {
            isLock = false;
        }

        if (isLock) {
            LOG.debug("Successfully locked message for re-processing: {}", msg.toHumanString());
            return true;
        } else {
            LOG.debug("Failed to lock message for re-processing: {}", msg.getMsgId());
            return false;
        }
    }
}
