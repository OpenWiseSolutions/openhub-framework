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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.HumanReadable;


/**
 * Entity for saving request with external system or during internal communication.
 * <p>
 * Request is uniquely identified by its {@link #getUri() URI} and {@link #getResponseJoinId() response join ID}.
 * Both these attributes helps to join right request and response together.
 *
 * @author Petr Juza
 * @see Response
 * @since 0.4
 */
@Entity
@Table(name = "request")
public class Request implements HumanReadable {

    private static final int URI_MAX_LENGTH = 400;
    /**
     * URI pattern is used for normalize functionality
     * E.g.: spring-ws://http://slc-ogs-t01.centropol.cz:8080/CIMEntPlatform-war/CIMOpsWS?messageFactory= would be
     * normalized to spring-ws://http://slc-ogs-t01.centropol.cz:8080/CIMEntPlatform-war/CIMOpsWS.
     */
    private static final Pattern NORMALIZED_URI_PATTERN = Pattern.compile("(.*?)(\\?.*)");

    @Id
    @Column(name = "req_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "openhub_id_sequence")
    @SequenceGenerator(name="openhub_id_sequence", sequenceName="openhub_sequence", allocationSize=1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "msg_id", nullable = true)
    private Message message;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "request")
    private Response response;

    @Column(name = "msg_id", nullable = true, insertable = false, updatable = false)
    private Long msgId;

    @Column(name = "res_join_id", length = 100, nullable = false)
    private String responseJoinId;

    @Column(name = "uri", length = URI_MAX_LENGTH, nullable = false)
    private String uri;

    // in PostgreSQL it's defined as TEXT
    @Column(name = "req_envelope", length = Integer.MAX_VALUE, nullable = false)
    private String request;

    @Column(name = "req_timestamp", nullable = false)
    private Instant reqTimestamp;

    /** Default public constructor. */
    public Request() {
    }

    /**
     * Creates a new request.
     *
     * @param uri the target URI
     * @param responseJoinId the identifier for pairing/joining request and response together
     * @param request the request (envelope) itself
     * @param msg the message
     * @return request entity
     */
    public static Request createRequest(String uri, String responseJoinId, String request, @Nullable Message msg) {
        Assert.hasText(uri, "uri must not be empty");
        Assert.hasText(request, "request must not be empty");
        Assert.hasText(responseJoinId, "responseJoinId must not be empty");

        Instant currDate = Instant.now();

        Request req = new Request();
        req.setUri(uri);
        req.setResponseJoinId(responseJoinId);
        req.setRequest(request);
        req.setMessage(msg);
        req.setReqTimestamp(currDate);

        return req;
    }

    /**
     * Gets unique request ID.
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
     * Gets asynch. message.
     *
     * @return message
     */
    public @Nullable Message getMessage() {
        return message;
    }

    public void setMessage(@Nullable Message message) {
        this.message = message;
    }

    /**
     * Gets message ID (only for asynchronous message).
     *
     * @return message ID
     */
    public @Nullable Long getMsgId() {
        return msgId;
    }

    public void setMsgId(@Nullable Long msgId) {
        this.msgId = msgId;
    }

    /**
     * Gets identifier for pairing/joining request and response together.
     * It can be {@link Message} or correlation ID or exchange ID or some ID that is unique with {@link #getUri()} uri.
     * This attribute helps to associate response to the right request.
     *
     * @return response join ID
     */
    public String getResponseJoinId() {
        return responseJoinId;
    }

    /**
     * Sets identifier for pairing/joining request and response together.
     *
     * @param responseJoinId response join ID
     */
    public void setResponseJoinId(String responseJoinId) {
        Assert.hasText(responseJoinId, "responseJoinId must not be empty");

        this.responseJoinId = responseJoinId;
    }

    /**
     * Gets endpoint/target URI.
     *
     * @return URI
     */
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        Assert.hasText(uri, "uri must not be empty");

        this.uri = StringUtils.abbreviate(uri, URI_MAX_LENGTH);
    }

    /**
     * Gets normalized endpoint/target URI.
     *
     * @return URI
     */
    public String getNormalizedUri() {
        Matcher uriMatcher = NORMALIZED_URI_PATTERN.matcher(this.uri);
        if (uriMatcher.matches() && uriMatcher.groupCount()> 0) {
            return uriMatcher.group(1);
        }
        return this.uri;
    }

    /**
     * Gets request content.
     *
     * @return request
     */
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        Assert.hasText(request, "request must not be empty");

        this.request = request;
    }

    /**
     * Gets timestamp when request was send to {@link #getUri() target URI}.
     *
     * @return timestamp
     */
    public Instant getReqTimestamp() {
        return reqTimestamp;
    }

    public void setReqTimestamp(Instant reqTimestamp) {
        Assert.notNull(reqTimestamp, "reqTimestamp must not be null");

        this.reqTimestamp = reqTimestamp;
    }

    /**
     * Gets response to this request.
     *
     * @return the response
     */
    @Nullable
    public Response getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Request) {
            Request en = (Request) obj;

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
                .append("responseJoinId", responseJoinId)
                .append("uri", uri)
                .append("request", request)
                .append("reqTimestamp", reqTimestamp)
                .toString();
    }

    @Override
    public String toHumanString() {
        return "(id = " + id + ", uri = " + uri + ", responseJoinId = " + responseJoinId + ")";
    }
}
