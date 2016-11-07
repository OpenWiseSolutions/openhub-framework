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

package org.openhubframework.openhub.api.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;


/**
 * Contract for creating web service URI.
 *
 * @author Petr Juza
 */
public interface WebServiceUriBuilder {

    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.1</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    String getOutWsUri(String connectionUri, String messageSenderRef, @Nullable String soapAction);


    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.2</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, @Nullable String soapAction);


    /**
     * Gets "from" URI for handling incoming WS messages with default "endpointMapping" bean.
     *
     * @return from URI
     * @param qName the operation QName (namespace + local part)
     * @param endpointMappingRef the endpoint mapping ref (bean id/name in Spring context)
     * @param params the endpoint URI parameters
     */
    String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params);

}
