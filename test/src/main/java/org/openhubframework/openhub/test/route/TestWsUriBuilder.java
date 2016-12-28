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

package org.openhubframework.openhub.test.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.route.WebServiceUriBuilder;


/**
 * Implementation of {@link WebServiceUriBuilder} for using in tests.
 * "direct" component is used instead of specific web service implementation, e.g. "spring-ws".
 * Advantage is that it's no further needed to use "advice" for changing input route URI.
 * <p/>
 * Direct URI has the following format: {@value #URI_WS_IN}/{@value #URI_WS_OUT} + local part of the request qName.
 * <p/>
 * Example of using in test:
 * <pre>
 *  Produce(uri = TestWsUriBuilder.URI_WS_IN + "syncHelloRequest")
    private ProducerTemplate producer;
 * </pre>
 *
 * @author Petr Juza
 */
@Component
@Primary
public class TestWsUriBuilder implements WebServiceUriBuilder {

    public static final String URI_WS_IN = "direct:inWS_";

    /**
     * URI of web service output endpoint for SOAP 1.1.
     */
    public static final String URI_WS_OUT = "direct:outWS_";

    /**
     * URI of web service output endpoint for SOAP 1.2.
     */
    public static final String URI_WS12_OUT = "direct:outWS12_";

    @Override
    public String getOutWsUri(String connectionUri, String messageSenderRef, String soapAction) {
        return URI_WS_OUT;
    }

    @Override
    public String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, String soapAction) {
        return URI_WS12_OUT;
    }

    @Override
    public String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params) {
        Assert.notNull(qName, "the qName must not be null");
        Assert.hasText(qName.getLocalPart(), "the localPart must not be empty");

        return URI_WS_IN + qName.getLocalPart();
    }
}