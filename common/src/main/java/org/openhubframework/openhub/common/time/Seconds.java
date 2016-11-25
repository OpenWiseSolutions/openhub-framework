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

package org.openhubframework.openhub.common.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.NumberUtils;


/**
 * Seconds.
 * <p>
 * There is {@link Duration} type in JDK only and because it's quite often need to define parameter values in seconds,
 * this class is quite useful.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class Seconds {

    public final static Seconds ZERO = Seconds.of(0);

    private int seconds = 0;

    private Seconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Creates seconds with specified count.
     *
     * @param seconds Count of seconds
     * @return seconds
     */
    public static Seconds of(int seconds) {
        return new Seconds(seconds);
    }

    /**
     * Creates seconds with specified count.
     *
     * @param seconds Count of seconds
     * @return seconds
     * @throws IllegalArgumentException if input string cannot be converted to the number
     */
    public static Seconds of(String seconds) {
        return new Seconds(NumberUtils.parseNumber(seconds, Integer.class));
    }

    /**
     * Gets count of seconds.
     *
     * @return count of seconds
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Converts to {@link Duration} in seconds.
     *
     * @return duration
     */
    public Duration toDuration() {
        return Duration.of(getSeconds(), ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Seconds) {
            Seconds en = (Seconds) obj;

            return new EqualsBuilder()
                    .append(getSeconds(), en.getSeconds())
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getSeconds())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("seconds", getSeconds())
                .toString();
    }
}
