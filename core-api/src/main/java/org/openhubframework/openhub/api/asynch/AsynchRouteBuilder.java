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

package org.openhubframework.openhub.api.asynch;

import static org.apache.camel.builder.Builder.constant;
import static org.openhubframework.openhub.api.common.jaxb.JaxbDataFormatHelper.jaxb;

import javax.annotation.Nullable;

import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.model.AsynchResponse;
import org.openhubframework.openhub.api.asynch.model.CallbackResponse;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.XPathValidator;


/**
 * Helper class for creating input route for asynch. messages.
 * <p/>
 * Create new instance with calling
 * {@link #newInstance(ServiceExtEnum, String, String, AsynchResponseProcessor, DataFormatDefinition)} method,
 * sets parameters and finally call {@link #build(RouteBuilder)} method for creating route definition.
 *
 * @author Petr Juza
 * @author Tomas Hanus
 */
public final class AsynchRouteBuilder {

    private String inUri;
    private ServiceExtEnum serviceType;
    private String operation;
    private @Nullable String routeId;
    private @Nullable Processor[] validators;
    private @Nullable Expression objectIdExpr;
    private @Nullable Expression funnelValue;
    private boolean guaranteedOrder;
    private boolean excludeFailedState;
    private AsynchResponseProcessor responseProcessor;
    private DataFormatDefinition responseMarshalling;
    private @Nullable String policyRef;

    private AsynchRouteBuilder(String inUri, ServiceExtEnum serviceType, String operation,
            AsynchResponseProcessor responseProcessor, DataFormatDefinition responseMarshalling) {
        Assert.hasText(operation, "the operation must not be empty");
        Assert.notNull(inUri, "the inUri must not be empty");
        Assert.notNull(serviceType, "the serviceType must not be null");
        Assert.notNull(responseProcessor, "the responseProcessor must not be null");
        Assert.notNull(responseMarshalling, "the responseMarshalling must not be null");

        this.inUri = inUri;
        this.serviceType = serviceType;
        this.operation = operation;
        this.responseProcessor = responseProcessor;
        this.responseMarshalling = responseMarshalling;
    }

    /**
     * Creates new instance with basic mandatory parameters.
     *
     * @param serviceType service type
     * @param operation operation name with processing route
     * @param inUri the from URI of this route
     * @param responseProcessor the response processor
     * @param responseMarshalling the response marshalling
     * @return new instance
     */
    public static AsynchRouteBuilder newInstance(ServiceExtEnum serviceType, String operation, String inUri,
            AsynchResponseProcessor responseProcessor, DataFormatDefinition responseMarshalling) {
        return new AsynchRouteBuilder(inUri, serviceType, operation, responseProcessor, responseMarshalling);
    }

    /**
     * Sets route ID. If not defined then it will be created in standard way by
     * calling {@link AbstractBasicRoute#getInRouteId(ServiceExtEnum, String)}.
     *
     * @param routeId the route ID
     */
    public AsynchRouteBuilder withRouteId(@Nullable String routeId) {
        this.routeId = routeId;
        return this;
    }

    /**
     * Sets array of validators, for example {@link XPathValidator}.
     *
     * @param validator the validator
     */
    public AsynchRouteBuilder withValidator(@Nullable Processor... validator) {
        this.validators = validator;
        return this;
    }

    /**
     * Sets expression for evaluating object ID.
     *
     * @param objectIdExpr expression for evaluating object ID
     * @see Message#getObjectId()
     */
    public AsynchRouteBuilder withObjectIdExpr(@Nullable Expression objectIdExpr) {
        this.objectIdExpr = objectIdExpr;
        return this;
    }

    /**
     * Sets funnel value.
     *
     * @param funnelValue the funnel value
     * @see Message#getFunnelValue()
     */
    public AsynchRouteBuilder withFunnelValue(@Nullable Expression funnelValue) {
        this.funnelValue = funnelValue;
        return this;
    }

    /**
     * Marks the route for processing in guaranteed order by message timestamp.
     */
    public AsynchRouteBuilder withGuaranteedOrder() {
        this.guaranteedOrder = true;
        this.excludeFailedState = false;
        return this;
    }

    /**
     * Marks the route for processing in guaranteed order by message timestamp.
     */
    public AsynchRouteBuilder withGuaranteedOrderWithoutFailed() {
        this.guaranteedOrder = true;
        this.excludeFailedState = true;
        return this;
    }

