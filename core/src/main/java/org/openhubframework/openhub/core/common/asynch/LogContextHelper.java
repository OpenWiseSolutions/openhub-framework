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

package org.openhubframework.openhub.core.common.asynch;

import javax.annotation.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.common.log.GUID;
import org.openhubframework.openhub.common.log.LogContext;
import org.openhubframework.openhub.common.log.LogContextFilter;


/**
 * Helper class for setting log context parameters.
 *
 * @author Petr Juza
 * @since 0.4
 */
public final class LogContextHelper {

    private LogContextHelper() {
    }

    /**
     * Set log context parameters, specifically:
     * <ul>
     *     <li>{@link LogContextFilter#CTX_SOURCE_SYSTEM}
     *     <li>{@link LogContextFilter#CTX_CORRELATION_ID}
     *     <li>{@link LogContextFilter#CTX_PROCESS_ID}
     *     <li>{@link LogContextFilter#CTX_REQUEST_ID}
     * </ul>
     *
     * It's because child threads don't inherits this information from parent thread automatically.
     * If there is no request ID defined then new ID is created.
     *
     * @param message the message
     * @param requestId the request ID
     */
    public static void setLogContextParams(Message message, @Nullable String requestId) {
        Assert.notNull(message, "the message must not be null");

        // source system
        LogContext.setContextValue(LogContextFilter.CTX_SOURCE_SYSTEM, message.getSourceSystem().getSystemName());

        // correlation ID
        LogContext.setContextValue(LogContextFilter.CTX_CORRELATION_ID, message.getCorrelationId());

        // process ID
        LogContext.setContextValue(LogContextFilter.CTX_PROCESS_ID, message.getProcessId());

        // request ID
        if (StringUtils.hasText(requestId)) {
            LogContext.setContextValue(LogContextFilter.CTX_REQUEST_ID, requestId);
        } else {
            LogContext.setContextValue(LogContextFilter.CTX_REQUEST_ID, new GUID().toString());
        }
    }

    /**
     * Remove context params, that are set in the {@link LogContextHelper#setLogContextParams(Message, String)}.
     */
    public static void removeLogContextParams() {
        // source system
        LogContext.removeContextValue(LogContextFilter.CTX_SOURCE_SYSTEM);

        // correlation ID
        LogContext.removeContextValue(LogContextFilter.CTX_CORRELATION_ID);

        // process ID
        LogContext.removeContextValue(LogContextFilter.CTX_PROCESS_ID);

        // request ID
        LogContext.removeContextValue(LogContextFilter.CTX_REQUEST_ID);
    }
}
