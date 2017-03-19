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

package org.openhubframework.openhub.core.common.directcall;

import java.time.Instant;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;


/**
 * Encapsulates parameters for direct call.
 *
 * @author Petr Juza
 * @see DirectCallParams
 */
public class DirectCallParams {

    private Object header;
    private Object body;
    private String uri;
    private String senderRef;
    private String soapAction;
    private Instant creationTimestamp;

    public DirectCallParams(Object body, String uri, String senderRef, @Nullable String soapAction) {
        this(body, uri, senderRef, soapAction, null);
    }

    public DirectCallParams(Object body, String uri, String senderRef, @Nullable String soapAction, @Nullable String header) {
        Assert.notNull(body, "the body must not be null");
        Assert.hasText(uri, "the uri must not be empty");
        Assert.hasText(senderRef, "the senderRef must not be empty");

        this.body = body;
        this.uri = uri;
        this.senderRef = senderRef;
        this.soapAction = soapAction;
        this.header = header;
        this.creationTimestamp = Instant.now();
    }

    /**
     * Gets call body.
     *
     * @return body
     */
    public Object getBody() {
        return body;
    }

    /**
     * Gets call header.
     *
     * @return header
     */
    @Nullable
    public Object getHeader() {
        return header;
    }

    /**
     * Sets call header.
     *
     * @param header header
     */
    public void setHeader(@Nullable Object header) {
        this.header = header;
    }

    /**
     * Gets external system URI.
     *
     * @return external system URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets reference (= Spring bean name) to sender.
     *
     * @return sender reference
     */
    public String getSenderRef() {
        return senderRef;
    }

    /**
     * Gets SOAP action.
     *
     * @return SOAP action
     */
    @Nullable
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * Gets timestamp when these params were created.
     *
     * @return timestamp
     */
    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uri", uri)
                .append("senderRef", senderRef)
                .append("soapAction", soapAction)
                .append("header", header != null ? StringUtils.abbreviate(header.toString(), 100) : "")
                .append("body", StringUtils.abbreviate(body.toString(), 100))
                .toString();
    }
}
