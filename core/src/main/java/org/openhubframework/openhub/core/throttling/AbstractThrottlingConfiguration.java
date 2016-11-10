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

package org.openhubframework.openhub.core.throttling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.spi.throttling.ThrottleProps;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;
import org.openhubframework.openhub.spi.throttling.ThrottlingConfiguration;


/**
 * Parent class for throttling configuration.
 *
 * @author Petr Juza
 */
public abstract class AbstractThrottlingConfiguration implements ThrottlingConfiguration {

    /**
     * Default time interval (in seconds).
     */
    public static final int DEFAULT_INTERVAL = 60;

    /**
     * Default max. requests for time interval.
     */
    public static final int DEFAULT_LIMIT = 60;

    private Map<ThrottleScope, ThrottleProps> props = new HashMap<ThrottleScope, ThrottleProps>();

    /**
     * True for disabling throttling at all.
     */
    private boolean throttlingDisabled = false;


    @Nullable
    @Override
    public final ThrottleProps getThrottleProps(ThrottleScope inScope) {
        int maxMatch = -1;
        ThrottleScope matchedScope = null;
        for (ThrottleScope scope : props.keySet()) {
            int match = scope.match(inScope);

            if (match >= 0 && match > maxMatch) {
                matchedScope = scope;
                maxMatch = match;
            }
        }

        if (matchedScope != null) {
            return props.get(matchedScope);
        } else {
            return null;
        }
    }

    /**
     * Adds new configuration property or updates already existing property.
     *
     * @param sourceSystem the source system, can be used '*' for any system
     * @param serviceName the service name, can be used '*' for any system
     * @param interval the time interval in seconds
     * @param limit the limit of requests for specified interval
     */
    protected final void addProperty(String sourceSystem, String serviceName, int interval, int limit) {
        ThrottleScope scope = new ThrottleScope(sourceSystem, serviceName);

        ThrottleProps throttleProps = new ThrottleProps(interval, limit);

        if (props.put(scope, throttleProps) == null) {
            Log.debug("new throttle properties added: " + scope + ", props: " + throttleProps);
        } else {
            Log.debug("throttle properties updated: " + scope + ", props: " + throttleProps);
        }
    }

    /**
     * Gets all throttling properties.
     *
     * @return throttling properties
     */
    @Override
    public final Map<ThrottleScope, ThrottleProps> getProperties() {
        return Collections.unmodifiableMap(props);
    }

    /**
     * Is throttling disabled at all?
     *
     * @return {@code true} for disabling
     */
    @Override
    public boolean isThrottlingDisabled() {
        return throttlingDisabled;
    }

    /**
     * Sets true for disabling throttling.
     *
     * @param throttlingDisabled true for disabling throttling
     */
    public void setThrottlingDisabled(boolean throttlingDisabled) {
        this.throttlingDisabled = throttlingDisabled;
    }
}
