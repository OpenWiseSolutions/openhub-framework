package org.openhubframework.openhub.api.route;

import javax.annotation.Nullable;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Contains information about route for getting {@link RouteType} in {@link RouteTypeResolver}.
 *
 * @author Romah Havlicek
 * @see RouteType
 * @see RouteTypeResolver
 * @since 2.0
 */
public class RouteTypeInfo {

    /**
     * Route.
     */
    private final Route route;

    /**
     * Exchange.
     */
    private final Exchange exchange;

    /**
     * Route id.
     */
    private final String routeId;

    /**
     * Route definition.
     */
    private final RouteDefinition routeDefinition;

    /**
     * New instance.
     *
     * @param route           route, {@code NULl} no route object
     * @param exchange        exchange, {@code NULL} no exchange object
     * @param routeId         route id, {@code NULL} no route id
     * @param routeDefinition route definition, {@code NULL} no route definition
     */
    public RouteTypeInfo(@Nullable Route route, @Nullable Exchange exchange, @Nullable String routeId,
                         @Nullable RouteDefinition routeDefinition) {
        this.route = route;
        this.exchange = exchange;
        this.routeId = routeId;
        this.routeDefinition = routeDefinition;
    }

    //--------------------------------------------------- SET / GET ----------------------------------------------------

    /**
     * Gets route.
     *
     * @return route, {@code NULL} - no route
     */
    @Nullable
    public Route getRoute() {
        return route;
    }

    /**
     * Gets exchange.
     *
     * @return exchange, {@code NULL} - no exchange
     */
    @Nullable
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * Gets route id.
     *
     * @return route id, {@code NULL} - no route id
     */
    @Nullable
    public String getRouteId() {
        return routeId;
    }

    /**
     * Gets route definition.
     *
     * @return route definition, {@code NULL} route definition
     */
    @Nullable
    public RouteDefinition getRouteDefinition() {
        return routeDefinition;
    }

    //--------------------------------------------- TOSTRING / HASH / EQUALS -------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RouteTypeInfo)) {
            return false;
        }

        RouteTypeInfo that = (RouteTypeInfo) o;

        return new EqualsBuilder()
                .append(getRoute(), that.getRoute())
                .append(getExchange(), that.getExchange())
                .append(getRouteId(), that.getRouteId())
                .append(getRouteDefinition(), that.getRouteDefinition())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getRoute())
                .append(getExchange())
                .append(getRouteId())
                .append(getRouteDefinition())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("route", route)
                .append("exchange", exchange)
                .append("routeId", routeId)
                .append("routeDefinition", routeDefinition)
                .toString();
    }
}
