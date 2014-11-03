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

package org.cleverbus.core.common.contextcall;

import javax.annotation.Nullable;


/**
 * Contract for calling method of service in sibling (Spring) context.
 * <p/>
 * Example: {@code makeCall(EndpointRegistry.class, "getEndpointURIs", Collection.class)}
 * calls method getEndpointURIs (without parameters) in service EndpointRegistry and expects result as Collection.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ContextCall {

    /**
     * Makes call to method of service in sibling (Spring) context.
     *
     * @param targetType the class of target service; there must be exactly one Spring bean of this type
     * @param methodName the name of calling method on target service
     * @param methodArgs the method arguments (if any)
     * @return response from calling (can be null if no response)
     * @throws IllegalStateException when error occurs during calling target method
     */
    @Nullable
    <T> T makeCall(Class<?> targetType, String methodName, Class<T> responseType, Object... methodArgs);

}
