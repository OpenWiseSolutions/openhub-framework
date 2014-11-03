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

package org.cleverbus.component.asynchchild;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.BindingTypeEnum;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;


/**
 * Endpoint for {@link AsynchChildComponent asynch-child} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class AsynchChildEndpoint extends DefaultEndpoint {

    private String service;
    private String operationName;
    private String correlationId;
    private String sourceSystem;
    private String objectId;
    private String funnelValue;
    private BindingTypeEnum bindingType = BindingTypeEnum.HARD;

    /**
     * Creates new endpoint.
     *
     * @param endpointUri the URI
     * @param component the "asynch-child" component
     */
    public AsynchChildEndpoint(String endpointUri, AsynchChildComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new AsynchChildProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("you cannot send messages to this endpoint:" + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getService() {
        return service;
    }

    /**
     * Sets message service name, e.g. "customer".
     *
     * @param service the service name
     */
    public void setService(String service) {
        this.service = service;
    }

    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets operation name, e.g. "createCustomer"
     *
     * @param operationName the operation name
     */
    public void setOperationName(@Nullable String operationName) {
        this.operationName = operationName;
    }

    @Nullable
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets correlation ID.
     *
     * @param correlationId the correlation ID
     */
    public void setCorrelationId(@Nullable String correlationId) {
        this.correlationId = correlationId;
    }

    @Nullable
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Sets source system, e.g. "CRM".
     *
     * @param sourceSystem the source system
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public BindingTypeEnum getBindingType() {
        return bindingType;
    }

    /**
     * Sets binding type, possible values are {@link BindingTypeEnum}.
     *
     * @param bindingType the binding type
     */
    public void setBindingType(BindingTypeEnum bindingType) {
        this.bindingType = bindingType;
    }

    @Nullable
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets object ID that will be changed during message processing.
     *
     * @param objectId the object ID
     */
    public void setObjectId(@Nullable String objectId) {
        this.objectId = objectId;
    }

    @Nullable
    public String getFunnelValue() {
        return funnelValue;
    }

    /**
     * Sets funnel value for "funnel" filtering.
     *
     * @param funnelValue the funnel value
     */
    public void setFunnelValue(@Nullable String funnelValue) {
        this.funnelValue = funnelValue;
    }
}
