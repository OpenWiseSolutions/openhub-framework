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

import org.joda.time.Seconds;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.util.NumberUtils;


/**
 * Various collection of {@link Seconds} to something an via versa converters.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class SecondsConverters {

    private SecondsConverters() {
        // utility class
    }

    /**
     * Register Seconds related registry defined in this class.
     *
     * @param registry the spring converter registry.
     */
    public static void registerConverters(ConverterRegistry registry) {
        registry.addConverter(new StringToSecondsConverter());
        registry.addConverter(new IntegerToSecondsConverter());
        registry.addConverter(new SecondsToStringConverter());
        registry.addConverter(new SecondsToIntegerConverter());
    }

    /**
     * Convert {@link String} to {@link Seconds}.
     *
     * First try if string argument is not a number, then {@link Seconds#seconds(int)} is used. If the string argument is not a number, it is
     * expect to be ISO format and {@link Seconds#parseSeconds(String)} is used.
     */
    static class StringToSecondsConverter implements Converter<String, Seconds> {

        @Override
        public Seconds convert(String source) {
            try {
                return Seconds.seconds(NumberUtils.parseNumber(source, Integer.class));
            } catch (NumberFormatException e) {
                return Seconds.parseSeconds(source);
            }
        }

    }

    /**
     * Convert {@link Integer} to {@link Seconds} using {@link Seconds#seconds(int)} method.
     */
    static class IntegerToSecondsConverter implements Converter<Integer, Seconds> {

        @Override
        public Seconds convert(Integer source) {
            return Seconds.seconds(source);
        }
    }

    /**
     * Convert {@link Seconds} to number of seconds as plain numeric string.
     */
    static class SecondsToStringConverter implements Converter<Seconds, String> {

        @Override
        public String convert(Seconds source) {
            return String.valueOf(source.getSeconds());
        }
    }

    /**
     * Convert {@link Seconds} to simple number.
     */
    static class SecondsToIntegerConverter implements Converter<Seconds, Integer> {

        @Override
        public Integer convert(Seconds source) {
            return source.getSeconds();
        }

    }
}
