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

package org.openhubframework.openhub.core.common.contextcall;

import javax.annotation.Nullable;

import org.openhubframework.openhub.api.exception.NoDataFoundException;


/**
 * Contract of registry calls for transferring calls' parameters and response between sibling Spring application
 * contexts, specifically from web application context to Spring WS (Camel) application context.
 * <p/>
 * This registry serves for saving call parameters and response. Registry is initialized in root application context
 * and therefore it's accessible from both child (sibling) application contexts.
 * <p/>
 * Basic workflow:
 * <ol>
 *     <li>client: add params
 *     <li>server: get params + add response
 *     <li>client: get response + clear memory
 * </ol>
 *
 * @author Petr Juza
 */
public interface ContextCallRegistry {

    /**
     * Adds new parameters under specified call ID.
     *
     * @param callId the call identifier
     * @param params call parameters
     */
    void addParams(String callId, ContextCallParams params);

    /**
     * Gets call parameters.
     *
     * @param callId the call identifier
     * @return call parameters
     * @throws NoDataFoundException if there is no response with specified call ID
     */
    ContextCallParams getParams(String callId);

    /**
     * Adds new response to specified call ID.
     *
     * @param callId the call identifier
     * @param res response of the call (can be null)
     */
    void addResponse(String callId, @Nullable Object res);

    /**
     * Gets response of the specified call.
     *
     * @param callId the call identifier
     * @return response of the call
     * @throws NoDataFoundException if there is no response with specified call ID
     */
    @Nullable
    <T> T getResponse(String callId, Class<T> requiredType);

    /**
     * Removes call parameters and response for specified call ID.
     *
     * @param callId the call identifier
     */
    void clearCall(String callId);

}
