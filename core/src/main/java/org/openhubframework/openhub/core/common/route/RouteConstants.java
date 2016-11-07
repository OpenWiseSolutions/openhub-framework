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

/**
 * Common route constants.
 *
 * @author Petr Juza
 */
public final class RouteConstants {

    private RouteConstants() {
    }

    /**
     * Name of the servlet for incoming HTTP communication.
     */
    public static final String CAMEL_SERVLET = "CamelServlet";

    /**
     * URI prefix for web services - "spring-ws" in web.xml.
     */
    public static final String WS_URI_PREFIX = "/ws/";

    /**
     * URI prefix for CamelServlet (see web.xml).
     */
    public static final String HTTP_URI_PREFIX = "/http/";

    /**
     * URI prefix for web admin GUI - "spring-admin-mvc" in web.xml.
     */
    public static final String WEB_URI_PREFIX = "/web/admin/";
}
