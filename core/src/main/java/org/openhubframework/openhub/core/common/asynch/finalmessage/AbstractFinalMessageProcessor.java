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

import javax.annotation.PostConstruct;

import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Abstract class for custom implementations of {@link FinalMessageProcessor}.
 *
 * @author Karel Kovarik
 * @since 2.1
 */
public abstract class AbstractFinalMessageProcessor implements FinalMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFinalMessageProcessor.class);

    @Autowired
    protected MessageDao messageDao;

    // transactionTemplate
    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @PostConstruct
    void initializeTransactionManager() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    }

    @Override
    public final void processMessage(final Message message) {
        LOG.trace("Will process message with id [{}].", message.getId());

        // execute in REQUIRED transaction
        transactionTemplate.execute((TransactionStatus status) -> {
            // fetch the message to have it in current hibernate session
            final Message loaded = messageDao.findMessage(message.getId());

            LOG.trace("Message fetched successfully");
            // invoke the processing method.
            doProcessMessage(loaded);
            return null; // callback without result
        });

        LOG.trace("Message processing finished.");
    }

    /**
     * Get instance of messageDao repository.
     *
     * @return the dao bean.
     */
    protected MessageDao getMessageDao() {
        return messageDao;
    }

    /**
     * Do process message.
     *
     * @param message the message entity.
     */
    protected abstract void doProcessMessage(Message message);
}
