package org.openhubframework.openhub.core.common.route;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openhubframework.openhub.api.configuration.CoreProps.URI_INPUT_PATTERN_FILTER;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.RouteTypeInfo;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;

/**
 * Test for {@link RouteDefinitionServiceImpl}.
 *
 * @author Roman Havlicek
 * @see RouteDefinitionServiceImpl
 * @since 2.0
 */
@TestPropertySource(properties = {URI_INPUT_PATTERN_FILTER + "=direct:.*input.*"})
public class RouteDefinitionServiceImplTest extends AbstractCoreTest {

    @Autowired
    private RouteDefinitionServiceImpl routeDefinitionService;

    /**
     * Init routes for test.
     *
     * @throws Exception all errors
     */
    @Before
    public void init() throws Exception {
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
    }

    /**
     * Test method {@link RouteDefinitionServiceImpl#findRouteType(RouteTypeInfo)}.
     *
     * @throws Exception all errors
     */
    @Test
    public void testFindRouteType() throws Exception {
        assertThat(routeDefinitionService.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteOne"), null, null, null)),
                is(RouteTypeEnum.INPUT));
        assertThat(routeDefinitionService.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteTwo"), null, null, null)),
                is(RouteTypeEnum.INPUT));
        assertThat(routeDefinitionService.findRouteType(
                        new RouteTypeInfo(getCamelContext().getRoute("outputTestRouteOne"), null, null, null)),
                nullValue());

        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteOne"))), is(RouteTypeEnum.INPUT));
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteTwo"))), is(RouteTypeEnum.INPUT));
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("outputTestRouteOne"))), nullValue());

        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getRouteId(ServiceTestEnum.CUSTOMER, "operation"), null)), nullValue());
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getExternalRouteId(ExternalSystemTestEnum.CRM, "operation"), null)), nullValue());
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getOutRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), nullValue());
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), is(RouteTypeEnum.INPUT));
        assertThat(routeDefinitionService.findRouteType(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.CUSTOMER, "test"), null)), is(RouteTypeEnum.INPUT));
    }

    /**
     * Test method {@link RouteDefinitionServiceImpl#isInputRoute(RouteTypeInfo)}.
     *
     * @throws Exception all errors
     */
    @Test
    public void testIsInputRoute() throws Exception {
        assertThat(routeDefinitionService.isInputRoute(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteOne"), null, null, null)),
                is(true));
        assertThat(routeDefinitionService.isInputRoute(
                        new RouteTypeInfo(getCamelContext().getRoute("inputTestRouteTwo"), null, null, null)),
                is(true));
        assertThat(routeDefinitionService.isInputRoute(
                        new RouteTypeInfo(getCamelContext().getRoute("outputTestRouteOne"), null, null, null)),
                is(false));

        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteOne"))), is(true));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("inputTestRouteTwo"))), is(true));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null, null,
                getCamelContext().getRouteDefinition("outputTestRouteOne"))), is(false));

        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getRouteId(ServiceTestEnum.CUSTOMER, "operation"), null)), is(false));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getExternalRouteId(ExternalSystemTestEnum.CRM, "operation"), null)), is(false));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getOutRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), is(false));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.ACCOUNT, "operation"), null)), is(true));
        assertThat(routeDefinitionService.isInputRoute(new RouteTypeInfo(null, null,
                AbstractBasicRoute.getInRouteId(ServiceTestEnum.CUSTOMER, "test"), null)), is(true));
    }
}