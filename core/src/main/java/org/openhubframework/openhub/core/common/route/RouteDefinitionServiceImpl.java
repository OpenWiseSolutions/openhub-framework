package org.openhubframework.openhub.core.common.route;

import java.util.List;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.route.RouteType;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Default service for getting information about route.
 *
 * @author Roman Havlicek
 * @see RouteTypeResolver
 * @see RouteType
 * @see RouteTypeUriResolver
 * @see RouteTypeInRouteIdResolver
 * @see RouteTypeInfo
 * @since 2.0
 */
@Service
public class RouteDefinitionServiceImpl implements RouteDefinitionService {

    @Autowired(required = false)
    private List<RouteTypeResolver> routeTypeResolvers;

    @Nullable
    @Override
    public RouteType findRouteType(RouteTypeInfo routeTypeInfo) {
        Assert.notNull(routeTypeInfo, "routeTypeInfo must not be null");

        RouteType result = null;
        if (!CollectionUtils.isEmpty(routeTypeResolvers)) {
            for (RouteTypeResolver routeTypeResolver : routeTypeResolvers) {
                result = routeTypeResolver.findRouteType(routeTypeInfo);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean isInputRoute(RouteTypeInfo routeTypeInfo) {
        Assert.notNull(routeTypeInfo, "routeTypeInfo must not be null");

        RouteType routeType = findRouteType(routeTypeInfo);
        return routeType != null && routeType.getName().equals(RouteTypeEnum.INPUT.getName());
    }
}
