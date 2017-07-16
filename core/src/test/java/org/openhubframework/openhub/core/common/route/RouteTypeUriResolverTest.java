package org.openhubframework.openhub.core.common.route;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openhubframework.openhub.api.configuration.CoreProps.URI_INPUT_PATTERN_FILTER;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.core.AbstractCoreTest;

/**
 * Test for {@link RouteTypeUriResolver}.
 *
 * @author Roman Havlicek
 * @see RouteTypeUriResolver
 * @since 2.0
 */
@TestPropertySource(properties = {URI_INPUT_PATTERN_FILTER + "=direct:.*input.*"})
public class RouteTypeUriResolverTest extends AbstractCoreTest {

    @Autowired
    private RouteTypeUriResolver routeTypeUriResolver;

    /**
     * Test for method {@link RouteTypeUriResolver#findRouteType(RouteTypeInfo)}.
     */
    @Test
    public void testFindRouteTypeForRoute() throws Exception {
        getCamelContext().addRoutes(new AbstractBasicRoute() {
            @Override
            protected void doConfigure() throws Exception {
                from("direct:inputRoute")
                        .routeId("inputTestRouteOne")
                        .log("TestRoute");

                from("direct:inputUri")
                        .routeId("inputTestRouteTwo")
                        .log("TestRoute");

                from("direct:outputRoute")
                        .routeId("outputTestRouteOne")
                        .log("TestRoute");
            }
        });
        assertThat(routeTypeUriResolver.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteOne"), null, null, null)),
                is(RouteTypeEnum.INPUT));
        assertThat(routeTypeUriResolver.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteTwo"), null, null, null)),
                is(RouteTypeEnum.INPUT));
        assertThat(routeTypeUriResolver.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("outputTestRouteOne"), null, null, null)),
                nullValue());

        assertThat(routeTypeUriResolver.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteOne"))), is(RouteTypeEnum.INPUT));
        assertThat(routeTypeUriResolver.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteTwo"))), is(RouteTypeEnum.INPUT));
        assertThat(routeTypeUriResolver.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("outputTestRouteOne"))), nullValue());
    }
}