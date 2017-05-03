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

package org.openhubframework.openhub.common;


import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;


/**
 * Class with common methods for use. For usage look into test ToolsTest class.
 */
public final class Tools {

    private Tools() {
    }

    /**
     * Returns the string representation of the <code>Object</code> argument.
     * 
     * @param value an <code>Object</code>.
     * @return if the argument is <code>null</code>, then 
     *          <code>null</code>; otherwise, the value of
     *          <code>value.toString()</code> is returned.
     */
    public static String toString(Object value) {
        return ((value == null) ? null : String.valueOf(value));
    }

    /**
     * Joins the provided input Strings into one String, separated by the provided separator character.
     * Null and empty elements are skipped.
     *
     * @param array     an array of String to join together into one String
     * @param separator separator character to separate the array elements with
     * @return the string merged from array, separated by the specified character
     */
    public static String joinNonEmpty(String[] array, char separator) {
        boolean somethingAdded = false;
        StringBuilder sb = new StringBuilder();

        for (String str : array) {
            if (StringUtils.isNotBlank(str)) {
                if (!somethingAdded) {
                    somethingAdded = true;
                } else {
                    sb.append(separator);
                }
                sb.append(str);
            }
        }

        return sb.toString();
    }

    /**
     * Marshals object graph into XML.
     *
     * @param obj the object graph
     * @param sourceClass the input class
     * @return XML as string
     * @see XmlConverter
     */
    public static String marshalToXml(Object obj, Class sourceClass) {
        Jaxb2Marshaller jaxb2 = new Jaxb2Marshaller();
        jaxb2.setContextPath(sourceClass.getPackage().getName());

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        jaxb2.marshal(obj, result);

        return writer.toString();
    }

    /**
     * Marshals object graph into XML.
     *
     * @param obj   the object graph
     * @param qName the QName
     * @param <T> as data type of object
     * @return XML as string
     * @see Marshaller
     */
    @SuppressWarnings("unchecked")
    public static <T> String marshalToXml(T obj, QName qName) {
        StringWriter stringWriter = new StringWriter();

        try {
            Marshaller marshaller = JAXBContext.newInstance(obj.getClass()).createMarshaller();
            Object element;
            if (qName != null) {
                element = new JAXBElement<T>(qName, (Class<T>) obj.getClass(), obj);
            } else {
                qName = new QName(obj.getClass().getPackage().getName(), obj.getClass().getSimpleName());
                element = new JAXBElement<T>(qName, (Class<T>) obj.getClass(), obj);
            }
            marshaller.marshal(element, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unmarshals XML into object graph.
     *
     * @param xml the input string
     * @param targetClass the target class
     * @param <T> as data type of target object
     * @return object graph
     * @see XmlConverter
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshalFromXml(String xml, Class<T> targetClass) {
        Jaxb2Marshaller jaxb2 = new Jaxb2Marshaller();
        jaxb2.setContextPath(targetClass.getPackage().getName());

        return (T) jaxb2.unmarshal(new StreamSource(new StringReader(xml)));
    }

    /**
     * Converts local instant of {@link OffsetDateTime} to standard UTC instant of {@link OffsetDateTime} with UTC time zone.
     *
     * @param localDateTime the local time
     * @return the UTC time
     */
    public static OffsetDateTime toUTC(OffsetDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime must not be null");

        return OffsetDateTime.ofInstant(localDateTime.toInstant(), ZoneId.of("UTC"));
    }

    /**
     * Converts UTC instant of {@link OffsetDateTime} to local instant of {@link OffsetDateTime} with default time zone.
     *
     * @param utcTime the UTC time
     * @return the local time
     */
    public static OffsetDateTime fromUTC(OffsetDateTime utcTime) {
        Assert.notNull(utcTime, "utcTime must not be null");

        return OffsetDateTime.ofInstant(utcTime.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Converts UTC time in millis to local {@link OffsetDateTime} with default time zone.
     *
     * @param utcMillis the UTC time in millis
     * @return the local time
     */
    public static OffsetDateTime fromUTC(long utcMillis) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(utcMillis), ZoneId.systemDefault());
    }

    /**
     * Gets all property names from {@link Environment environment}.
     *
     * @param env The environment
     * @return set of property names
     */
    public static Set<String> getAllKnownPropertyNames(ConfigurableEnvironment env) {
        Set<String> names = new HashSet<>();

        for (PropertySource<?> propertySource : env.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                Collections.addAll(names, ((EnumerablePropertySource) propertySource).getPropertyNames());
            }
        }

        return names;
    }

    /**
     * Gets {@link Properties} from {@link ConfigurableEnvironment} with properties that name start with
     * prefix from parameter.
     *
     * @param env                environment
     * @param propertyNamePrefix prefix for property names
     * @return {@link Properties} with properties that name start with propertyNamePrefix
     */
    public static Properties getPropertiesWithPrefix(ConfigurableEnvironment env, String propertyNamePrefix) {
        Assert.notNull(env, "env must not be null");
        Assert.hasText(propertyNamePrefix, "propertyNamePrefix must not be empty");

        Properties result = new Properties();
        for (String propertyName : getAllKnownPropertyNames(env)) {
            if (propertyName.startsWith(propertyNamePrefix)) {
                result.setProperty(propertyName, env.getProperty(propertyName));
            }
        }
        return result;
    }

    /**
     * Substitutes all places of the message using a values. See {@link MessageFormatter} for more detail.
     *
     * @param message the message with places defined as {}
     * @param values an array of values that are used to substitute formatting places of the message
     * @return the final string
     */
    public static String fm(String message, Object... values) {
        if (StringUtils.isBlank(message) || ArrayUtils.isEmpty(values)) {
            return message;
        }
        return MessageFormatter.arrayFormat(message, values).getMessage();
    }
}
