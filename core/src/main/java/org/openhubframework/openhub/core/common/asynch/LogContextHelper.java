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

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.common.log.GUID;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.common.log.LogContextFilter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


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
        Log.setContextValue(LogContextFilter.CTX_SOURCE_SYSTEM, message.getSourceSystem().getSystemName());

        // correlation ID
        Log.setContextValue(LogContextFilter.CTX_CORRELATION_ID, message.getCorrelationId());

        // process ID
        Log.setContextValue(LogContextFilter.CTX_PROCESS_ID, message.getProcessId());

        // request ID
        if (StringUtils.hasText(requestId)) {
            Log.setContextValue(LogContextFilter.CTX_REQUEST_ID, requestId);
        } else {
            Log.setContextValue(LogContextFilter.CTX_REQUEST_ID, new GUID().toString());
        }
    }
}
