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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.openhubframework.openhub.api.common.HumanReadable;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;


/**
 * Input asynchronous message.
 *
 * @author Petr Juza
 */
@Entity
@Table(name = "message",
        uniqueConstraints = @UniqueConstraint(name = "uq_correlation_system",
                columnNames = {"correlation_id", "source_system"}))
public class Message implements HumanReadable {

    /**
     * Separator that separates business error descriptions.
     */
    public static final String ERR_DESC_SEPARATOR = "||";

    @Id
    @Column(name = "msg_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long msgId;

    @Column(name = "msg_timestamp", nullable = false)
    private Date msgTimestamp;

    @Column(name = "receive_timestamp", nullable = false)
    private Date receiveTimestamp;

    @Column(name = "service", length = 30, nullable = false)
    @Access(AccessType.PROPERTY)
    private String serviceInternal;

    @Transient
    private ServiceExtEnum service;

    @Column(name = "operation_name", length = 100, nullable = false)
    private String operationName;

    @Column(name = "object_id", length = 50, nullable = true)
    private String objectId;

    @Column(name = "entity_type", length = 30, nullable = true)
    @Access(AccessType.PROPERTY)
    private String entityTypeInternal;

    @Transient
    private EntityTypeExtEnum entityType;

    @Column(name = "correlation_id", length = 100, nullable = false)
    private String correlationId;

    @Column(name = "process_id", length = 100, nullable = true)
    private String processId;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "payload", length = Integer.MAX_VALUE, nullable = false)
    private String payload;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "envelope", length = Integer.MAX_VALUE, nullable = true)
    private String envelope;

    @Column(name = "source_system", length = 15, nullable = false)
    @Access(AccessType.PROPERTY)
    private String sourceSystemInternal;

    @Transient
    private ExternalSystemExtEnum sourceSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 25, nullable = false)
    private MsgStateEnum state;

    @Column(name = "start_process_timestamp", nullable = true)
    private Date startProcessTimestamp;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "failed_error_code", length = 5, nullable = true)
    @Access(AccessType.PROPERTY)
    private String failedErrorCodeInternal;

    @Transient
    private ErrorExtEnum failedErrorCode;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "failed_desc", length = Integer.MAX_VALUE, nullable = true)
    private String failedDesc;

    @Column(name = "last_update_timestamp", nullable = true)
    private Date lastUpdateTimestamp;

    @Column(name = "custom_data", length = 20000, nullable = true)
    private String customData;

    @Column(name = "business_error", length = 20000, nullable = true)
    private String businessError;

    @Column(name = "parent_msg_id", nullable = true)
    private Long parentMsgId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_binding_type", length = 25, nullable = true)
    private BindingTypeEnum parentBindingType;

    @Column(name = "funnel_value", length = 50, nullable = true)
    private String funnelValue;

    @Column(name = "funnel_component_id", length = 50, nullable = true)
    private String funnelComponentId;

    @Column(name = "guaranteed_order", nullable = false)
    private boolean guaranteedOrder;

    @Column(name = "exclude_failed_state", nullable = false)
    private boolean excludeFailedState;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    private Set<ExternalCall> externalCalls = new TreeSet<ExternalCall>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    private Set<Request> requests = new TreeSet<Request>();

    @Transient
    private boolean parentMessage;

    @Transient
    private int processingPriority;


    /**
     * Empty (default) constructor.
     */
    public Message() {
    }

    /**
     * Creates message with specified source system and correlation ID.
     *
     * @param sourceSystem the source system
     * @param correlationId the correlation ID
     */
    public Message(ExternalSystemExtEnum sourceSystem, String correlationId) {
        Assert.notNull(sourceSystem, "the sourceSystem must not be null");
        Assert.hasText(correlationId, "the correlationId must not be empty");

        setSourceSystem(sourceSystem);
        this.correlationId = correlationId;
    }

    /**
     * Gets unique message ID.
     *
     * @return message ID
     */
    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    /**
     * Gets timestamp from source system.
     *
     * @return timestamp
     */
    public Date getMsgTimestamp() {
        return msgTimestamp != null ? new Date(msgTimestamp.getTime()) : null;
    }

    public void setMsgTimestamp(Date msgTimestamp) {
        Assert.notNull(msgTimestamp, "the msgTimestamp must not be null");

        this.msgTimestamp = msgTimestamp != null ? new Date(msgTimestamp.getTime()) : null;
    }

    /**
     * Gets timestamp when message was received.
     * This timestamp determines the order of next processing.
     *
     * @return timestamp
     */
    public Date getReceiveTimestamp() {
        return receiveTimestamp != null ? new Date(receiveTimestamp.getTime()) : null;
    }

    public void setReceiveTimestamp(Date receiveTimestamp) {
        Assert.notNull(receiveTimestamp, "the receiveTimestamp must not be null");

        this.receiveTimestamp = receiveTimestamp != null ? new Date(receiveTimestamp.getTime()) : null;
    }

    /**
     * Gets service name, e.g. customer.
     *
     * @return service name
     */
    public ServiceExtEnum getService() {
        return service;
    }

    public void setService(ServiceExtEnum service) {
        this.service = service;
        this.serviceInternal = service.getServiceName();
    }

    private String getServiceInternal() {
        return serviceInternal;
    }

    private void setServiceInternal(final String service) {
        this.serviceInternal = service;

        this.service = new ServiceExtEnum() {
            @Override
            public final String getServiceName() {
                return service;
            }
        };
    }

    /**
     * Gets operation name, e.g. createCustomer.
     *
     * @return op. name
     */
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Gets object ID that will be changed during message processing.
     * This attribute serves for finding messages in the queue which deals with identical object.
     *
     * @return object ID
     * @see #getEntityTypeInternal()
     */
    @Nullable
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(@Nullable String objectId) {
        this.objectId = objectId;
    }

    /**
     * Gets type of the entity that is being changed.
     * <p/>
     * In general it's enough to detect identical changed data by objectId and operation name but there are
     * few different operations which can change the same data (e.g. setCustomer, setCustomerExt).
     * <p/>
     * If defined then it will be used for "obsolete operation call" detection instead of operation name.
     *
     * @return entity type
     * @see #getObjectId()
     */
    @Nullable
    public EntityTypeExtEnum getEntityType() {
        return entityType;
    }

    public void setEntityType(@Nullable EntityTypeExtEnum entityType) {
        this.entityType = entityType;
        this.entityTypeInternal = entityType != null ? entityType.getEntityType() : null;
    }

    @Nullable
    private String getEntityTypeInternal() {
        return entityTypeInternal;
    }

    private void setEntityTypeInternal(@Nullable final String entityTypeInternal) {
        this.entityTypeInternal = entityTypeInternal;
        if (entityTypeInternal != null) {
            this.entityType = new EntityTypeExtEnum() {

                @Override
                public String getEntityType() {
                    return entityTypeInternal;
                }
            };
        } else {
            this.entityType = null;
        }
    }

    /**
     * Gets correlation ID that serves for pairing asynchronous request and response.
     *
     * @return correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets process ID that serves for pairing more requests with one process.
     *
     * @return process ID
     */
    @Nullable
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(@Nullable String processId) {
        this.processId = processId;
    }

    /**
     * Gets body content (XML by default).
     *
     * @return body content
     */
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * Gets the whole SOAP envelope (= the full original request: SOAP headers, SOAP body).
     *
     * @return the envelope; can be {@code null} because it's possible to turn off saving the envelope
     *      with regards to performance
     */
    @Nullable
    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(@Nullable String envelope) {
        this.envelope = envelope;
    }

    /**
     * Gets source system.
     *
     * @return source system
     */
    public ExternalSystemExtEnum getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(ExternalSystemExtEnum sourceSystem) {
        Assert.notNull(sourceSystem, "the sourceSystem must not be null");

        this.sourceSystem = sourceSystem;
        this.sourceSystemInternal = sourceSystem.getSystemName();
    }

    private String getSourceSystemInternal() {
        return sourceSystemInternal;
    }

    private void setSourceSystemInternal(final String sourceSystem) {
        Assert.notNull(sourceSystem, "the sourceSystem must not be null");

        this.sourceSystemInternal = sourceSystem;
        this.sourceSystem = new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return sourceSystem;
            }
        };
    }

    /**
     * Gets message state.
     *
     * @return msg state
     */
    public MsgStateEnum getState() {
        return state;
    }

    public void setState(MsgStateEnum state) {
        this.state = state;
    }

    /**
     * Gets timestamp when the message started processing.
     * When message starts repeatably then this time is valid for last processing.
     *
     * @return start date of processing
     */
    public Date getStartProcessTimestamp() {
        return startProcessTimestamp != null ? new Date(startProcessTimestamp.getTime()) : null;
    }

    public void setStartProcessTimestamp(Date startProcessTimestamp) {
        Assert.notNull(startProcessTimestamp, "the processTimestamp must not be null");

        this.startProcessTimestamp = startProcessTimestamp != null ? new Date(startProcessTimestamp.getTime()) : null;
    }

    /**
     * Gets number of failed processing.
     *
     * @return number of failed processing
     */
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * Gets error code if last try was unsuccessful with error.
     * <p/>
     * Note: there can be only one error during message processing because next processing is stopped
     * when an error occurred.
     *
     * @return error code
     */
    @Nullable
    public ErrorExtEnum getFailedErrorCode() {
        return failedErrorCode;
    }

    public void setFailedErrorCode(@Nullable ErrorExtEnum failedErrorCode) {
        this.failedErrorCode = failedErrorCode;
        this.failedErrorCodeInternal = failedErrorCode != null ? failedErrorCode.getErrorCode() : null;
    }

    @Nullable
    private String getFailedErrorCodeInternal() {
        return failedErrorCodeInternal;
    }

    private void setFailedErrorCodeInternal(@Nullable final String failedErrorCodeInternal) {
        this.failedErrorCodeInternal = failedErrorCodeInternal;
        if (failedErrorCodeInternal != null) {
            this.failedErrorCode = new ErrorExtEnum() {
                @Override
                public String getErrorCode() {
                    return failedErrorCodeInternal;
                }

                @Override
                public String getErrDesc() {
                    // no description available
                    return failedErrorCodeInternal;
                }
            };
        } else {
            this.failedErrorCode = null;
        }
    }

    /**
     * Gets error description if last try was unsuccessful with error.
     *
     * @return error description
     */
    @Nullable
    public String getFailedDesc() {
        return failedDesc;
    }

    public void setFailedDesc(@Nullable String failedDesc) {
        this.failedDesc = failedDesc;
    }

    /**
     * Gets timestamp when the entity was changed last time.
     *
     * @return timestamp
     */
    @Nullable
    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp != null ? new Date(lastUpdateTimestamp.getTime()) : null;
    }

    public void setLastUpdateTimestamp(@Nullable Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp != null ? new Date(lastUpdateTimestamp.getTime()) : null;
    }

    /**
     * Gets custom data.
     * <p/>
     * Custom data can be used for saving arbitrary data for transferring state between more processing calls
     * of the asynchronous message.
     *
     * @return the custom data
     */
    @Nullable
    public String getCustomData() {
        return customData;
    }

    public void setCustomData(@Nullable String customData) {
        this.customData = customData;
    }

    /**
     * Gets business error descriptions.
     * <p/>
     * During processing of asynchronous message can be collected lot of business errors which is suitable
     * to present to source (callee) system (for example when we want to create new customer and this customer
     * doesn't have valid customer number).
     * <p/>
     * Each error description is separated by {@value #ERR_DESC_SEPARATOR}.
     *
     * @return business error descriptions
     */
    @Nullable
    public String getBusinessError() {
        return businessError;
    }

    /**
     * Gets list of business error descriptions.
     *
     * @return list of descriptions
     */
    public List<String> getBusinessErrorList() {
        if (getBusinessError() == null) {
            return Collections.emptyList();
        } else {
            String[] errs = StringUtils.split(getBusinessError(), ERR_DESC_SEPARATOR);

            return Arrays.asList(errs);
        }
    }

    public void setBusinessError(@Nullable String businessError) {
        this.businessError = businessError;
    }

    /**
     * Gets ID of the parent message (if any).
     *
     * @return parent message ID
     * @see #getParentBindingType()
     */
    @Nullable
    public Long getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(@Nullable Long parentMsgId) {
        this.parentMsgId = parentMsgId;

        // set default value if not set
        if (getParentBindingType() == null) {
            setParentBindingType(BindingTypeEnum.SOFT);
        }
    }

    /**
     * Gets type of binding between parent and child message.
     *
     * @return binding type
     * @see #getParentMsgId()
     */
    @Nullable
    public BindingTypeEnum getParentBindingType() {
        return parentBindingType;
    }

    public void setParentBindingType(@Nullable BindingTypeEnum parentBindingType) {
        this.parentBindingType = parentBindingType;
    }

    /**
     * Is there message that has parent message with {@link BindingTypeEnum#HARD hard} binding?
     *
     * @return {@code true} for hard-binding parent message otherwise {@code false}
     */
    public boolean existHardParent() {
        return getParentMsgId() != null && getParentBindingType() == BindingTypeEnum.HARD;
    }

    /**
     * Is parent message that has child messages?
     * <p/>
     * Note: binding between child and parent message must be {@link BindingTypeEnum#HARD hard}.
     *
     * @return {@code true} when this message is parent, otherwise {@code false}
     */
    public boolean isParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(boolean parentMessage) {
        this.parentMessage = parentMessage;
    }

    /**
     * Gets value for funnel filtering - you can have funnel that will ensure that there is only one processing
     * message with same funnel value.
     *
     * @return funnel value
     * @see #getFunnelComponentId()
     */
    public String getFunnelValue() {
        return funnelValue;
    }

    public void setFunnelValue(String funnelValue) {
        this.funnelValue = funnelValue;
    }

    /**
     * Gets funnel component identifier.
     * Each funnel has unique identifier that says where is route currently being processed.
     *
     * @return funnel component identifier
     * @see #getFunnelValue()
     */
    public String getFunnelComponentId() {
        return funnelComponentId;
    }

    public void setFunnelComponentId(String funnelComponentId) {
        this.funnelComponentId = funnelComponentId;
    }

    /**
     * Gets flag (true/false) if route should be processed in guaranteed order or not.
     *
     * @return {@code true} for guaranteed order otherwise {@code false}
     * @see #getFunnelValue()
     * @see #isExcludeFailedState()
     */
    public boolean isGuaranteedOrder() {
        return guaranteedOrder;
    }

    public void setGuaranteedOrder(boolean guaranteedOrder) {
        this.guaranteedOrder = guaranteedOrder;
    }

    /**
     * Returns {@code true} if FAILED state should be excluded from guaranteed order.
     * {@link MsgStateEnum#FAILED FAILED} state is used for guaranteed order by default;
     * <p/>
     * This option has influence only if {@link #isGuaranteedOrder() guaranteed processing order} is enabled.
     *
     * @return {@code true} if FAILED state should be excluded
     * @see #isGuaranteedOrder()
     */
    public boolean isExcludeFailedState() {
        return excludeFailedState;
    }

    public void setExcludeFailedState(boolean excludeFailedState) {
        this.excludeFailedState = excludeFailedState;
    }

    /**
     * Gets the set of referenced external calls.
     *
     * @return the set of referenced external calls
     */
    public List<ExternalCall> getExternalCalls() {
        final List<ExternalCall> result = new ArrayList<ExternalCall>(externalCalls);
        Collections.sort(result, new Comparator<ExternalCall>() {
            @Override
            public int compare(ExternalCall o1, ExternalCall o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    /**
     * Gets the set of referenced logged requests.
     *
     * @return the set of referenced logged requests
     */
    public List<Request> getRequests() {
        final List<Request> result = new ArrayList<Request>(requests);
        Collections.sort(result, new Comparator<Request>() {
            @Override
            public int compare(Request o1, Request o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    public int getProcessingPriority() {
        return processingPriority;
    }

    /**
     * Sets priority of processing this message.
     * The higher number the higher priority.
     *
     * @param processingPriority the priority number
     */
    public void setProcessingPriority(int processingPriority) {
        this.processingPriority = processingPriority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Message) {
            Message en = (Message) obj;

            return new EqualsBuilder()
                    .append(msgId, en.msgId)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(msgId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("msgId", msgId)
            .append("state", state)
            .append("correlationId", correlationId)
            .append("processId", processId)
            .append("msgTimestamp", msgTimestamp)
            .append("receiveTimestamp", receiveTimestamp)
            .append("service", service == null ? null : service.getServiceName())
            .append("operationName", operationName)
            .append("objectId", objectId)
            .append("entityType", entityType != null ? entityType.getEntityType() : null)
//            .append("payload", StringUtils.substring(payload, 0, 500))
            .append("sourceSystem", sourceSystem != null ? sourceSystem.getSystemName() : null)
            .append("startProcessTimestamp", startProcessTimestamp)
            .append("failedCount", failedCount)
            .append("failedErrorCode", failedErrorCode)
//            .append("failedDesc", StringUtils.substring(payload, 0, 200))
            .append("lastUpdateTimestamp", lastUpdateTimestamp)
            .append("customData", StringUtils.substring(customData, 0, 200))
            .append("businessError", StringUtils.substring(businessError, 0, 200))
            .append("parentMsgId", parentMsgId)
            .append("parentBindingType", parentBindingType)
            .append("funnelValue", funnelValue)
            .append("funnelComponentId", funnelComponentId)
            .append("guaranteedOrder", guaranteedOrder)
            .append("excludeFailedState", excludeFailedState)
            .append("processingPriority", processingPriority)
            .toString();
    }

    @Override
    public String toHumanString() {
        return "(msg_id = " + msgId + ", correlationId = " + correlationId + ")";
    }
}
