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

package org.openhubframework.openhub.common.converter;

import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.util.NumberUtils;


/**
 * Various collection of {@link DurationConverters} to something an via versa converters.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class DurationConverters {

    private DurationConverters() {
        // utility class
    }

    /**
     * Register {@link Duration}s related registry defined in this class.
     *
     * @param registry the spring converter registry.
     */
    public static void registerConverters(ConverterRegistry registry) {
        registry.addConverter(new StringToDurationConverter());
        registry.addConverter(new LongToDurationConverter());
        registry.addConverter(new SecondsToStringConverter());
        registry.addConverter(new DurationToLongConverter());
    }

    /**
     * Convert {@link String} to {@link Duration}.
     *
     * First try if string argument is not a number, then {@link Duration#millis(long)} is used. If the string argument is not a number, it is
     * expect to be ISO format and {@link Duration#parse(String)} is used.
     */
    static class StringToDurationConverter implements Converter<String, Duration> {

        @Override
        public Duration convert(String source) {
            try {
                return Duration.millis(NumberUtils.parseNumber(source, Long.class));
            } catch (NumberFormatException e) {
                return Duration.parse(source);
            }
        }

    }

    /**
     * Convert {@link Integer} to {@link Seconds} using {@link Seconds#seconds(int)} method.
     */
    static class LongToDurationConverter implements Converter<Long, Duration> {

        @Override
        public Duration convert(Long source) {
            return Duration.millis(source);
        }
    }

    /**
     * Convert {@link Duration} to number of milliseconds as plain numeric string.
     */
    static class SecondsToStringConverter implements Converter<Duration, String> {

        @Override
        public String convert(Duration source) {
            return String.valueOf(source.getMillis());
        }
    }

    /**
     * Convert {@link Duration} to simple number.
     */
    static class DurationToLongConverter implements Converter<Duration, Long> {

        @Override
        public Long convert(Duration source) {
            return source.getMillis();
        }

    }
}

