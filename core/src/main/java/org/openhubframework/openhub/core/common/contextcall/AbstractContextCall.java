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

import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Parent implementation of {@link ContextCall} interface, defines base behaviour.
 *
 * @author Petr Juza
 */
public abstract class AbstractContextCall implements ContextCall {

    @Autowired
    private ContextCallRegistry callRegistry;

    @Nullable
    @Override
    public <T> T makeCall(Class<?> targetType, String methodName, Class<T> responseType, Object... methodArgs) {
        // generate unique ID
        String callId = UUID.randomUUID().toString();

        // save params into registry
        ContextCallParams params = new ContextCallParams(targetType, methodName, methodArgs);

        try {
            callRegistry.addParams(callId, params);

            // call target service
            callTargetMethod(callId, targetType, methodName);

            // get response from the call
            return callRegistry.getResponse(callId, responseType);

        } finally {
            callRegistry.clearCall(callId);
        }
    }

    /**
     * Calls target method of the service by specific protocol.
     *
     * @param callId the unique call ID
     * @param targetType the class of target service
     * @param methodName the name of calling method on target service
     * @throws IllegalStateException when error occurs during calling target method
     */
    protected abstract void callTargetMethod(String callId, Class<?> targetType, String methodName);

}
