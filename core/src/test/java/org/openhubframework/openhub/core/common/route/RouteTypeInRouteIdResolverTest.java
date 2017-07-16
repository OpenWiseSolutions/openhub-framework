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
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;

/**
 * Test for {@link RouteTypeInRouteIdResolver}.
 *
 * @author Roman Havlicek
 * @see RouteTypeInRouteIdResolver
 * @since 2.0
 */
@TestPropertySource(properties = {URI_INPUT_PATTERN_FILTER + "=direct:inputUri"})
public class RouteTypeInRouteIdResolverTest extends AbstractCoreTest {

    @Autowired
    private RouteTypeInRouteIdResolver routeTypeInRouteIdResolver;

    /**
     * Test for method {@link RouteTypeInRouteIdResolver#findRouteType(RouteTypeInfo)}.
     */
    @Test
    public void testFindRouteTypeForRoute() {
        assertThat(routeTypeInRouteIdResolver.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getRouteId(ServiceTestEnum.CUSTOMER, "operation"), null)), nullValue());
        assertThat(routeTypeInRouteIdResolver.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getExternalRouteId(ExternalSystemTestEnum.CRM, "operation"), null)), nullValue());
        assertThat(routeTypeInRouteIdResolver.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getOutRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), nullValue());
        assertThat(routeTypeInRouteIdResolver.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), is(RouteTypeEnum.INPUT));
        assertThat(routeTypeInRouteIdResolver.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.CUSTOMER, "test"), null)), is(RouteTypeEnum.INPUT));
    }
}