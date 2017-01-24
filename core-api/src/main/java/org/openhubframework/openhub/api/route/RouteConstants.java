/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.route;

import org.openhubframework.openhub.api.asynch.AsynchConstants;


/**
 * Common constants regarding to route implementation.
 *
 * @author Petr Juza
 * @see AsynchConstants
 * @see #MAPPING_SUFFIX
 * @since 2.0
 */
public final class RouteConstants {

    /**
     * Mapping suffix: {@code *}.
     */
    public static final String MAPPING_SUFFIX = "*";

    /**
     * Bean name of authorization policy for web services.
     */
    public static final String WS_AUTH_POLICY = "roleWsAuthPolicy";

    /**
     * Bean name of web services endpoint mapping.
     */
    public static final String ENDPOINT_MAPPING_BEAN = "endpointMapping";

    /**
     * Name of the servlet for incoming HTTP communication.
     */
    public static final String CAMEL_SERVLET = "CamelServlet";

    /**
     * Name of the servlet for incoming Web service communication.
     */
    public static final String WS_SERVLET = "MessageDispatcherServlet";
    
    /**
     * URI prefix for web services.
     *
     * @see #WS_URI_PREFIX_MAPPING
     */
    public static final String WS_URI_PREFIX = "/ws/";

    /**
     * URI prefix <strong>mapping</strong> for web services.
     *
     * @see #WS_URI_PREFIX
     */
    public static final String WS_URI_PREFIX_MAPPING = WS_URI_PREFIX + MAPPING_SUFFIX;

    /**
     * URI prefix for {@link #CAMEL_SERVLET CamelServlet}.
     *
     * @see #HTTP_URI_PREFIX_MAPPING
     */
    public static final String HTTP_URI_PREFIX = "/http/";

    /**
     * URI prefix <strong>mapping</strong> for {@link #CAMEL_SERVLET CamelServlet}.
     * 
     * @see #HTTP_URI_PREFIX
     */
    public static final String HTTP_URI_PREFIX_MAPPING = HTTP_URI_PREFIX + MAPPING_SUFFIX;

    /**
     * URI prefix for web admin console.
     * 
     * @see #WS_URI_PREFIX_MAPPING
     */
    public static final String WEB_URI_PREFIX = "/web/admin/";

    /**
     * URI prefix mapping for web admin console.
     *
     * @see #WS_URI_PREFIX
     */
    public static final String WEB_URI_PREFIX_MAPPING = WEB_URI_PREFIX + "*";

    private RouteConstants() {
    }
}
