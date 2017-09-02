/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.common.rpc;

import java.time.OffsetDateTime;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;


/**
 * General fault response when server error occurred.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class GeneralFaultRpc {

    private final OffsetDateTime timestamp;

    private final HttpStatus status;

    private final ErrorExtEnum error;

    private final Exception exception;

    /**
     * Creates new fault response.
     *
     * @param status    HTTP status
     * @param error     Error
     * @param exception Exception
     */
    public GeneralFaultRpc(HttpStatus status, ErrorExtEnum error, Exception exception) {
        Assert.notNull(status, "status mustn't not be null");
        Assert.notNull(error, "error mustn't not be null");
        Assert.notNull(exception, "exception mustn't not be null");

        this.timestamp = OffsetDateTime.now();
        this.status = status;
        this.error = error;
        this.exception = exception;
    }

    /**
     * Creates new fault response.
     *
     * @param status HTTP status
     * @param ex     Integration exception
     */
    public GeneralFaultRpc(HttpStatus status, IntegrationException ex) {
        this(status, ex.getError(), ex);
    }

    /**
     * Gets timestamp: 2016-12-10T18:21:08+0100 (string): timestamp when error occurred.
     */
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets httpStatus: 204 (number) - HTTP status code.
     */
    public int getHttpStatus() {
        return status.value();
    }

    /**
     * Gets httpDesc: 'No Content' (string) - description of HTTP status.
     */
    public String getHttpDesc() {
        return status.getReasonPhrase();
    }

    /**
     * Gets errorCode: 'O_E001' (string) - internal error code.
     */
    public String getErrorCode() {
        return error.getErrorCode();
    }

    /**
     * Gets type: 'java.lang.RuntimeException' (string) - type of error (=class name of the exception).
     */
    public String getType() {
        return exception.getClass().getSimpleName();
    }

    /**
     * Gets message: 'Message describing the error' (string) - a description of the error in default language.
     */
    public String getMessage() {
        return exception.getMessage();
    }

    /**
     * Gets messageI18n: 'Message describing the error' (string,) - a description of the error in localized language.
     */
    @Nullable
    public String getMessageI18n() {
        return exception.getMessage();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("timestamp", timestamp)
                .append("status", status)
                .append("error", error)
                .append("exception", exception)
                .toString();
    }
}
