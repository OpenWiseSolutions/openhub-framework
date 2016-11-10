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

package org.openhubframework.openhub.admin.dao.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * DTO with main message attributes used for message reports.
 *
 * @author Viliam Elischer
 */
public class MessageReportDto {

    private int messageId; // SQL "msg_id"
    private String serviceName;
    private String operationName;
    private String sourceSystem;
    private String state; // state CONSTANTs
    private int stateCount; // SQL: Count(*) as pocet
    private Date messageReceiveTimestamp; // SQL "recieve_timestamp"

    /* Default Constructor */

    public MessageReportDto() {
    }

    /* Getters & Setters */

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Date getMessageReceiveTimestamp() {
        return messageReceiveTimestamp;
    }

    public void setMessageReceiveTimestamp(Date messageReceiveTimestamp) {
        this.messageReceiveTimestamp = messageReceiveTimestamp;
    }

    public int getStateCount() {
        return stateCount;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("MsgID:",messageId)
                .append("MsgRecieveDateTime", messageReceiveTimestamp)
                .append("ServiceName",serviceName)
                .append("MsgOpName",operationName)
                .append("MsgSrcSys",sourceSystem)
                .append("MsgState",state)
                .append("StateCount",stateCount)
                .toString();
    }

}