    /**
     * Sets response processor. If not set then general response {@link AsynchResponse} will be used.
     *
     * @param responseProcessor   the response processor
     * @param responseMarshalling the response marshalling
     */
    public AsynchRouteBuilder withResponseProcessor(AsynchResponseProcessor responseProcessor,
            DataFormatDefinition responseMarshalling) {
        Assert.notNull(responseProcessor, "the responseProcessor must not be null");
        Assert.notNull(responseMarshalling, "the responseMarshalling must not be null");

        this.responseProcessor = responseProcessor;
        this.responseMarshalling = responseMarshalling;
        return this;
    }

    /**
     * Sets (Spring bean) reference to policy, e.g. authorization policy.
     *
     * @param policyRef the reference to policy
     */
    public AsynchRouteBuilder withPolicyRef(@Nullable String policyRef) {
        this.policyRef = policyRef;
        return this;
    }

    public String getInUri() {
        return inUri;
    }

    public ServiceExtEnum getServiceType() {
        return serviceType;
    }

    public String getOperation() {
        return operation;
    }

    @Nullable
    public String getRouteId() {
        return routeId;
    }

    @Nullable
    public Processor[] getValidators() {
        return validators;
    }

    @Nullable
    public Expression getObjectIdExpr() {
        return objectIdExpr;
    }

    @Nullable
    public Expression getFunnelValue() {
        return funnelValue;
    }

    @Nullable
    public AsynchResponseProcessor getResponseProcessor() {
        return responseProcessor;
    }

    @Nullable
    public DataFormatDefinition getResponseMarshalling() {
        return responseMarshalling;
    }

    @Nullable
    public String getPolicyRef() {
        return policyRef;
    }

    /**
     * Builds new route definition for processing incoming asynchronous messages.
     *
     * @param route current route builder
     * @return route definition
     */
    public final RouteDefinition build(RouteBuilder route) {
        Assert.notNull(route, "the route must not be null");

        // check guaranteed order - funnel value must be filled
        if (guaranteedOrder && funnelValue == null) {
            throw new IllegalStateException("There is no funnel value for guaranteed order.");
        }

        String finalRouteId = routeId;
        if (finalRouteId == null) {
            finalRouteId = AbstractBasicRoute.getInRouteId(serviceType, operation);
        }

        RouteDefinition routeDefinition = route.from(inUri).routeId(finalRouteId);

        if (validators != null) {
            for (Processor validator : validators) {
                routeDefinition.process(validator);
            }
        }

        if (policyRef != null) {
            routeDefinition.policy(policyRef);
        }

        if (objectIdExpr != null) {
            routeDefinition.setHeader(AsynchConstants.OBJECT_ID_HEADER, objectIdExpr);
        }

        if (funnelValue != null) {
            routeDefinition.setHeader(AsynchConstants.FUNNEL_VALUE_HEADER, funnelValue);
        }

        if (guaranteedOrder) {
            routeDefinition.setHeader(AsynchConstants.GUARANTEED_ORDER_HEADER, constant(true));
        }

        if (excludeFailedState) {
            routeDefinition.setHeader(AsynchConstants.EXCLUDE_FAILED_HEADER, constant(true));
        }

        // header values
        routeDefinition.setHeader(AsynchConstants.SERVICE_HEADER, route.constant(serviceType));
        routeDefinition.setHeader(AsynchConstants.OPERATION_HEADER, route.constant(operation));

        // route the request to asynchronous processing
        routeDefinition.to(AsynchConstants.URI_ASYNCH_IN_MSG);

        // create response
        if (responseProcessor != null) {
            routeDefinition.process(responseProcessor);
        } else {
            // use default response, general for all asynch. requests
            routeDefinition.process(new AsynchResponseProcessor() {

                @Override
                protected Object setCallbackResponse(CallbackResponse callbackResponse) {
                    AsynchResponse asynchResponse = new AsynchResponse();
                    asynchResponse.setConfirmAsynchRequest(callbackResponse);

                    return asynchResponse;
                }
            });
        }

        // response -> XML
        if (responseMarshalling != null) {
            routeDefinition.marshal(responseMarshalling);
        } else {
            routeDefinition.marshal(jaxb(AsynchResponse.class));
        }

        return routeDefinition;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("inUri", inUri)
            .append("serviceType", serviceType.getServiceName())
            .append("operation", operation)
            .append("policyRef", policyRef)
            .append("objectId", objectIdExpr)
            .append("funnelValue", funnelValue)
            .toString();
    }
}
