package org.openhubframework.openhub.admin.web.message.rpc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.admin.web.common.rpc.BaseRpc;
import org.openhubframework.openhub.api.entity.Response;
import org.springframework.core.convert.converter.Converter;


/**
 * Response related to the request.
 *
 * @author Karel Kovarik
 */
public class ResponseInfoRpc extends BaseRpc<Response, Long> {

    private ZonedDateTime timestamp;
    private String payload;

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public static Converter<Response, ResponseInfoRpc> fromResponse() {
        return source -> {
            final ResponseInfoRpc ret = new ResponseInfoRpc();
            ret.setId(source.getId());
            if(source.getResTimestamp() != null) {
                ret.setTimestamp(
                        ZonedDateTime.ofInstant(source.getResTimestamp(), ZoneId.systemDefault())
                );
            }
            ret.setPayload(source.getResponse());
            return ret;
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("timestamp", timestamp)
                // no payload
                .toString();
    }
}
