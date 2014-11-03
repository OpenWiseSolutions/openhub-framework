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

package org.cleverbus.common.jaxb;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JaxbDataFormat;

/**
 * Helper class for creating Camel {@link JaxbDataFormat} used for marshalling and unmarshalling.
 */
public class JaxbDataFormatHelper {

    /**
     * Creates a JAXB data format for marshalling and unmarshalling
     * the specified class to/from its native XML representation.
     *
     * @param xmlClass the class that this data format will be able to marshal and unmarshal
     * @return the Camel {@link DataFormatDefinition} instance for use in routes
     */
    public static JaxbDataFormat jaxb(Class<?> xmlClass) {
        JaxbDataFormat dataFormat = new JaxbDataFormat(false);
        dataFormat.setContextPath(xmlClass.getPackage().getName());
        return dataFormat;
    }

    /**
     * Creates a JAXB data format for marshalling and unmarshalling the specified class
     * to/from its native XML representation.
     * <p/>
     * If rootQName is provided, this data format will work with classes that are not root elements
     * (not annotated with {@link XmlRootElement}).
     *
     * @param xmlClass  the class that this data format will be able to marshal and unmarshal
     * @param rootQName the QName (optional) of the root element in XML,
     *                  which is necessary for marshalling classes without XmlRootElement to XML
     * @return the Camel {@link DataFormatDefinition} instance for use in routes
     */
    public static JaxbDataFormat jaxb(Class<?> xmlClass, @Nullable QName rootQName) {
        JaxbDataFormat dataFormat = jaxb(xmlClass);
        // partial marshaling - class without @XmlRootElement
        if (rootQName != null) {
            dataFormat.setFragment(true);
            dataFormat.setPartClass(xmlClass.getName());
            dataFormat.setPartNamespace(rootQName.toString());
        }
        return dataFormat;
    }

    /**
     * Creates a JAXB data format for <strong>unmarshalling</strong> (only)
     * the specified class to/from its native XML representation.
     * Unlike data format returned by {@link #jaxb(Class)},
     * this data format works with classes that are not root elements
     * (e.g., not annotated with {@link XmlRootElement}).
     *
     * @param xmlClass the class that this data format will be able to marshal and unmarshal
     * @return the Camel {@link DataFormatDefinition} instance for use in routes
     * @see #jaxbFragment(Class, String) for marshalling fragments (as this one can only unmarshal)
     */
    public static JaxbDataFormat jaxbFragment(Class<?> xmlClass) {
        JaxbDataFormat jaxbIn = jaxb(xmlClass);
        // partial unmarshaling - class without @XmlRootElement
        jaxbIn.setFragment(true);
        jaxbIn.setPartClass(xmlClass.getName());
        return jaxbIn;
    }

    /**
     * Creates a JAXB data format for marshalling and unmarshalling
     * the specified class to/from its native XML representation.
     * Unlike data format returned by {@link #jaxb(Class)},
     * this data format works with classes that are not root elements
     * (e.g., not annotated with {@link XmlRootElement}).
     *
     * @param xmlClass      the class that this data format will be able to marshal and unmarshal
     * @param partNamespace the namespace (optional) with the name of the root element
     *                      (name in XML, not its Java class name),
     *                      which is necessary for marshalling the class to XML
     * @return the Camel {@link DataFormatDefinition} instance for use in routes
     */
    public static JaxbDataFormat jaxbFragment(Class<?> xmlClass, String partNamespace) {
        JaxbDataFormat dataFormat = jaxbFragment(xmlClass);
        // partial marshaling - class without @XmlRootElement
        dataFormat.setFragment(true);
        dataFormat.setPartClass(xmlClass.getName());
        dataFormat.setPartNamespace(partNamespace);
        return dataFormat;
    }
}
