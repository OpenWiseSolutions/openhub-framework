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

package org.openhubframework.openhub.admin.web.message.rpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.api.entity.EntityTypeExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageActionType;
import org.openhubframework.openhub.core.common.asynch.msg.MessageHelper;
import org.springframework.core.convert.converter.Converter;


/**
 * Message detail RPC, with all attributes for detail queries.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class MessageRpc extends MessageBaseRpc {

    private String processId;
    private ZonedDateTime lastChange;
    private int failedCount;
    private ZonedDateTime msgTimestamp;
    private String objectId;
    private EntityTypeExtEnum entityType;
    private String funnelValue;
    private String funnelComponentId;
    private boolean guaranteedOrder;
    private boolean excludeFailedState;
    private String businessError;
    private Long parentMsgId;
    private String body;
    private String envelope;
    private String failedDescription;
    private List<RequestInfoRpc> requests;
    private List<ExternalCallInfoRpc> externalCalls;
    private List<MessageActionType> allowedActions;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public ZonedDateTime getLastChange() {
        return lastChange;
    }

    public void setLastChange(ZonedDateTime lastChange) {
        this.lastChange = lastChange;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public ZonedDateTime getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(ZonedDateTime msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEntityType() {
        return entityType.getEntityType();
    }

    public void setEntityType(EntityTypeExtEnum entityType) {
        this.entityType = entityType;
    }

    public String getFunnelValue() {
        return funnelValue;
    }

    public void setFunnelValue(String funnelValue) {
        this.funnelValue = funnelValue;
    }

    public String getFunnelComponentId() {
        return funnelComponentId;
    }

    public void setFunnelComponentId(String funnelComponentId) {
        this.funnelComponentId = funnelComponentId;
    }

    public boolean isGuaranteedOrder() {
        return guaranteedOrder;
    }

    public void setGuaranteedOrder(boolean guaranteedOrder) {
        this.guaranteedOrder = guaranteedOrder;
    }

    public boolean isExcludeFailedState() {
        return excludeFailedState;
    }

    public void setExcludeFailedState(boolean excludeFailedState) {
        this.excludeFailedState = excludeFailedState;
    }

    public String getBusinessError() {
        return businessError;
    }

    public void setBusinessError(String businessError) {
        this.businessError = businessError;
    }

    public Long getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(Long parentMsgId) {
        this.parentMsgId = parentMsgId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(String envelope) {
        this.envelope = envelope;
    }

    public String getFailedDescription() {
        return failedDescription;
    }

    public void setFailedDescription(String failedDescription) {
        this.failedDescription = failedDescription;
    }

    public List<RequestInfoRpc> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestInfoRpc> requests) {
        this.requests = requests;
    }

    public List<ExternalCallInfoRpc> getExternalCalls() {
        return externalCalls;
    }

    public void setExternalCalls(List<ExternalCallInfoRpc> externalCalls) {
        this.externalCalls = externalCalls;
    }

    public List<MessageActionType> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<MessageActionType> allowedActions) {
        this.allowedActions = allowedActions;
    }

    /**
     * Converter to convert from Message entity.
     * @return filled in MessageRpc object.
     */
    public static Converter<Message, MessageRpc> fromMessage() {
        return source -> {
            final MessageRpc ret = fromMessage(new MessageRpc()).convert(source);
            // additional fields
            ret.setProcessId(source.getProcessId());
            if(source.getLastUpdateTimestamp() != null) {
                ret.setLastChange(
                        ZonedDateTime.ofInstant(source.getLastUpdateTimestamp(), ZoneId.systemDefault())
                );
            }
            ret.setFailedCount(source.getFailedCount());
            if(source.getMsgTimestamp() != null) {
                ret.setMsgTimestamp(
                        ZonedDateTime.ofInstant(source.getMsgTimestamp(), ZoneId.systemDefault())
                );
            }
            ret.setObjectId(source.getObjectId());
            ret.setEntityType(source.getEntityType());
            ret.setFunnelValue(source.getFunnelValue());
            ret.setFunnelComponentId(source.getFunnelComponentId());
            ret.setGuaranteedOrder(source.isGuaranteedOrder());
            ret.setExcludeFailedState(source.isExcludeFailedState());
            ret.setBusinessError(source.getBusinessError());
            ret.setParentMsgId(source.getParentMsgId());
            ret.setBody(source.getPayload());
            ret.setEnvelope(source.getEnvelope());
            ret.setFailedDescription(source.getFailedDesc());
            ret.setRequests(source.getRequests().stream()
                    .map(request -> RequestInfoRpc.fromRequest().convert(request))
                    .collect(Collectors.toList())
            );
            ret.setExternalCalls(source.getExternalCalls().stream()
                    .map(externalCall -> ExternalCallInfoRpc.fromExternalCall().convert(externalCall))
                    .collect(Collectors.toList())
            );
            ret.setAllowedActions(MessageHelper.allowedActions(source));
            return ret;
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("processId", processId)
                .append("lastChange", lastChange)
                .append("failedCount", failedCount)
                .append("msgTimestamp", msgTimestamp)
                .append("objectId", objectId)
                .append("entityType", entityType)
                .append("funnelValue", funnelValue)
                .append("funnelComponentId", funnelComponentId)
                .append("guaranteedOrder", guaranteedOrder)
                .append("excludeFailedState", excludeFailedState)
                .append("businessError", businessError)
                .append("parentMsgId", parentMsgId)
                // body skipped
                // envelope skipped
                .append("failedDescription", failedDescription)
                .append("requests", requests)
                .append("externalCalls", externalCalls)
                .toString();
    }
}
