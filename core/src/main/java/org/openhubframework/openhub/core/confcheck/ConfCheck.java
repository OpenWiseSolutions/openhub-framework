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

package org.openhubframework.openhub.core.confcheck;

import org.openhubframework.openhub.api.exception.ConfigurationException;


/**
 * Contract for configuration checking.
 * <p>
 * Implement this interface to check configuration during application start-up.
 * Implementation (aka bean) has to be in root or web child application context.
 *
 * @author Petr Juza
 * @since 1.0
 * @see ConfigurationChecker
 */
public interface ConfCheck {

    /**
     * Checks configuration.
     *
     * @throws ConfigurationException when configuration checking failed
     */
    void check() throws ConfigurationException;

}
