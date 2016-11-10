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

import org.openhubframework.openhub.api.common.HumanReadable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Throttle properties - max. count for specified time interval.
 *
 * @author Petr Juza
 */
public final class ThrottleProps implements HumanReadable {

    public static final String PROP_VALUE_SEPARATOR = "/";

    private int interval;
    private int limit;

    /**
     * Creates throttle properties.
     *
     * @param interval the time interval in seconds
     * @param limit the limit of requests for specified interval
     */
    public ThrottleProps(int interval, int limit) {
        this.interval = interval;
        this.limit = limit;
    }

    public int getInterval() {
        return interval;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interval", interval)
                .append("limit", limit)
                .toString();
    }

    @Override
    public String toHumanString() {
        return limit + PROP_VALUE_SEPARATOR + interval;
    }
}
