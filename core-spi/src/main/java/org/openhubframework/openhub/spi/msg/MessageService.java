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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.camel.ExchangeProperty;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;

import org.apache.camel.Header;
import org.apache.camel.Properties;


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
     * <p/>
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
     * <p/>
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
     * <p/>
     * If message is child message then parent message will be marked as failed too.
     *
     * @param msg the message
     * @param errCode the error code
     * @param errDesc the error description
     */
    void setStateFailed(Message msg, ErrorExtEnum errCode, String errDesc);

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
     * Finds message by message external system and correlation Id.
     * If system parameter is null then messages are searched by correlation id only.
     *
     * @param correlationId the correlation ID
     * @param systemEnum the external system
     * @return message or {@code null} if not found message with specified correlation ID
     */
    @Nullable
    Message findMessageByCorrelationId(String correlationId, @Nullable ExternalSystemExtEnum systemEnum);

    /**
     * Finds message by substring in payload property.
     *
     * @param substring the substring of payload property
     * @return list of messages or {@code empty list} if not found messages with substring in payload property
     */
    List<Message> findMessagesByContent(String substring);

    /**
     * Get count of messages in specific state.
     *
     * @param state State of message
     * @param interval searching messages updated after this interval (in seconds)
     * @return count of messages
     */
    int getCountMessages(MsgStateEnum state, @Nullable Integer interval);

    /**
     * Get count of processing messages for specified funnel value and funnel ID.
     *
     * @param funnelValue the funnel value
     * @param idleInterval interval (in seconds) that determines how long can be message processing
     * @param funnelCompId the funnel component ID
     * @return count of processing messages
     */
    int getCountProcessingMessagesForFunnel(String funnelValue, int idleInterval, String funnelCompId);

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
    List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, int idleInterval,
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
}
