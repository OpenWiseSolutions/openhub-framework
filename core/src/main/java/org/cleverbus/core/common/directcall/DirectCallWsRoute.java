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

package org.cleverbus.core.common.directcall;

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.route.RouteConstants;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Route that can directly call specified web service URI via specified WS sender.
 * Route expects HTTP GET call with unique call identifier to {@link DirectCallRegistry}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration
public class DirectCallWsRoute extends AbstractBasicRoute {

    static final String SERVLET_URL = "directWS";
    static final String CALL_ID_HEADER = "callId";

    static final String ROUTE_ID_DIRECT_CALL = "directCall" + EXTERNAL_ROUTE_SUFFIX;
    static final String ROUTING_SLIP_ID = "routingSlip";

    @Autowired
    private DirectCallRegistry callRegistry;

    @Override
    protected void doConfigure() throws Exception {
        from("servlet:///" + SERVLET_URL + "?servletName=" + RouteConstants.CAMEL_SERVLET)
            .routeId(ROUTE_ID_DIRECT_CALL)

            .validate(header(CALL_ID_HEADER).isNotNull())

            .log(LoggingLevel.DEBUG, "Incoming direct WS call with ID: ${headers." + CALL_ID_HEADER + "} ")

            .bean(this, "setHeader")

            .bean(this, "getRequest")

            .convertBodyTo(String.class, "UTF-8")

            .routingSlip(method(this, "getWsUri")).id(ROUTING_SLIP_ID)

            .bean(this, "removeCallParams");
    }

    /**
     * Sets request header if available.
     *
     * @param callId Call ID for getting call parameters from {@link DirectCallRegistry}
     * @param exchange Camel exchange
     */
    @Handler
    public void setHeader(@Header(CALL_ID_HEADER) String callId, Exchange exchange) {
        Assert.hasText(callId, "the callId must not be empty");

        DirectCallParams params = callRegistry.getParams(callId);

        if (params.getHeader() != null) {
            Log.debug("Direct WS call: header=" + params.getHeader());

            exchange.getIn().setHeader(SpringWebserviceConstants.SPRING_WS_SOAP_HEADER, params.getHeader());
        }
    }

    /**
     * Gets request to external system.
     *
     * @param callId Call ID for getting call parameters from {@link DirectCallRegistry}
     * @return request
     */
    @Handler
    public Object getRequest(@Header(CALL_ID_HEADER) String callId) {
        Assert.hasText(callId, "the callId must not be empty");

        DirectCallParams params = callRegistry.getParams(callId);

        Log.debug("Direct WS call: uri= " + params.getUri() + ",\nsenderRef= " + params.getSenderRef()
                + ",\nsoapAction= " + params.getSoapAction() + ",\nbody: " + params.getBody());

        return params.getBody();
    }

    /**
     * Gets URI for calling external system.
     *
     * @param callId Call ID for getting call parameters from {@link DirectCallRegistry}
     * @return WS URI
     */
    @Handler
    public String getWsUri(@Header(CALL_ID_HEADER) String callId) {
        Assert.hasText(callId, "the callId must not be empty");

        DirectCallParams params = callRegistry.getParams(callId);

        return getOutWsUri(params.getUri(), params.getSenderRef(), params.getSoapAction());
    }

    @Handler
    public void removeCallParams(@Header(CALL_ID_HEADER) String callId) {
        Assert.hasText(callId, "the callId must not be empty");

        callRegistry.removeParams(callId);
    }
}
