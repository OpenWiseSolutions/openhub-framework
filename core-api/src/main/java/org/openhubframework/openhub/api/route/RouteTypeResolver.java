package org.openhubframework.openhub.api.route;

import javax.annotation.Nullable;

/**
 * Resolver for getting {@link RouteType} from {@link RouteTypeInfo}.
 *
 * @author Romah Havlicek
 * @see RouteType
 * @see RouteTypeInfo
 * @since 2.0
 */
public interface RouteTypeResolver {

    /**
     * Gets {@link RouteType} from information about route in object {@link RouteTypeInfo}.
     *
     * @param routeTypeInfo information about route
     * @return route type, {@code NULL} route type was not recognized
     */
    @Nullable
    RouteType findRouteType(RouteTypeInfo routeTypeInfo);
}
