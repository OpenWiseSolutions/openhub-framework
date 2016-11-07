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

package org.openhubframework.openhub.component.externalcall;

import java.util.Arrays;

import org.openhubframework.openhub.api.extcall.ExtCallComponentParams;
import org.openhubframework.openhub.spi.extcall.ExternalCallService;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ComponentConfiguration;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointConfiguration;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultComponentConfiguration;
import org.apache.camel.util.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Apache Camel Component for wrapping external calls with checks for duplicate and outdated calls.
 * <p/>
 * The URI format is the following:
 * {@code extcall:[keyType]:[targetURI]} where keyType can be one of:
 * <ol><li>{@code message} - to generate a key based on message source system and correlation ID,
 * effectively providing duplicate call protection, but not obsolete call protection</li>
 * <li>{@code entity} - to generate a key based on message objectId property,
 * providing both duplicate call protection and obsolete call protection</li>
 * <li>{@code custom} - to use a custom key provided in the {@link ExtCallComponentParams#EXTERNAL_CALL_KEY} exchange property</li>
 * </ol>
 * In the first two cases (message and entity),
 * if the {@link ExtCallComponentParams#EXTERNAL_CALL_KEY} exchange property is provided,
 * it will be appended to the generated key.
 * <p/>
 * By default, the {@code targetURI} is used as the operation.
 * This can be changed by providing an optional {@link ExtCallComponentParams#EXTERNAL_CALL_OPERATION} exchange property.
 * The targetURI will still be the URI that is called, if the external call is not skipped,
 * but the duplicate/obsolete protection logic will use the {@link ExtCallComponentParams#EXTERNAL_CALL_OPERATION} value for checking,
 * if the call should be made or skipped.
 */
public class ExternalCallComponent implements Component {

    @Autowired
    private ExternalCallService service;

    @Produce
    private ProducerTemplate producer;

    private CamelContext context;

    @Override
    public Endpoint createEndpoint(String uri) throws Exception {
        String endpointURI = ObjectHelper.after(uri, ":");
        if (endpointURI != null && endpointURI.startsWith("//")) {
            endpointURI = endpointURI.substring(2);
        }
        Assert.hasText(endpointURI, "External Call endpoint URI must not be empty");

        String keyTypeString = ObjectHelper.before(endpointURI, ":");
        Assert.notNull(keyTypeString, "External Call endpoint URI must be in format [keyType]:[targetURI]");
        String targetURI = ObjectHelper.after(endpointURI, ":");
        if (targetURI != null && targetURI.startsWith("//")) {
            targetURI = targetURI.substring(2);
        }
        Assert.hasText(keyTypeString, "External Call key type must not be empty");
        Assert.hasText(targetURI, "External Call target URI must not be empty");

        ExternalCallKeyType keyType = null;
        for (ExternalCallKeyType aKeyType : ExternalCallKeyType.values()) {
            if (aKeyType.name().equalsIgnoreCase(keyTypeString)) {
                keyType = aKeyType;
                break;
            }
        }
        Assert.notNull(keyType, String.format("External Call key type \"%s\" is invalid. It must be one of: %s",
                keyTypeString, Arrays.toString(ExternalCallKeyType.values())));

        return new ExternalCallEndpoint(uri, this, keyType, targetURI);
    }

    @Override
    public boolean useRawUri() {
        return false;
    }

    @Override
    public EndpointConfiguration createConfiguration(String uri) throws Exception {
        return null;
    }

    @Override
    public ComponentConfiguration createComponentConfiguration() {
        return new DefaultComponentConfiguration(this);
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        context = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return context;
    }

    ProducerTemplate getProducerTemplate() {
        return producer;
    }

    ExternalCallService getService() {
        return service;
    }
}
