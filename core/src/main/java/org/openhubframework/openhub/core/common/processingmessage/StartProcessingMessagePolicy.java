package org.openhubframework.openhub.core.common.processingmessage;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.exception.StoppingException;
import org.openhubframework.openhub.core.common.route.RouteTypeEnum;
import org.openhubframework.openhub.spi.node.NodeService;
import org.openhubframework.openhub.spi.route.RouteDefinitionService;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.api.route.RouteTypeResolver;

/**
 * Policy that check if ESB is stopped and refused with exception {@link StoppingException} all incoming call.
 * Input route is recognized by method {@link RouteDefinitionService#isInputRoute(RouteTypeInfo)}.
 *
 * @author Roman Havlicek
 * @see RouteDefinitionService#isInputRoute(RouteTypeInfo)
 * @see RouteTypeEnum#INPUT
 * @see RouteTypeResolver
 * @see StartProcessingMessagePolicyFactory
 * @since 2.0
 */
public class StartProcessingMessagePolicy extends RoutePolicySupport {

    /**
     * Service for check if ESB is stopped.
     */
    private final NodeService nodeService;

    /**
     * New instance.
     *
     * @param nodeService node service
     */
    public StartProcessingMessagePolicy(NodeService nodeService) {
        Assert.notNull(nodeService, "nodeService must not be null");

        this.nodeService = nodeService;
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        Assert.notNull(route, "route must not be null");
        Assert.notNull(exchange, "exchange must not be null");

        if (!nodeService.getActualNode().isAbleToHandleNewMessages()) {
            exchange.setException(new StoppingException("ESB is stopping ..."));
        }
    }
}
