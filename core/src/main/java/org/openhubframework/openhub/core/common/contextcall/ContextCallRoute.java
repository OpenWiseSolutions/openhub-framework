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

package org.openhubframework.openhub.core.common.contextcall;

import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.core.common.route.RouteConstants;


/**
 * Route that serves as input URI for calling from one Spring context to another context.
 * Route expects HTTP GET call with unique call identifier to {@link ContextCallRegistry}.
 *
 * @author Petr Juza
 */
@CamelConfiguration
public class ContextCallRoute extends AbstractBasicRoute {

    private static final Logger LOG = LoggerFactory.getLogger(ContextCallRoute.class);

    static final String SERVLET_URL = "contextCall";
    static final String CALL_ID_HEADER = "callId";

    static final String ROUTE_ID_CONTEXT_CALL = "contextCall" + EXTERNAL_ROUTE_SUFFIX;

    @Autowired
    private ContextCallRegistry callRegistry;

    @Override
    protected void doConfigure() throws Exception {
        from("servlet:///" + SERVLET_URL + "?servletName=" + RouteConstants.CAMEL_SERVLET)
            .routeId(ROUTE_ID_CONTEXT_CALL)

            .validate(header(CALL_ID_HEADER).isNotNull())

            .log(LoggingLevel.DEBUG, "Incoming context call with ID: ${headers." + CALL_ID_HEADER + "} ")

            .bean(this, "makeCall");
    }

    /**
     * Makes call.
     *
     * @param callId Call ID for getting call parameters from {@link ContextCallRegistry}
     */
    @Handler
    public void makeCall(@Header(CALL_ID_HEADER) String callId) {
        Assert.hasText(callId, "the callId must not be empty");

        // get params
        ContextCallParams params = callRegistry.getParams(callId);

        Object res = ReflectionCallUtils.invokeMethod(params, getApplicationContext());

        // save response
        callRegistry.addResponse(callId, res);

        LOG.debug("Response of the call ID '" + callId + "' was saved: " + res);
    }
}
