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

import org.openhubframework.openhub.api.entity.MsgStateEnum;


/**
 * Defines contract for manipulation with existing messages.
 *
 * @author Viliam Elischer
 */
public interface MessageOperationService {

    /**
     * Method designed to change the state of an asynchronous message (moved from {@link MsgStateEnum#FAILED}
     * to {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param messageId    Identifier of the message used in the restart process (must be in FAILED state)
     * @param totalRestart {@code true} if message should start from scratch again or {@code false}
     *                                 if message should continue when it failed
     */
    void restartMessage(long messageId, boolean totalRestart);

    /**
     * Cancels next message processing, sets {@link MsgStateEnum#CANCEL} state.
     * Only {@link MsgStateEnum#NEW}, {@link MsgStateEnum#PARTLY_FAILED} and {@link MsgStateEnum#POSTPONED}
     * messages can be canceled.
     *
     * @param messageId the message ID
     */
    void cancelMessage(long messageId);
}
