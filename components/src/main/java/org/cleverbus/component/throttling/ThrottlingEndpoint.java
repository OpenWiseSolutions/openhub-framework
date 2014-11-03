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

package org.cleverbus.component.throttling;

import javax.annotation.Nullable;

import org.cleverbus.spi.throttling.ThrottlingProcessor;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.springframework.util.Assert;


/**
 * Endpoint for {@link ThrottlingComponent throttling} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingEndpoint extends DefaultEndpoint {

    private RequestTypeEnum requestType;

    private String operationName;

    /**
     * Creates new endpoint.
     *
     * @param endpointUri the URI
     * @param component the "throttling" component
     */
    public ThrottlingEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ThrottlingProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("you cannot send messages to this endpoint:" + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public RequestTypeEnum getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypeEnum requestType) {
        Assert.notNull(requestType, "requestType mustn't be null");

        this.requestType = requestType;
    }

    @Nullable
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(@Nullable String operationName) {
        this.operationName = operationName;
    }

    ThrottlingProcessor getThrottlingProcessor() {
        return ((ThrottlingComponent)getComponent()).getThrottlingProcessor();
    }
}
