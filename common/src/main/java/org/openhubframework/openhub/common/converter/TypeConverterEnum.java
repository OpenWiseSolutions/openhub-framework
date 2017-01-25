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

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.common.Tools;


/**
 * Enumeration of possible types with conversion functionality.
 *
 * @author Petr Juza
 * @since 2.0
 */
public enum TypeConverterEnum implements DataTypeConverter {

    /**
     * Text, string (default).
     */
    STRING(String.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(String.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), String.class.getSimpleName()));

            return (String)obj;
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            return str;
        }
    },

    /**
     * Integer, whole number.
     */
    INT(Integer.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(Integer.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), Integer.class.getSimpleName()));

            NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.getLocale());
            return nf.format(obj);
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.getLocale());
            return nf.parse(str).intValue();
        }
    },

    /**
     * Floating number.
     */
    FLOAT(Float.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(Float.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), Float.class.getSimpleName()));

            NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.getLocale());
            return nf.format(obj);
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.getLocale());
            return nf.parse(str).floatValue();
        }
    },

    /**
     * Date (without time).
     */
    DATE(LocalDate.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(LocalDate.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), LocalDate.class.getSimpleName()));

            return obj.toString(); // output will be in the ISO-8601 format
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            try {
                // str must be in ISO format: 2007-12-03
                return LocalDate.parse(str);
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                // try again with time
                return LocalDate.parse(str, ISODateTimeFormat.dateTime());
            }
        }
    },

    /**
     * Boolean.
     */
    BOOLEAN(Boolean.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(Boolean.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), Boolean.class.getSimpleName()));

            return obj.toString();
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            return Boolean.valueOf(str);
        }
    },

    /**
     * File.
     */
    FILE(File.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(File.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), File.class.getSimpleName()));

            return ((File)obj).getPath();
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            return new File(str);
        }
    },

    /**
     * Regular pattern.
     */
    PATTERN(Pattern.class) {
        @Override
        public String convertToString(Object obj) {
            if (obj == null) {
                return null;
            }

            Assert.isTrue(Pattern.class.isInstance(obj), Tools.fm("input object's type '{}' doesn't "
                    + "corresponds to declared type '{}'", obj.getClass().getSimpleName(), Pattern.class.getSimpleName()));

            return ((Pattern)obj).pattern();
        }

        @Override
        public Object convertToObject(String str) throws ParseException {
            if (str == null) {
                return null;
            }

            return Pattern.compile(str);
        }
    };

    private Class<?> typeClass;

    TypeConverterEnum(Class<?> typeClass) {
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
     * Gets {@link TypeConverterEnum} by its class.
     *
     * @param clazz Requested class
     * @return type of property
     */
    public static TypeConverterEnum typeOf(Class<? extends Serializable> clazz) {
        for (TypeConverterEnum dataType : values()) {
            if (dataType.getTypeClass().equals(clazz)) {
                return dataType;
            }
        }

        throw new IllegalStateException("There is no DataTypeEnum with class" + clazz);
    }
}

