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

import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;

import org.apache.camel.LoggingLevel;


/**
 * Route definition for ping service - listens to {@code /http/ping} url.
 * Simple ping service for monitoring of the server.
 *
 * @author Petr Juza
 */
@CamelConfiguration
public class PingRoute extends AbstractBasicRoute {

    private static final String ROUTE_ID_PING = "ping" + IN_ROUTE_SUFFIX;

    @Override
    public void doConfigure() throws Exception {
        from("servlet:///ping?servletName=" + RouteConstants.CAMEL_SERVLET)
                .routeId(ROUTE_ID_PING)

                .log(LoggingLevel.INFO, "Incoming PING request ... ")
                .transform(constant("PONG\n"));
    }
}
