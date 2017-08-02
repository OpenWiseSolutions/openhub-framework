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

package org.openhubframework.openhub.spi.msg;

import static org.openhubframework.openhub.api.asynch.AsynchConstants.CUSTOM_DATA_PROP;
import static org.openhubframework.openhub.api.asynch.AsynchConstants.EXCEPTION_ERROR_CODE;
import static org.openhubframework.openhub.api.asynch.AsynchConstants.MSG_HEADER;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.apache.camel.Properties;

import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;


/**
 * Service for manipulating with {@link Message}.
 *
 * @author Petr Juza
 */
public interface MessageService {

    String BEAN = "messageService";

    /**
     * Inserts new message.
     *
     * @param message message that will be saved
     */
    void insertMessage(Message message);
    
    /**
     * Inserts new messages.
     *
     * @param messages the collection of message
     */
    void insertMessages(Collection<Message> messages);

    /**
     * Changes state of the message to {@link MsgStateEnum#OK}.
     * <p>
     * If message is child message then method checks if all child messages of the parent message aren't processed.
     *
     * @param msg the message
     * @param props the exchange properties [property name; property value]
     */
    void setStateOk(@Header(MSG_HEADER) Message msg, @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#PROCESSING}.
     *
     * @param msg the message
     */
    void setStateProcessing(Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#WAITING} - only if the message hasn't been already processed.
     *
     * @param msg the message
     */
    void setStateWaiting(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#WAITING_FOR_RES}.
     *
     * @param msg the message
     */
    void setStateWaitingForResponse(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#PARTLY_FAILED} but without increasing error count.
     *
     * @param msg the message
     */
    void setStatePartlyFailedWithoutError(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param msg the message
     * @param ex the exception
     * @param errCode the error code that can be explicitly defined if needed
     * @param customData the custom data
     * @param props the exchange properties [property name; property value]
     */
    void setStatePartlyFailed(@Header(MSG_HEADER) Message msg,
                              Exception ex,
                              @ExchangeProperty(EXCEPTION_ERROR_CODE) @Nullable ErrorExtEnum errCode,
                              @ExchangeProperty(CUSTOM_DATA_PROP) @Nullable String customData,
                              @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#FAILED}.
     * <p>
     * If message is child message then parent message will be marked as failed too.
     *
     * @param msg the message
     * @param ex the exception
     * @param errCode the error code that can be explicitly defined if needed
     * @param customData the custom data
     * @param props the exchange properties [property name; property value]
     */
    void setStateFailed(@Header(MSG_HEADER) Message msg,
                        Exception ex,
                        @ExchangeProperty(EXCEPTION_ERROR_CODE) @Nullable ErrorExtEnum errCode,
                        @ExchangeProperty(CUSTOM_DATA_PROP) @Nullable String customData,
                        @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#FAILED}.
     * <p>
     * If message is child message then parent message will be marked as failed too.
     *
     * @param msg the message
     * @param errCode the error code
     * @param errDesc the error description
     */
    void setStateFailed(Message msg, ErrorExtEnum errCode, String errDesc);

    /**
     * Set state {@link MsgStateEnum#IN_QUEUE} on {@link Message} under database lock.
     *
     * @param message message on which will be state changed
     * @return {@code true} - state was successfully changed, {@code false} - otherwise
     */
    boolean setStateInQueueForLock(Message message);

    /**
     * Set state {@link MsgStateEnum#PROCESSING} on {@link Message} under database lock.
     *
     * @param message message on which will be state changed
     * @return {@code true} - state was successfully changed, {@code false} - otherwise
     */
    boolean setStateProcessingForLock(Message message);

    /**
     * Finds message by message ID.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not found message with specified ID
     */
    @Nullable
    Message findMessageById(Long msgId);

    /**
     * Finds message by message ID with eager loading.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not found message with specified ID
     */
    @Nullable
    Message findEagerMessageById(Long msgId);

    /**
     * Finds list of messages that match with given filter. Sorted by received timestamp (newest first).
     *
     * @param messageFilter the filter.
     * @param limit the limit of message count.
     * @return collection of messages, or {@code empty list} if none were found.
     */
    List<Message> findMessagesByFilter(MessageFilter messageFilter, long limit);

    /**
     * Get count of messages in specific state.
     *
     * @param state State of message
     * @param interval searching messages updated after this interval (in seconds)
     * @return count of messages
     */
    int getCountMessages(MsgStateEnum state, @Nullable Duration interval);

    /**
     * Get count of processing messages for specified funnel value and funnel ID.
     *
     * @param funnelValue the funnel value
     * @param idleInterval interval (in seconds) that determines how long can be message processing
     * @param funnelCompId the funnel component ID
     * @return count of processing messages
     */
    int getCountProcessingMessagesForFunnel(String funnelValue, Duration idleInterval, String funnelCompId);

    /**
     * Gets list of messages with specified funnel value for guaranteed processing order of whole routes.
     *
     * @param funnelValue the funnel value
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForRoute(String funnelValue, boolean excludeFailedState);

    /**
     * Gets list of messages with specified funnel value for guaranteed processing order of messages
     * for specified funnel.
     *
     * @param funnelValue the funnel value
     * @param idleInterval interval (in seconds) that determines how long can message be processing
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @param funnelCompId the funnel component ID
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, Duration idleInterval,
            boolean excludeFailedState, String funnelCompId);

    /**
     * Changes state of the message to {@link MsgStateEnum#POSTPONED}.
     *
     * @param msg the message
     */
    void setStatePostponed(@Header(MSG_HEADER) Message msg);

    /**
     * Sets funnel component identifier to specified message.
     *
     * @param msg the message
     * @param funnelCompId the funnel component ID
     */
    void setFunnelComponentId(Message msg, String funnelCompId);

    /**
     * Finds ONE message in state {@link MsgStateEnum#POSTPONED}.
     *
     * @param interval Interval (in seconds) after that can be postponed message processed again
     * @return message or null if there is no any message
     */
    @Nullable
    Message findPostponedMessage(Duration interval);

    /**
     * Finds ONE message in state {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param interval Interval (in seconds) between two tries of partly failed messages.
     * @return message or null if there is no any message
     */
    @Nullable
    Message findPartlyFailedMessage(Duration interval);
}
