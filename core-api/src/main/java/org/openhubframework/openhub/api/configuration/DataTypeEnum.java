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

package org.openhubframework.openhub.api.configuration;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;


/**
 * Enumeration of possible data types of configuration parameters.
 *
 * @author Petr Juza
 * @since 2.0
 */
public enum DataTypeEnum {

    /**
     * Text, string (default).
     */
    STRING(String.class),

    /**
     * Integer, whole number.
     */
    INT(Integer.class),

    /**
     * Floating number.
     */
    FLOAT(Float.class),

    /**
     * Date (without time).
     */
    DATE(LocalDate.class),

    /**
     * Boolean.
     */
    BOOLEAN(Boolean.class),

    /**
     * File.
     */
    FILE(File.class),

    /**
     * Regular pattern.
     */
    PATTERN(Pattern.class);

    private Class<?> typeClass;

    DataTypeEnum(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    /**
     * Gets class of data type.
     *
     * @return class of data type
     */
    public Class<?> getTypeClass() {
        return typeClass;
    }

    /**
     * Gets {@link DataTypeEnum} by its class.
     *
     * @param clazz Requested class
     * @return type of property
     */
    public static DataTypeEnum typeOf(Class<? extends Serializable> clazz) {
        for (DataTypeEnum dataType : values()) {
            if (dataType.getTypeClass().equals(clazz)) {
                return dataType;
            }
        }

        throw new IllegalStateException("There is no DataTypeEnum with class" + clazz);
    }
}
