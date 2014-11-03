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

package org.cleverbus.spi.throttling;

import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;


/**
 * Contract for throttling processor - throttles input messages.
 * <p/>
 * Throttling is defined for:
 * <ul>
 *     <li>count of requests for specified time interval
 *     <li>source external system
 *     <li>service
 * </ul>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ThrottlingProcessor {

    /**
     * Throttle specified source system and service name.
     *
     * @param throttleScope the specified scope for throttling
     * @throws IntegrationException with {@link InternalErrorEnum#E114} when count of requests exceeds limit
     *      for specified time interval
     */
    void throttle(ThrottleScope throttleScope);
}
