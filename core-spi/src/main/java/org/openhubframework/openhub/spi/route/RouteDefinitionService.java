package org.openhubframework.openhub.spi.route;

import javax.annotation.Nullable;

import org.openhubframework.openhub.api.route.RouteType;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Service for getting information about route.
 *
 * @author Roman Havlicek
 * @see RouteType
 * @see RouteTypeInfo
 * @see RouteTypeResolver
 * @since 2.0
 */
public interface RouteDefinitionService {

    /**
     * Gets {@link RouteType} from information about route in object {@link RouteTypeInfo}.
     *
     * @param routeTypeInfo information about route
     * @return route type, {@code NULL} route type was not recognized
     */
    @Nullable
    RouteType findRouteType(RouteTypeInfo routeTypeInfo);

    /**
     * Gets if route is input route.
     * Input route has input into ESB (Webservice, REST, etc.).
     *
     * @param routeTypeInfo infrmation about route
     * @return {@code true} is input route, {@code false} otherwise
     */
    boolean isInputRoute(RouteTypeInfo routeTypeInfo);
}
