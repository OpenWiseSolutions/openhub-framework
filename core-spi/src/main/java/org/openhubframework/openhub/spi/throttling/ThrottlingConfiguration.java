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

package org.openhubframework.openhub.spi.throttling;

import java.util.Map;

import javax.annotation.Nullable;


/**
 * Throttling configuration contract.
 *
 * @author Petr Juza
 */
public interface ThrottlingConfiguration {

    /**
     * Gets throttle properties for most equal scope.
     *
     * @param inScope the scope of the incoming request
     * @return properties or {@code null} if there is no properties for specified scope
     */
    @Nullable
    ThrottleProps getThrottleProps(ThrottleScope inScope);

    /**
     * Gets all throttling properties.
     *
     * @return throttling properties
     */
    Map<ThrottleScope, ThrottleProps> getProperties();

    /**
     * Is throttling disabled at all?
     *
     * @return {@code true} for disabling
     */
    boolean isThrottlingDisabled();

}
