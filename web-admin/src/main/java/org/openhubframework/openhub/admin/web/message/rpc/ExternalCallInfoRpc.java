package org.openhubframework.openhub.admin.web.message.rpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.admin.web.common.rpc.BaseRpc;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.springframework.core.convert.converter.Converter;


/**
 * ExternalCall related to the message.
 *
 * @author Karel Kovarik
 */
public class ExternalCallInfoRpc extends BaseRpc<ExternalCall, Long> {

    private ExternalCallStateEnum state;
    private String operationName;
    private String callId;
    private ZonedDateTime lastChange;

    public ExternalCallStateEnum getState() {
        return state;
    }

    public void setState(ExternalCallStateEnum state) {
        this.state = state;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public ZonedDateTime getLastChange() {
        return lastChange;
    }

    public void setLastChange(ZonedDateTime lastChange) {
        this.lastChange = lastChange;
    }

    public static Converter<ExternalCall, ExternalCallInfoRpc> fromExternalCall() {
        return source -> {
            final ExternalCallInfoRpc ret = new ExternalCallInfoRpc();
            ret.setId(source.getId());
            ret.setState(source.getState());
            ret.setOperationName(source.getOperationName());
            ret.setCallId(source.getEntityId());
            ret.setLastChange(ZonedDateTime.ofInstant(source.getLastUpdateTimestamp(), ZoneId.systemDefault()));
            return ret;
        };
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("state", state)
                .append("operationName", operationName)
                .append("callId", callId)
                .append("lastChange", lastChange)
                .toString();
    }
}
