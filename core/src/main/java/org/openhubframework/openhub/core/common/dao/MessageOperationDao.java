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

package org.openhubframework.openhub.core.common.dao;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;


/**
 * DAO interface for operations with messages.
 *
 * @author Viliam Elischer
 */
public interface MessageOperationDao {

    /**
     * Restarts message = change the message state from FAILED -> PARTLY_FAILED.
     * <p/>
     * Only messages in FAILED state can be changed, otherwise exception will be thrown.
     *
     * @param msg the message
     * @return {@code true} if state was successfully set, otherwise {@code false}
     */
    boolean setPartlyFailedState(Message msg);

    /**
     * Removes external calls for specific message.
     *
     * @param msg the message
     * @param totalRestart {@code true} if all external call should be deleted (=message will be processed from scratch)
     *                                 or {@code false} if only external calls for confirmations should be deleted
     */
    void removeExtCalls(Message msg, boolean totalRestart);

    /**
     * Cancels next message processing = change the message state to {@link MsgStateEnum#CANCEL}.
     *
     * @param msg the message
     * @return {@code true} if state was successfully set, otherwise {@code false}
     */
    boolean setCancelState(Message msg);
}
