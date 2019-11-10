package org.openhubframework.openhub.core.circuitbreaker;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.core.CoreTestConfig;
import org.openhubframework.openhub.core.common.asynch.ExceptionTranslationRoute;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitDownException;
import org.openhubframework.openhub.test.route.ActiveRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

/**
 * Parent for circuit breaker tests.
 *
 * Note: routes with circuit breaker do not use it as a component.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
@ContextConfiguration(classes = {
        CoreTestConfig.class,
        AbstractCircuitBreakerTest.TestContext.class
})
@ActiveRoutes(
        classes = {
                CircuitBreakerHazelcastImplTest.CircuitSimpleRoute.class,
                CircuitBreakerHazelcastImplTest.CircuitFallbackRoute.class,
                ExceptionTranslationRoute.class
        })
public abstract class AbstractCircuitBreakerTest extends AbstractCoreTest {

    @Autowired
    protected CircuitSimpleRoute simpleRoute;

    @Autowired
    protected CircuitFallbackRoute fallbackRoute;

    @Produce(uri = "direct:simple-route")
    protected ProducerTemplate producer;

    @Produce(uri = "direct:fallback-route")
    protected ProducerTemplate fallbackRouteProducer;

    public static class TestContext {
        @Bean
        public CircuitSimpleRoute circuitSimpleRoute() {
            return new CircuitSimpleRoute();
        }

        @Bean
        public CircuitFallbackRoute circuitFallbackRoute() {
            return new CircuitFallbackRoute();
        }
    }

    /**
     * Simple route with circuit breaker.
     */
    static class CircuitSimpleRoute extends AbstractBasicRoute {

        private CircuitConfiguration circuitConfiguration = new CircuitConfiguration();
        @Autowired
        private CircuitBreaker circuitBreaker;

        @Override
        protected void doConfigure() throws Exception {
            // @formatter:off
            from("direct:simple-route")
                    .setProperty(CircuitBreaker.CONFIGURATION_PROPERTY, constant(circuitConfiguration))
                    .doTry()
                        .process(circuitBreaker.checkCircuitIsOpen())
                        .to("mock:external-system")
                    .doFinally()
                        .process(circuitBreaker.updateCircuitState())
                    .end()
            ;
            // @formatter:on
        }

        public CircuitConfiguration getCircuitConfiguration() {
            return circuitConfiguration;
        }

    }

    /**
     * Route with circuit breaker and fallback.
     */
    static class CircuitFallbackRoute extends AbstractBasicRoute {

        private CircuitConfiguration circuitConfiguration = new CircuitConfiguration();
        @Autowired
        private CircuitBreaker circuitBreaker;

        @Override
        protected void doConfigure() throws Exception {
            // @formatter:off
            from("direct:fallback-route")

                    .doTry()
                        .setProperty(CircuitBreaker.CONFIGURATION_PROPERTY, constant(circuitConfiguration))
                        .process(circuitBreaker.checkCircuitIsOpen())
                        .to("mock:external-system")
                    .doCatch(CircuitDownException.class)
                        .process(exchange -> exchange.getIn().setBody("FALLBACK"))
                    .doFinally()
                        .process(circuitBreaker.updateCircuitState())
                    .end()
            ;
            // @formatter:on
        }

        public CircuitConfiguration getCircuitConfiguration() {
            return circuitConfiguration;
        }

    }

    // util methods
    protected void sendBodyInTryCatch(Object body) {
        try {
            producer.sendBody(body);
        } catch (Exception ex) {
            // do nothing
        }
    }

    protected void sendBodyInTryCatch(ProducerTemplate producer, Object body) {
        try {
            producer.sendBody(body);
        } catch (Exception ex) {
            // do nothing
        }
    }
}
