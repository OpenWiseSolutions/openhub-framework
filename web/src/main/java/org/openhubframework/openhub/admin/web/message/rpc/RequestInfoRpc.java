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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.admin.web.common.rpc.BaseRpc;
import org.openhubframework.openhub.api.entity.Request;
import org.springframework.core.convert.converter.Converter;


/**
 * Request related to the message.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class RequestInfoRpc extends BaseRpc<Request, Long> {

    private String uri;
    private ZonedDateTime timestamp;
    private String payload;
    private ResponseInfoRpc response;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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

    public ResponseInfoRpc getResponse() {
        return response;
    }

    public void setResponse(ResponseInfoRpc response) {
        this.response = response;
    }

    public static Converter<Request, RequestInfoRpc> fromRequest() {
        return source -> {
            final RequestInfoRpc ret = new RequestInfoRpc();
            ret.setId(source.getId());
            ret.setUri(source.getUri());
            ret.setTimestamp(ZonedDateTime.ofInstant(source.getReqTimestamp(), ZoneId.systemDefault()));
            ret.setPayload(source.getRequest());
            if(source.getResponse() != null) {
                ret.setResponse(ResponseInfoRpc.fromResponse().convert(source.getResponse()));
            }
            return ret;
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("uri", uri)
                .append("timestamp", timestamp)
                // no payload
                .append("response", response)
                .toString();
    }
}
