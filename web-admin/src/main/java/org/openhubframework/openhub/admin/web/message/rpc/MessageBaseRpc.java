package org.openhubframework.openhub.admin.web.message.rpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.admin.web.common.rpc.BaseRpc;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.springframework.core.convert.converter.Converter;


/**
 * Base RPC for message entities.
 *
 * @author Karel Kovarik
 * @see Message entity.
 */
public abstract class MessageBaseRpc extends BaseRpc<Message, Long> {

    private String correlationId;
    private ExternalSystemExtEnum sourceSystem;
    private ZonedDateTime received;
    private ZonedDateTime processingStarted;
    private MsgStateEnum state;
    private ErrorExtEnum errorCode;
    private ServiceExtEnum serviceName;
    private String operationName;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public ExternalSystemExtEnum getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(ExternalSystemExtEnum sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public ZonedDateTime getReceived() {
        return received;
    }

    public void setReceived(ZonedDateTime received) {
        this.received = received;
    }

    public ZonedDateTime getProcessingStarted() {
        return processingStarted;
    }

    public void setProcessingStarted(ZonedDateTime processingStarted) {
        this.processingStarted = processingStarted;
    }

    public MsgStateEnum getState() {
        return state;
    }

    public void setState(MsgStateEnum state) {
        this.state = state;
    }

    public ErrorExtEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorExtEnum errorCode) {
        this.errorCode = errorCode;
    }

    public ServiceExtEnum getServiceName() {
        return serviceName;
    }

    public void setServiceName(ServiceExtEnum serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    protected static <P extends MessageBaseRpc> Converter<Message, P> fromMessage(P message) {
        return source -> {
            message.setId(source.getId());
            message.setCorrelationId(source.getCorrelationId());
            message.setSourceSystem(source.getSourceSystem());
            message.setReceived(
                    ZonedDateTime.ofInstant(source.getReceiveTimestamp(), ZoneId.systemDefault()));
            if(source.getStartProcessTimestamp() != null) {
                message.setProcessingStarted(
                        ZonedDateTime.ofInstant(source.getStartProcessTimestamp(), ZoneId.systemDefault()));
            }
            message.setState(source.getState());
            message.setErrorCode(source.getFailedErrorCode());
            message.setServiceName(source.getService());
            message.setOperationName(source.getOperationName());
            return message;
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("correlationId", correlationId)
                .append("sourceSystem", sourceSystem)
                .append("received", received)
                .append("processingStarted", processingStarted)
                .append("state", state)
                .append("errorCode", errorCode)
                .append("serviceName", serviceName)
                .append("operationName", operationName)
                .toString();
    }
}
