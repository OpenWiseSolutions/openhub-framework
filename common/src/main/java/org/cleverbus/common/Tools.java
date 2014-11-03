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

package org.cleverbus.common;


import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.converter.jaxp.XmlConverter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;


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
            if (StringUtils.hasText(str)) {
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
     * Converts local {@link DateTime} to UTC {@link DateTime} with UTC time zone.
     *
     * @param localDateTime the local time
     * @return the UTC time
     */
    public static DateTime toUTC(DateTime localDateTime) {
        DateTimeZone dateTimeZone = localDateTime.getZone();
        long utcTime = dateTimeZone.convertLocalToUTC(localDateTime.getMillis(), false);
        return new DateTime(utcTime, DateTimeZone.UTC);
    }

    /**
     * Converts UTC {@link DateTime} to local {@link DateTime} with default time zone.
     *
     * @param utcTime the UTC time
     * @return the local time
     */
    public static DateTime fromUTC(DateTime utcTime) {
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        long localTime = dateTimeZone.convertUTCToLocal(utcTime.getMillis());
        return new DateTime(localTime, dateTimeZone);
    }

    /**
     * Converts UTC time in millis to local {@link DateTime} with default time zone.
     *
     * @param utcMillis the UTC time in millis
     * @return the local time
     */
    public static DateTime fromUTC(long utcMillis) {
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        long localTime = dateTimeZone.convertUTCToLocal(utcMillis);
        return new DateTime(localTime, dateTimeZone);
    }
}
