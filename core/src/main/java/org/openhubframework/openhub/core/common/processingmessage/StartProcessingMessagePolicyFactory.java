package org.openhubframework.openhub.core.common.processingmessage;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicy;
import org.apache.camel.spi.RoutePolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.openhubframework.openhub.core.common.route.RouteTypeEnum;
import org.openhubframework.openhub.spi.node.NodeService;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Factory for create {@link StartProcessingMessagePolicy} which refused incoming call for input route if ESB is stopped.
 * Input route is recognized by method {@link RouteDefinitionService#isInputRoute(RouteTypeInfo)}.
 *
 * @author Roman Havlicek
 * @see RouteDefinitionService#isInputRoute(RouteTypeInfo)
 * @see RouteTypeEnum#INPUT
 * @see RouteTypeResolver
 * @see StartProcessingMessagePolicy
 * @since 2.0
 */
@Component
public class StartProcessingMessagePolicyFactory implements RoutePolicyFactory {

    /**
     * One instance of {@link StartProcessingMessagePolicy}.
     * For getting instace use {@link #getStartProcessingMessagePolicy()}.
     */
    private StartProcessingMessagePolicy startProcessingMessagePolicy;

    @Autowired
    private RouteDefinitionService routeDefinitionService;

    @Autowired
    private NodeService nodeService;

    @Override
    public RoutePolicy createRoutePolicy(CamelContext camelContext, String routeId, RouteDefinition route) {
        Assert.notNull(camelContext, "camelContext must not be null");
        Assert.hasText(routeId, "routeId must not be empty");
        Assert.notNull(route, "route must not be null");

        if (routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null, routeId, route))) {
            return getStartProcessingMessagePolicy();
        } else {
            return null;
        }
    }

    /**
     * Get instance of {@link StartProcessingMessagePolicy}.
     *
     * @return instance of {@link StartProcessingMessagePolicy}
     */
    private synchronized StartProcessingMessagePolicy getStartProcessingMessagePolicy() {
        if (startProcessingMessagePolicy == null) {
            startProcessingMessagePolicy = new StartProcessingMessagePolicy(nodeService);
        }
        return startProcessingMessagePolicy;
    }
}
