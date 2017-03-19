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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.HumanReadable;


/**
 * The class represents a throttling scope consisting of a source system and service name.
 *
 * @author Petr Juza
 */
public final class ThrottleScope implements HumanReadable {

    /**
     * Represents any source system.
     */
    public static final String ANY_SOURCE_SYSTEM = "any_system";

    /**
     * Represents any service.
     */
    public static final String ANY_SERVICE = "any_service";

    private static final String ANY = "*";

    public static final String THROTTLE_SEPARATOR = ".";

    private String sourceSystem;

    private String serviceName;

    /**
     * Creates new throttle scope.
     *
     * @param sourceSystem the source system, can be used {@link #ANY_SOURCE_SYSTEM} or '*'
     * @param serviceName the service name, can be used {@link #ANY_SERVICE} or '*'
     */
    public ThrottleScope(String sourceSystem, String serviceName) {
        Assert.hasText(sourceSystem, "the sourceSystem must not be empty");
        Assert.hasText(serviceName, "the serviceName must not be empty");

        this.sourceSystem = sourceSystem.equals(ANY) ? ANY_SOURCE_SYSTEM : sourceSystem;
        this.serviceName = serviceName.equals(ANY) ? ANY_SERVICE : serviceName;
    }

    /**
     * Gets source system.
     *
     * @return source system
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Gets service name.
     *
     * @return service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Tests if the throttling scopes match.
     *
     * @param that represents object of matching
     * @return the match factor. Negative value signifies no match.
     *    Non-negative signifies a match. The greater the returned value
     *    the closer the match.
     */
    public int match(final ThrottleScope that) {
        int factor = 0;

        if (StringUtils.equalsIgnoreCase(this.sourceSystem, that.sourceSystem)) {
            factor += 1;
        } else {
            if (this.sourceSystem != ANY_SOURCE_SYSTEM && that.sourceSystem != ANY_SOURCE_SYSTEM) {
                return -1;
            }
        }

        if (StringUtils.equalsIgnoreCase(this.serviceName, that.serviceName)) {
            factor += 1;
        } else {
            if (this.serviceName != ANY_SERVICE && that.serviceName != ANY_SERVICE) {
                return -1;
            }
        }

        return factor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ThrottleScope) {
            ThrottleScope en = (ThrottleScope) obj;

            return new EqualsBuilder()
                    .append(getSourceSystem(), en.getSourceSystem())
                    .append(getServiceName(), en.getServiceName())
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getSourceSystem())
                .append(getServiceName())
                .toHashCode();
    }

    @Override
    public String toHumanString() {
        return (sourceSystem.equals(ANY_SOURCE_SYSTEM) ? ANY : sourceSystem) + THROTTLE_SEPARATOR
                + (serviceName.equals(ANY_SERVICE) ? ANY : serviceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sourceSystem", sourceSystem)
                .append("serviceName", serviceName)
                .toString();
    }
}
