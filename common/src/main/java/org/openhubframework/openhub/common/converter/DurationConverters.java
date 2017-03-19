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

import java.time.Duration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;


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
        registry.addConverter(new DurationToStringConverter());
        registry.addConverter(new DurationToLongConverter());
    }

    /**
     * Convert {@link String} in format defined by ISO-8601 to {@link Duration}.
     */
    static class StringToDurationConverter implements Converter<String, Duration> {

        @Override
        public Duration convert(String source) {
            return Duration.parse(source);
        }
    }

    /**
     * Convert {@link Long} to {@link Duration}.
     */
    static class LongToDurationConverter implements Converter<Long, Duration> {

        @Override
        public Duration convert(Long source) {
            return Duration.ofMillis(source);
        }
    }

    /**
     * Convert {@link Duration} to string representation (ISO-8601).
     */
    static class DurationToStringConverter implements Converter<Duration, String> {

        @Override
        public String convert(Duration source) {
            return source.toString();
        }
    }

    /**
     * Convert {@link Duration} to simple number (number of millis).
     */
    static class DurationToLongConverter implements Converter<Duration, Long> {

        @Override
        public Long convert(Duration source) {
            return source.toMillis();
        }

    }
}

