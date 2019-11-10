package org.openhubframework.openhub.component.circuitbreaker;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.component.AbstractComponentsTest;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

/**
 * Simple test suite for {@link CircuitComponent}.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
@TestPropertySource(properties = {
        "ohf.circuitbreaker.enabled=true",
        "ohf.circuitbreaker.impl=org.openhubframework.openhub.core.circuitbreaker.CircuitBreakerInMemoryImpl",
        "ohf.foo.circuitbreaker.enabled=true",
        "ohf.foo.circuitbreaker.thresholdPercentage=90",
        "ohf.foo.circuitbreaker.windowSizeInMillis=10000",
        "ohf.foo.circuitbreaker.minimalCountInWindow=10",
        "ohf.foo.circuitbreaker.sleepInMillis=30000",
})
@EnableConfigurationProperties(CircuitComponentTest.FooCircuitConfigurationProperties.class)
public class CircuitComponentTest extends AbstractComponentsTest {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private FooCircuitConfigurationProperties fooCircuitConfigProps;

    @Before
    public void prepareRoutes() throws Exception {
        final RouteBuilder testedRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    // configure circuit breaker component
                    .setProperty(CircuitBreaker.CONFIGURATION_PROPERTY, constant(fooCircuitConfigProps))
                    // circuit:<circuit-name>:<uri>
                    .to("circuit:foo:mock:test");
            }
        };
        // add to camel context
        getCamelContext().addRoutes(testedRoute);
    }

    @Test
    public void test_passThrough() throws Exception {

        mock.expectedMessageCount(2);
        producer.sendBody("payload-1");
        producer.sendBody("payload-2");
        mock.assertIsSatisfied();
    }

    @Test
    public void test_circuitDown() throws Exception {
        fooCircuitConfigProps.setMinimalCountInWindow(1L);
        fooCircuitConfigProps.setThresholdPercentage(1);
        fooCircuitConfigProps.setSleepInMillis(60_000);
        fooCircuitConfigProps.setWindowSizeInMillis(30_000);

        mock.whenExchangeReceived(1, exchange -> {
            throw new RuntimeException("Something went wrong.");
        });

        mock.expectedMessageCount(1);
        sendBodyInTryCatch("payload-1");
        sendBodyInTryCatch("payload-2");
        mock.assertIsSatisfied();
    }

    // util methods
    protected void sendBodyInTryCatch(Object body) {
        try {
            producer.sendBody(body);
        } catch (Exception ex) {
            // do nothing
        }
    }

    @ConfigurationProperties(prefix = "ohf.foo.circuitbreaker")
    public static class FooCircuitConfigurationProperties extends CircuitConfiguration {
        // nothing in here, already inherited from CircuitConfiguration
    }
}
