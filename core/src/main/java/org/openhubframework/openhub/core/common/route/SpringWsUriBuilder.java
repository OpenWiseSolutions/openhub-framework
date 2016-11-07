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

package org.openhubframework.openhub.core.common.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.openhubframework.openhub.api.route.WebServiceUriBuilder;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Implementation of {@link WebServiceUriBuilder} for using Spring WS component.
 *
 * @author Petr Juza
 */
public class SpringWsUriBuilder implements WebServiceUriBuilder {

    private static final String MESSAGE_FACTORY_SOAP11 = "messageFactorySOAP11";

    private static final String MESSAGE_FACTORY_SOAP12 = "messageFactorySOAP12";

    @Override
    public String getOutWsUri(String connectionUri, String messageSenderRef, String soapAction) {
        Assert.hasText(connectionUri, "the connectionUri must not be empty");
        Assert.hasText(messageSenderRef, "the messageSenderRef must not be empty");

        String wsUri = "spring-ws:" + connectionUri + "?messageSender=#" + messageSenderRef
                + "&messageFactory=#" + MESSAGE_FACTORY_SOAP11;

        if (StringUtils.isNotEmpty(soapAction)) {
            wsUri += "&soapAction=" + soapAction;
        }

        return wsUri;
    }


    @Override
    public String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, String soapAction) {
        Assert.hasText(connectionUri, "the connectionUri must not be empty");
        Assert.hasText(messageSenderRef, "the messageSenderRef must not be empty");

        String wsUri = "spring-ws:" + connectionUri + "?messageSender=#" + messageSenderRef
                + "&messageFactory=#" + MESSAGE_FACTORY_SOAP12;

        if (StringUtils.isNotEmpty(soapAction)) {
            wsUri += "&soapAction=" + soapAction;
        }

        return wsUri;
    }


    @Override
    public String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params) {
        Assert.notNull(qName, "the qName must not be null");
        Assert.hasText(endpointMappingRef, "the endpointMappingRef must not be empty");

        return "spring-ws:rootqname:" + qName + "?endpointMapping=#" + endpointMappingRef
                + (params != null ? "&" + params : "");
    }
}
