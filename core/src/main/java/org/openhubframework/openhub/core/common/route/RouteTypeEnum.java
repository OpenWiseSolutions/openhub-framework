package org.openhubframework.openhub.core.common.route;

import org.openhubframework.openhub.api.route.RouteType;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Contains all base route types.
 *
 * @author Roman Havlicek
 * @see RouteType
 * @see RouteTypeResolver
 * @see RouteDefinitionService
 * @see RouteTypeInfo
 * @since 2.0
 */
public enum RouteTypeEnum implements RouteType {

    /**
     * Route of this type is input route into ESB (web service, REST etc.).
     */
    INPUT;

    @Override
    public String getName() {
        return name();
    }
}
