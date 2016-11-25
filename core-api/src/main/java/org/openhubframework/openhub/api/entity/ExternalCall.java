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

import java.time.Instant;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.HumanReadable;


/**
 * Evidence of calls to external systems (billing, VF, ...).
 * This table serves for checking of duplication calls to external systems and checks obsolete calls.
 * <p>
 * Entity ID contains real entity ID for operations which change existing data
 * or correlationID for operations which creates new data.
 * <p>
 * Special case are confirmations which have operation with the name "{@value #CONFIRM_OPERATION}"
 * and entity ID will be set to {@link Message#getMsgId() message ID}.
 * There are only confirmations which failed previously.
 *
 * @author Petr Juza
 */
@Entity
@Table(name = "external_call",
        uniqueConstraints = @UniqueConstraint(name = "uq_operation_entity_id",
                columnNames = {"operation_name", "entity_id"}))
public class ExternalCall implements HumanReadable {

    public static final String CONFIRM_OPERATION = "confirmation";

    @Id
    @Column(name = "call_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "openhub_id_sequence")
    @SequenceGenerator(name="openhub_id_sequence", sequenceName="openhub_sequence", allocationSize=1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "msg_id", nullable = false)
    private Message message;

    @Column(name = "msg_id", nullable = false, insertable = false, updatable = false)
    private Long msgId;

    @Column(name = "operation_name", length = 100, nullable = false)
    private String operationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20, nullable = false)
    private ExternalCallStateEnum state;

    @Column(name = "entity_id", length = 150, nullable = false)
    private String entityId;

    @Column(name = "msg_timestamp", nullable = false)
    private Instant msgTimestamp;

    @Column(name = "creation_timestamp", nullable = false)
    private Instant creationTimestamp;

    @Version
    @Column(name = "last_update_timestamp", nullable = false)
    private Instant lastUpdateTimestamp;

    @Column(name = "failed_count", nullable = false)
    private int failedCount = 0;

    /** Default public constructor. */
    public ExternalCall() {
    }

    /**
     * Creates new {@link ExternalCallStateEnum#FAILED failed} confirmation call.
     *
     * @param msg the message
     * @return external call entity
     */
    public static ExternalCall createFailedConfirmation(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        Instant currDate = Instant.now();

        ExternalCall extCall = new ExternalCall();
        extCall.setMessage(msg);
        extCall.setOperationName(CONFIRM_OPERATION);
        extCall.setState(ExternalCallStateEnum.FAILED);
        extCall.setEntityId(msg.getCorrelationId());
        extCall.setCreationTimestamp(currDate);
        extCall.setLastUpdateTimestamp(currDate);
        extCall.setMsgTimestamp(msg.getMsgTimestamp());
        extCall.setFailedCount(1);

        return extCall;
    }

    /**
     * Creates a new external call with {@link ExternalCallStateEnum#PROCESSING processing} state.
     *
     * @param operationName the name of operation 
     * @param entityId  the ID of entity (external ID as constraint of processing)
     * @param msg the message
     * @return external call entity
     */
    public static ExternalCall createProcessingCall(String operationName, String entityId, Message msg) {
        Assert.notNull(operationName, "operationName (uri) must not be null");
        Assert.notNull(entityId, "entityId (operation key) must not be null");
        Assert.notNull(msg, "msg must not be null");

        Instant currDate = Instant.now();

        ExternalCall extCall = new ExternalCall();
        extCall.setCreationTimestamp(currDate);
        extCall.setFailedCount(0);

        extCall.setMessage(msg);
        extCall.setMsgId(msg.getMsgId());
        extCall.setMsgTimestamp(msg.getMsgTimestamp());

        extCall.setOperationName(operationName);
        extCall.setEntityId(entityId);

        extCall.setState(ExternalCallStateEnum.PROCESSING);
        extCall.setLastUpdateTimestamp(currDate);

        return extCall;
    }

    /**
     * Gets unique external call ID.
     *
     * @return unique ID
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets parent message ID.
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
     * Gets main asynch. message.
     *
     * @return message
     */
    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Gets target operation identification (e.g. target system name + operation name).
     *
     * @return operation name
     */
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Gets state of processing.
     *
     * @return state
     */
    public ExternalCallStateEnum getState() {
        return state;
    }

    public void setState(ExternalCallStateEnum state) {
        this.state = state;
    }

    /**
     * Gets entity ID.
     *
     * @return entity ID
     */
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets creation date of this entity.
     *
     * @return creation date
     */
    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * Gets timestamp when the entity was changed last time.
     *
     * @return timestamp
     */
    public Instant getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Instant lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    /**
     * Gets message timestamp from source system.
     *
     * @return message timestamp
     * @see Message#getMsgTimestamp()
     */
    public Instant getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(Instant msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    /**
     * Gets count of failed tries.
     * This value has sense only when {@link #isConfirmationCall()} is {@code true}.
     *
     * @return failedCount of failed tries
     */
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * Gets {@code true} when external call represents confirmation call.
     *
     * @return {@code true} for confirmation call, otherwise {@code false}
     */
    public boolean isConfirmationCall() {
        return operationName.equals(CONFIRM_OPERATION);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ExternalCall) {
            ExternalCall en = (ExternalCall) obj;

            return new EqualsBuilder()
                    .append(getId(), en.getId())
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("msgId", msgId)
                .append("operationName", operationName)
                .append("state", state)
                .append("entityId", entityId)
                .append("msgTimestamp", msgTimestamp)
                .append("creationTimestamp", creationTimestamp)
                .append("lastUpdateTimestamp", lastUpdateTimestamp)
                .append("failedCount", failedCount)
                .toString();
    }

    @Override
    public String toHumanString() {
        return "(id = " + id + ", operationName = " + operationName + ", entityId = " + entityId + ")";
    }
}
