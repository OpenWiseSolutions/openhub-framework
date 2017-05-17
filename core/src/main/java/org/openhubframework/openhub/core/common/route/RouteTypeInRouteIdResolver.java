package org.openhubframework.openhub.core.common.route;

import javax.annotation.Nullable;

import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.route.RouteType;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Resolver for getting {@link RouteType} by route input route identifier ({@link RouteDefinition#routeId(String)}).
 *
 * @author Roman Havlicek
 * @see RouteDefinition#routeId(String)
 * @see RouteTypeEnum
 * @see RouteType
 * @see RouteDefinitionService
 * @see RouteTypeInfo
 * @since 2.0
 */
@Component
@Order(RouteTypeInRouteIdResolver.ORDER)
public class RouteTypeInRouteIdResolver implements RouteTypeResolver {

    public static final int ORDER = RouteTypeUriResolver.ORDER + 1;

    /**
     * Regular expression for check input route id ({@link AbstractBasicRoute#getInRouteId(ServiceExtEnum, String)}).
     */
    private static final String ROUTE_ID_IN_REG_EXP = ".+" + AbstractBasicRoute.ROUTE_ID_DELIMITER
            + ".+" + AbstractBasicRoute.IN_ROUTE_SUFFIX + ".*";

    @Nullable
    @Override
    public RouteType findRouteType(RouteTypeInfo routeTypeInfo) {
        Assert.notNull(routeTypeInfo, "routeTypeInfo must not be null");

        String routeId = routeTypeInfo.getRouteId();

        //check in routeDefinition
        if (StringUtils.isBlank(routeId) && routeTypeInfo.getRouteDefinition() != null) {
            routeId = routeTypeInfo.getRouteDefinition().getId();
        }

        //check in route
        if (StringUtils.isBlank(routeId) && routeTypeInfo.getRoute() != null) {
            routeId = routeTypeInfo.getRoute().getId();
        }

        RouteType result = null;
        if (!StringUtils.isBlank(routeId) && routeId.matches(ROUTE_ID_IN_REG_EXP)) {
            result = RouteTypeEnum.INPUT;
        }
        return result;
    }
}
