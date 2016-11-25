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

package org.openhubframework.openhub.component.throttling;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.lang3.StringUtils;
import org.openhubframework.openhub.spi.throttling.ThrottlingProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Apache Camel component "throttling" for throttling processing messages.
 * <p>
 * Syntax: {@code throttling:requestType[:operationName]}, where
 * <ul>
 *     <li>requestType specifies request type, e.g. SYNC or ASYNC
 *     <li>operation name, e.g. "createCustomer" (mandatory for SYNC request type only)
 * </ul>
 *
 * @author Petr Juza
 */
public class ThrottlingComponent extends DefaultComponent {

    @Autowired
    private ThrottlingProcessor throttlingProcessor;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ThrottlingEndpoint endpoint = new ThrottlingEndpoint(uri, this);

        // parse URI - "requestType:operationName"
        String endpointURI = ObjectHelper.after(uri, ":");
        if (endpointURI != null && endpointURI.startsWith("//")) {
            endpointURI = endpointURI.substring(2);
        }

        endpointURI = StringUtils.trimToNull(endpointURI);
        Assert.hasText(endpointURI, "Throttling endpoint URI must not be empty");

        RequestTypeEnum requestTypeEnum;
        String requestType;
        String operationName = null;

        // endpointURI = "requestType:operationName"
        if (StringUtils.contains(endpointURI, ":")) {
            requestType = ObjectHelper.before(endpointURI, ":");
            operationName = ObjectHelper.after(endpointURI, ":");
        } else {
            requestType = endpointURI;
        }

        // check request type value
        if (requestType.equalsIgnoreCase(RequestTypeEnum.SYNC.name())
                || requestType.equalsIgnoreCase(RequestTypeEnum.ASYNC.name())) {
            requestTypeEnum = RequestTypeEnum.valueOf(requestType.toUpperCase());
        } else {
            throw new IllegalArgumentException("request type must have one of the following values: 'sync' or 'async'");
        }

        // check operation name for SYNC request type
        if (requestTypeEnum == RequestTypeEnum.SYNC && operationName == null) {
            throw new IllegalArgumentException("operation name is mandatory for 'sync' request type");
        }

        endpoint.setRequestType(requestTypeEnum);
        endpoint.setOperationName(operationName);

        return endpoint;
    }

    ThrottlingProcessor getThrottlingProcessor() {
        return throttlingProcessor;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // checking references
        Assert.notNull(throttlingProcessor, "throttlingProcessor mustn't be null");
    }
}
