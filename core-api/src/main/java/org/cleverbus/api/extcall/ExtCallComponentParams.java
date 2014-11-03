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

package org.cleverbus.api.extcall;

import org.apache.camel.Exchange;


/**
 * Constants for external call parameters.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class ExtCallComponentParams {

    /**
     * Optional exchange property, which specifies which operation to use for external call checks.
     */
    public static final String EXTERNAL_CALL_OPERATION = "externalCallOperation";

    /**
     * Optional exchange property, which is appended to the generated operation key or used instead of the key,
     * depending on the URI.
     */
    public static final String EXTERNAL_CALL_KEY = "externalCallKey";

    /**
     * Optional exchange property, which can be set during external call execution.
     * <ul>
     *     <li>If set to {@code true}, the external call is considered successful</li>
     *     <li>If set to {@code false}, the external call is considered failed</li>
     *     <li>If not set (default) or set to {@code null},
     *     the external call is considered successful unless:
     *     either an unhandled exception occurs ({@link Exchange#isFailed()} returns true),
     *     or the exchange is stopped ({@link Exchange#ROUTE_STOP} property is set to true)</li>
     * </ul>
     * If set to anything else, a boolean value will be acquired via Camel type conversion.
     */
    public static final String EXTERNAL_CALL_SUCCESS = "externalCallSuccess";


    private ExtCallComponentParams() {
    }
}
