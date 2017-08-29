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
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.openhubframework.openhub.common.converter.DataTypeConverter;
import org.openhubframework.openhub.common.converter.TypeConverterEnum;


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
    STRING(String.class, TypeConverterEnum.STRING),

    /**
     * Integer, whole number.
     */
    INT(Integer.class, TypeConverterEnum.INT),

    /**
     * Floating number.
     */
    FLOAT(Float.class, TypeConverterEnum.FLOAT),

    /**
     * Date (without time).
     */
    DATE(LocalDate.class, TypeConverterEnum.DATE),

    /**
     * Boolean.
     */
    BOOLEAN(Boolean.class, TypeConverterEnum.BOOLEAN),

    /**
     * File.
     */
    FILE(File.class, TypeConverterEnum.FILE),

    /**
     * Regular pattern.
     */
    PATTERN(Pattern.class, TypeConverterEnum.PATTERN);

    private Class<?> typeClass;

    private DataTypeConverter converter;

    DataTypeEnum(Class<?> typeClass, DataTypeConverter converter) {
        this.typeClass = typeClass;
        this.converter = converter;
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
     * Gets data type converter.
     *
     * @return the DataTypeConverter implementation.
     */
    public DataTypeConverter getConverter() {
        return converter;
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
