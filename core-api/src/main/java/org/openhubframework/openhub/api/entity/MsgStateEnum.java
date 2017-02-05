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

package org.openhubframework.openhub.api.entity;

/**
 * Enumeration of possible message states.
 *
 * @author Petr Juza
 */
public enum MsgStateEnum {

    /**
     * New saved message.
     */
    NEW,

    /**
     * Successfully processed message.
     */
    OK,

    /**
     * Message is in queue and waiting for free thread for processing.
     */
    IN_QUEUE,

    /**
     * Message is just processing.
     */
    PROCESSING,

    /**
     * Last processing ended with error, there will be next try.
     */
    PARTLY_FAILED,

    /**
     * Finally failed message, no next processing.
     */
    FAILED,

    /**
     * Parent message that waits for child messages.
     */
    WAITING,

    /**
     * Message that waits for confirmation/response from external system (e.g. from VF)
     */
    WAITING_FOR_RES,

    /**
     * Message is postponed because there was another message that was processed at the same time
     * with same funnel values.
     */
    POSTPONED,

    /**
     * Message was canceled by external system or by administrator. This state isn't set by this application.
     */
    CANCEL;


    /**
     * Is specified state running?
     *
     * @param state the state
     * @return {@code true} if state is running otherwise {@code false}
     */
    public static boolean isRunning(MsgStateEnum state) {
        return state == PROCESSING || state == WAITING || state == WAITING_FOR_RES;
    }

    /**
     * Is specified state processing?
     *
     * @param state the state
     * @return {@code true} if state is processing otherwise {@code false}
     */
    public static boolean isProcessing(MsgStateEnum state) {
        return isRunning(state) || state == PARTLY_FAILED || state == POSTPONED;
    }

    /**
     * Is specified state final?
     *
     * @param state the state
     * @return {@code true} if state is final otherwise {@code false}
     */
    public static boolean isFinal(MsgStateEnum state) {
        return state == OK || state == CANCEL || state == FAILED;
    }
}
