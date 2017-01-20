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

import org.springframework.core.convert.converter.ConverterRegistry;


/**
 * Helper class to register all customer converters.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class Converters {

    private Converters() {
        // utility class
    }

    /**
     * Register various default converters:
     * <ul>
     * <li>{@link SecondsConverters}</li>
     * <li>{@link DurationConverters}
     * </ul>
     *
     * @param registry the converter registry.
     */
    public static void registerConverters(ConverterRegistry registry) {
        SecondsConverters.registerConverters(registry);
        DurationConverters.registerConverters(registry);
    }
}