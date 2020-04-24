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

import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;
import org.openhubframework.openhub.core.common.dao.RequestResponseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * FinalMessageProcessor implementation that does delete given message from datastore.
 *
 * It does delete message and all related entities as well.
 *
 * @author Karel Kovarik
 * @since 2.1
 * @see FinalMessageProcessor
 */
@Qualifier(DeleteFinalMessageProcessor.QUALIFIER)
public class DeleteFinalMessageProcessor extends AbstractFinalMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteFinalMessageProcessor.class);

    /**
     * Qualifier used for the bean.
     */
    public static final String QUALIFIER = "deleteFinalMessageProcessor";

    /**
     * The order used in FinalMessageProcessor hierarchy, see {@link FinalMessageProcessor#getOrder()}.
     */
    public static final int ORDER = -1_000;

    @Autowired
    protected RequestResponseDao requestResponseDao;

    @Autowired
    protected ExternalCallDao externalCallDao;

    @Override
    protected void doProcessMessage(final Message message) {
        // delete external calls
        deleteExternalCalls(message);

        // delete request responses
        deleteRequestsResponses(message);

        // delete message entity itself
        deleteMessageEntity(message);

        LOG.debug("Message [{}] deleted.", message.getId());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Delete all external calls for given Message.
     *
     * @param message the message to be deleted.
     */
    protected void deleteExternalCalls(final Message message) {
        message.getExternalCalls().stream()
               .peek(externalCall -> LOG.debug("Will delete external call [{}].", externalCall.getId()))
               .forEach(externalCall -> externalCallDao.delete(externalCall));
    }

    /**
     * Delete all request & responses for given Message.
     *
     * @param message the message to be deleted.
     */
    protected void deleteRequestsResponses(final Message message) {
        for (Request request : message.getRequests()) {
            LOG.debug("Will delete request [{}] and response [{}].",
                    request.getId(),
                    request.getResponse() != null ? request.getResponse().getId() : null);
            if (request.getResponse() != null) {
                requestResponseDao.deleteResponse(request.getResponse());
            }
            requestResponseDao.deleteRequest(request);
        }
    }

    /**
     * Delete message entity itself.
     *
     * @param message the message to be deleted.
     */
    protected void deleteMessageEntity(final Message message) {
        getMessageDao().delete(message);
    }
}
