package org.openhubframework.openhub.core.common.route;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.camel.Route;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.route.RouteType;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Resolver for getting {@link RouteType} by route input URI ({@link RouteDefinition#from(String)}).
 *
 * @author Roman Havlicek
 * @see RouteDefinition#from(String)
 * @see RouteTypeEnum
 * @see RouteType
 * @see RouteDefinitionService
 * @see RouteTypeInfo
 * @since 2.0
 */
@Component
@Order(RouteTypeUriResolver.ORDER)
public class RouteTypeUriResolver implements RouteTypeResolver {

    public static final int ORDER = 1;

    @ConfigurableValue(key = CoreProps.URI_INPUT_PATTERN_FILTER)
    private ConfigurationItem<String> inputUriPattern;

    @Nullable
    @Override
    public RouteType findRouteType(RouteTypeInfo routeTypeInfo) {
        Assert.notNull(routeTypeInfo, "routeTypeInfo must not be null");

        Set<String> uris = new HashSet<>();

        //check uri from route
        Route route = routeTypeInfo.getRoute();
        if (route != null && route.getConsumer() != null && route.getConsumer().getEndpoint() != null) {
            uris.add(routeTypeInfo.getRoute().getConsumer().getEndpoint().getEndpointUri());
        }

        //check uri from route definition
        if (routeTypeInfo.getRouteDefinition() != null) {
            for (FromDefinition fromDefinition : routeTypeInfo.getRouteDefinition().getInputs()) {
                if (!StringUtils.isBlank(fromDefinition.getUri())) {
                    uris.add(fromDefinition.getUri());
                }
            }
        }

        RouteType result = null;
        for (String uri : uris) {
            if (!StringUtils.isBlank(uri) && uri.matches(inputUriPattern.getValue())) {
                result = RouteTypeEnum.INPUT;
                break;
            }
        }
        return result;
    }
}
