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

package org.cleverbus.api.route;

import org.cleverbus.api.entity.ServiceExtEnum;

import org.springframework.util.Assert;


/**
 * Parent route definition for extension routes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class AbstractExtRoute extends AbstractBasicRoute {

    /**
     * Suffix for extension routes.
     */
    public static final String EXT_ROUTE_SUFFIX = "_ext_route";

    /**
     * Suffix for asynchronous incoming routes, specific for extension routes.
     */
    public static final String EXT_IN_ROUTE_SUFFIX = "_in_route";

    /**
     * Suffix for asynchronous outbound routes, specific for extension routes.
     */
    public static final String EXT_OUT_ROUTE_SUFFIX = "_out_route";


    /**
     * Gets route ID for synchronous routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getRouteId(ServiceExtEnum, String)
     */
    public static String getExtRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous incoming routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getInRouteId(ServiceExtEnum, String)
     */
    public static String getExtInRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_IN_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous outbound routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getOutRouteId(ServiceExtEnum, String)
     */
    public static String getExtOutRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_OUT_ROUTE_SUFFIX;
    }
}
