/*
 * Copyright 2012-2017 the original author or authors.
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

import java.time.Instant;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Filter for messages.
 *
 * @author Karel Kovarik
 */
public class MessageFilter {

    private Instant receivedFrom;
    private Instant receivedTo;
    private Instant lastChangeFrom;
    private Instant lastChangeTo;
    private String sourceSystem;
    private String correlationId;
    private String processId;
    private MsgStateEnum state;
    private String errorCode;
    private String serviceName;
    private String operationName;
    private String fulltext;

    public Instant getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(Instant receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    @Nullable
    public Instant getReceivedTo() {
        return receivedTo;
    }

    public void setReceivedTo(Instant receivedTo) {
        this.receivedTo = receivedTo;
    }

    @Nullable
    public Instant getLastChangeFrom() {
        return lastChangeFrom;
    }

    public void setLastChangeFrom(Instant lastChangeFrom) {
        this.lastChangeFrom = lastChangeFrom;
    }

    @Nullable
    public Instant getLastChangeTo() {
        return lastChangeTo;
    }

    public void setLastChangeTo(Instant lastChangeTo) {
        this.lastChangeTo = lastChangeTo;
    }

    @Nullable
    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @Nullable
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Nullable
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Nullable
    public MsgStateEnum getState() {
        return state;
    }

    public void setState(MsgStateEnum state) {
        this.state = state;
    }

    @Nullable
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Nullable
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Nullable
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    @Nullable
    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("receivedFrom", receivedFrom)
                .append("receivedTo", receivedTo)
                .append("lastChangeFrom", lastChangeFrom)
                .append("lastChangeTo", lastChangeTo)
                .append("sourceSystem", sourceSystem)
                .append("correlationId", correlationId)
                .append("processId", processId)
                .append("state", state)
                .append("errorCode", errorCode)
                .append("serviceName", serviceName)
                .append("operationName", operationName)
                .append("fulltext", fulltext)
                .toString();
    }
}
