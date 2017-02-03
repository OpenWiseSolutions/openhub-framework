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

package org.openhubframework.openhub.core.throttling;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * Configuration properties for the throttling.
 *
 * @author Petr Juza
 * @since 2.0
 */
@ConfigurationProperties(OpenHubPropertyConstants.PREFIX + "throttling")
public class ThrottlingProperties {

    private Counter counter;

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    /**
     * throttling.counter properties
     */
    public static class Counter {

        /**
         * the implementation of throttling counter
         */
        private Class impl;

        public Class getImpl() {
            return impl;
        }

        public void setImpl(Class impl) {
            this.impl = impl;
        }
    }
}
