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

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.openhubframework.openhub.core.common.dao.MessageOperationDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;


/**
 * MessageOperationService implements methods to interact with the integrations messages
 * and if necessary to manipulate their properties, such as state, timestamps, etc.
 *
 * @author Viliam Elischer
 */
public class MessageOperationServiceImpl implements MessageOperationService {

    @Autowired
    private MessageOperationDao msgOpDao;

    @Autowired
    private MessageDao msgDao;

    /**
     * Method restarts message enables the option for the user to re-set/restart a message in a FAILED/CANCEL state.
     * Steps:
     * <ol>
     * <li>Determine, if msg with ID exists in <b>message</b> table</li>
     * <li>Update the state of the msg with ID to 'PARTLY_FAILED' in the <b>message</b> table</li>
     * <li>Try to delete, the record based on msg ID from <b>external_call</b> table</li>
     * </ol>
     * When the totalRestart is true the condition operation_name = 'confirmation' is NOT USED. And all records form
     * external_call with the given MessageID are removed.
     *
     * @param messageId    ID of the message that the user wants to restart
     * @param totalRestart Variable acts as SQL WHERE condition toggle between 2 statements
     */
    @Transactional
    @Override
    public synchronized void restartMessage(long messageId, boolean totalRestart) {
        try {
            Message msg = msgDao.getMessage(messageId);

            // check FAILED/CANCEL state
            if (msg.getState() != MsgStateEnum.FAILED && msg.getState() != MsgStateEnum.CANCEL) {
                throw new IllegalStateException("message " + msg.toHumanString() + " is not in FAILED or CANCEL state.");
            }

            if (!msgOpDao.setPartlyFailedState(msg)) {
                throw new IllegalStateException("Message (id = " + messageId + ") hasn't been restarted.");
            }

            msgOpDao.removeExtCalls(msg, totalRestart);

            Log.debug("Message (id = " + messageId + ", totalRestart = " + totalRestart + ") was successfully restarted ...");
        } catch (DataAccessException dx) {
            throw new RuntimeException("An error occurred during message restarting", dx);
        }
    }

    @Transactional
    @Override
    public synchronized void cancelMessage(long messageId) {
        try {
            Message msg = msgDao.getMessage(messageId);

            // check message state
            if (msg.getState() != MsgStateEnum.NEW && msg.getState() != MsgStateEnum.PARTLY_FAILED
                    && msg.getState() != MsgStateEnum.POSTPONED) {
                throw new IllegalStateException("Message (id = " + messageId + ") can be canceled only "
                        + "when its NEW or PARTLY_FAILED or POSTPONED.");
            }

            if (!msgOpDao.setCancelState(msg)) {
                throw new IllegalStateException("Message (id = " + messageId + ") hasn't been changed to CANCEL state.");
            }

            Log.debug("Message " + msg.toHumanString() + " was successfully canceled ...");
        } catch (DataAccessException dx) {
            throw new RuntimeException("An error occurred during message cancelling", dx);
        }
    }
}
