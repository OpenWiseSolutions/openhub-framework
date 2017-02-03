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

package org.openhubframework.openhub.api.asynch.msg;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.*;


/**
 * Represents child message.
 *
 * @author Petr Juza
 * @see Message
 */
public final class ChildMessage {

    private Message parentMessage;

    private BindingTypeEnum bindingType = BindingTypeEnum.HARD;

    private ServiceExtEnum service;

    private String operationName;

    private String objectId;

    private EntityTypeExtEnum entityType;

    private String body;

    private String funnelValue;

    /**
     * Creates child message with specified binding type to parent message.
     *
     *
     * @param parentMessage the parent message
     * @param bindingType the binding type to parent message
     * @param service the service name, e.g. customer
     * @param operationName the operation name, e.g. createCustomer
     * @param body the new message (XML) body
     * @param objectId the object ID that will be changed during message processing.
     *                   This parameter serves for finding messages in the queue which deals with identical object.
     * @param entityType the type of the entity that is being changed.
     *                   This parameter serves for finding messages in the queue which deals with identical object,
     *                   see {@link Message#getEntityTypeInternal()} for more details
     * @param funnelValue the funnel value
     *
     */
    public ChildMessage(Message parentMessage, BindingTypeEnum bindingType, ServiceExtEnum service, String operationName,
            String body, @Nullable String objectId, @Nullable EntityTypeExtEnum entityType, @Nullable String funnelValue) {

        Assert.notNull(parentMessage, "the parentMessage must not be null");
        Assert.notNull(bindingType, "the bindingType must not be null");
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be null");
        Assert.hasText(body, "the body must not be empty");

        this.parentMessage = parentMessage;
        this.bindingType = bindingType;
        this.service = service;
        this.operationName = operationName;
        this.objectId = objectId;
        this.entityType = entityType;
        this.body = body;
        this.funnelValue = funnelValue;
    }

    /**
     * Creates child message with {@link BindingTypeEnum#HARD HARD} binding type to parent message.
     *
     * @param parentMessage the parent message
     * @param service the service name, e.g. customer.
     * @param operationName the operation name, e.g. createCustomer.
     * @param body the new message (XML) body
     */
    public ChildMessage(Message parentMessage, ServiceExtEnum service, String operationName, String body) {
        this(parentMessage, BindingTypeEnum.HARD, service, operationName, body, null, null, null);
    }

    /**
     * Converts {@link ChildMessage} into a full {@link Message} that can be persisted or processed.
     *
     * @param childMsg the child message info
     * @return a new Message that is child of {@link ChildMessage#getParentMessage()}
     */
    public static Message createMessage(ChildMessage childMsg) {
        Assert.notNull(childMsg, "childMsg must not be null");

        Message parentMsg = childMsg.getParentMessage();

        if (childMsg.getBindingType() == BindingTypeEnum.HARD) {
            parentMsg.setParentMessage(true);
        }

        Date currDate = new Date();

        Message msg = new Message();

        // new fields
        msg.setState(MsgStateEnum.NEW);
        msg.setStartProcessTimestamp(currDate);
        msg.setCorrelationId(UUID.randomUUID().toString());
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(parentMsg.getSourceSystem());

        // fields from parent
        msg.setParentMsgId(parentMsg.getMsgId());
        msg.setParentBindingType(childMsg.getBindingType());
        msg.setMsgTimestamp(parentMsg.getMsgTimestamp());
        msg.setReceiveTimestamp(parentMsg.getReceiveTimestamp());
        msg.setProcessId(parentMsg.getProcessId());

        // fields from child
        msg.setService(childMsg.getService());
        msg.setOperationName(childMsg.getOperationName());
        msg.setObjectId(childMsg.getObjectId());
        msg.setEntityType(childMsg.getEntityType());
        msg.setPayload(childMsg.getBody());
        msg.setFunnelValue(childMsg.getFunnelValue());

        return msg;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public BindingTypeEnum getBindingType() {
        return bindingType;
    }

    public ServiceExtEnum getService() {
        return service;
    }

    public String getOperationName() {
        return operationName;
    }

    @Nullable
    public String getObjectId() {
        return objectId;
    }

    @Nullable
    public EntityTypeExtEnum getEntityType() {
        return entityType;
    }

    public String getBody() {
        return body;
    }

    @Nullable
    public String getFunnelValue() {
        return funnelValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("parentMessage", parentMessage)
            .append("bindingType", bindingType)
            .append("service", service != null ? service.getServiceName() : null)
            .append("operationName", operationName)
            .append("objectId", objectId)
            .append("entityType", entityType != null ? entityType.getEntityType() : null)
            .append("funnelValue", funnelValue)
            .append("body", StringUtils.substring(body, 0, 500))
            .toString();
    }
}
