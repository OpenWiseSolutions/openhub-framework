package org.openhubframework.openhub.api.entity;

import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
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

    public Instant getReceivedTo() {
        return receivedTo;
    }

    public void setReceivedTo(Instant receivedTo) {
        this.receivedTo = receivedTo;
    }

    public Instant getLastChangeFrom() {
        return lastChangeFrom;
    }

    public void setLastChangeFrom(Instant lastChangeFrom) {
        this.lastChangeFrom = lastChangeFrom;
    }

    public Instant getLastChangeTo() {
        return lastChangeTo;
    }

    public void setLastChangeTo(Instant lastChangeTo) {
        this.lastChangeTo = lastChangeTo;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public MsgStateEnum getState() {
        return state;
    }

    public void setState(MsgStateEnum state) {
        this.state = state;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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
