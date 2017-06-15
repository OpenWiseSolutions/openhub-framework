package org.openhubframework.openhub.admin.web.message.rpc;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Message filter rpc object.
 * 
 * @author Karel Kovarik
 */
public class MessageFilterRpc {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime receivedFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime receivedTo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastChangeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime lastChangeTo;
    private String sourceSystem;
    private String correlationId;
    private String processId;
    private MsgStateEnum state;
    private String errorCode;
    private String serviceName;
    private String operationName;
    private String fulltext;

    public OffsetDateTime getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(OffsetDateTime receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public OffsetDateTime getReceivedTo() {
        return receivedTo;
    }

    public void setReceivedTo(OffsetDateTime receivedTo) {
        this.receivedTo = receivedTo;
    }

    public OffsetDateTime getLastChangeFrom() {
        return lastChangeFrom;
    }

    public void setLastChangeFrom(OffsetDateTime lastChangeFrom) {
        this.lastChangeFrom = lastChangeFrom;
    }

    public OffsetDateTime getLastChangeTo() {
        return lastChangeTo;
    }

    public void setLastChangeTo(OffsetDateTime lastChangeTo) {
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

    public static Converter<MessageFilterRpc, MessageFilter> toMessageFilter() {
        return source -> {
            final MessageFilter ret = new MessageFilter();
            if(source.getReceivedFrom() != null) {
                ret.setReceivedFrom(source.getReceivedFrom().toInstant());
            }
            if(source.getReceivedTo() != null) {
                ret.setReceivedTo(source.getReceivedTo().toInstant());
            }
            if(source.getLastChangeFrom() != null) {
                ret.setLastChangeFrom(source.getLastChangeFrom().toInstant());
            }
            if(source.getLastChangeTo() != null) {
                ret.setLastChangeTo(source.getLastChangeTo().toInstant());
            }
            ret.setSourceSystem(source.getSourceSystem());
            ret.setCorrelationId(source.getCorrelationId());
            ret.setProcessId(source.getProcessId());
            ret.setState(source.getState());
            ret.setErrorCode(source.getErrorCode());
            ret.setServiceName(source.getServiceName());
            ret.setOperationName(source.getOperationName());
            ret.setFulltext(source.getFulltext());
            return ret;
        };
    }
}
