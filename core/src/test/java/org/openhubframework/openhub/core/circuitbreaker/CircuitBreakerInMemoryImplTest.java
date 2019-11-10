package org.openhubframework.openhub.core.circuitbreaker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.openhubframework.openhub.core.CoreTestConfig;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;


@ContextConfiguration(classes = {
        CoreTestConfig.class,
        AbstractCircuitBreakerTest.TestContext.class,
        CircuitBreakerInMemoryImplTest.Context.class
})
@TestPropertySource(properties = {
        "ohf.circuitbreaker.enabled=true",
        "ohf.circuitbreaker.impl=org.openhubframework.openhub.core.circuitbreaker.CircuitBreakerInMemoryImpl"
})
public class CircuitBreakerInMemoryImplTest extends AbstractCircuitBreakerTest {

    @EndpointInject(uri = "mock:external-system")
    private MockEndpoint externalSystemMock;

    @Test
    public void test_circuitDown() throws Exception {
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(10_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(51);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-DOWN");

        externalSystemMock.reset();
        externalSystemMock.whenExchangeReceived(2, exchange -> {
            throw new RuntimeException("FAILED REQUEST2");
        });
        externalSystemMock.whenExchangeReceived(3, exchange -> {
            throw new RuntimeException("FAILED REQUEST3");
        });

        externalSystemMock.expectedMessageCount(3);

        producer.sendBody("PAYLOAD1");
        sendBodyInTryCatch("PAYLOAD2");
        sendBodyInTryCatch("PAYLOAD3");
        // circuit should be down, this call will not be performed
        sendBodyInTryCatch("PAYLOAD4");

        externalSystemMock.assertIsSatisfied();
    }

    @Test
    public void test_circuitReset() throws Exception {
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(1_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(50);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(1_000);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-RESET");

        externalSystemMock.reset();
        externalSystemMock.whenExchangeReceived(1, exchange -> {
            throw new RuntimeException("FAILED REQUEST2");
        });
        externalSystemMock.whenExchangeReceived(2, exchange -> {
            throw new RuntimeException("FAILED REQUEST3");
        });

        sendBodyInTryCatch("PAYLOAD1");
        sendBodyInTryCatch("PAYLOAD2");
        // circuit should be down, this call will not be performed
        sendBodyInTryCatch("PAYLOAD3");

        Thread.sleep(2_000);
        // circuit should be up again, and this call will be performed
        sendBodyInTryCatch("PAYLOAD4");

        final List<Exchange> exchangeList = externalSystemMock.getReceivedExchanges();
        assertThat(exchangeList, notNullValue());
        assertThat(exchangeList.size(), is(3));
        assertThat(exchangeList.get(0).getIn().getBody(String.class), is("PAYLOAD1"));
        assertThat(exchangeList.get(1).getIn().getBody(String.class), is("PAYLOAD2"));
        assertThat(exchangeList.get(2).getIn().getBody(String.class), is("PAYLOAD4"));
    }

    @Test
    public void test_circuitUp_minimalCount() throws Exception {
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(1_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(50);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(1_000);
        // minimal count effectively should disable
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-MINIMAL-COUNT");

        externalSystemMock.reset();
        // throw exception in any case
        externalSystemMock.whenAnyExchangeReceived(exchange -> {
            throw new RuntimeException("FAILED REQUEST");
        });

        int count = 10;
        externalSystemMock.expectedMessageCount(count);

        for (int i = 1; i <= count; i++) {
            sendBodyInTryCatch("PAYLOAD" + i);
        }

        externalSystemMock.assertIsSatisfied();
    }

    @Test
    public void test_circuitUp_percentage() throws Exception {
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(1_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(51);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-UP-PERCENTAGE");

        externalSystemMock.reset();
        externalSystemMock.whenExchangeReceived(2, exchange -> {
            throw new RuntimeException("FAILED REQUEST1");
        });

        externalSystemMock.expectedMessageCount(4);

        // should not trigger, as only one request failed out of 3, max fail ratio is 50%
        sendBodyInTryCatch("PAYLOAD1");
        sendBodyInTryCatch("PAYLOAD2");
        sendBodyInTryCatch("PAYLOAD3");
        sendBodyInTryCatch("PAYLOAD4");

        externalSystemMock.assertIsSatisfied();
    }

    @Test
    public void test_circuitUp_window() throws Exception {
        // window is too short
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(1);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(50);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-UP-WINDOW");

        externalSystemMock.reset();
        // throw exception in any case
        externalSystemMock.whenAnyExchangeReceived(exchange -> {
            throw new RuntimeException("FAILED REQUEST");
        });

        int count = 10;
        externalSystemMock.expectedMessageCount(count);

        for (int i = 1; i <= count; i++) {
            sendBodyInTryCatch("PAYLOAD" + i);
            Thread.sleep(2);
        }

        externalSystemMock.assertIsSatisfied();
    }

    @Test
    public void test_circuitDown_oneFail() throws Exception {
        // window is too short
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(1_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(100);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(1);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-DOWN-WINDOW");

        externalSystemMock.reset();
        // throw exception in any case
        externalSystemMock.whenAnyExchangeReceived(exchange -> {
            throw new RuntimeException("FAILED REQUEST");
        });

        int count = 10;
        externalSystemMock.expectedMessageCount(1);

        for (int i = 1; i <= count; i++) {
            sendBodyInTryCatch("PAYLOAD" + i);
        }

        externalSystemMock.assertIsSatisfied();
    }

    @Test
    public void test_circuitDown_withFallback() throws Exception {
        fallbackRoute.getCircuitConfiguration().setWindowSizeInMillis(10_000);
        fallbackRoute.getCircuitConfiguration().setThresholdPercentage(51);
        fallbackRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        fallbackRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        fallbackRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-DOWN-WITH-FALLBACK");

        externalSystemMock.reset();
        externalSystemMock.whenExchangeReceived(2, exchange -> {
            throw new RuntimeException("FAILED REQUEST2");
        });
        externalSystemMock.whenExchangeReceived(3, exchange -> {
            throw new RuntimeException("FAILED REQUEST3");
        });

        externalSystemMock.expectedMessageCount(3);

        sendBodyInTryCatch(fallbackRouteProducer, "PAYLOAD1");
        sendBodyInTryCatch(fallbackRouteProducer, "PAYLOAD2");
        sendBodyInTryCatch(fallbackRouteProducer, "PAYLOAD3");
        // circuit should be down, this call will not be performed, fallback response should be present instead
        final String response = fallbackRouteProducer.requestBody((Object) "PAYLOAD4", String.class);

        externalSystemMock.assertIsSatisfied();

        assertThat(response, is("FALLBACK"));
    }

    @Test
    public void test_circuitBreakerDisabled() throws Exception {
        simpleRoute.getCircuitConfiguration().setEnabled(Boolean.FALSE); // disabled
        simpleRoute.getCircuitConfiguration().setWindowSizeInMillis(10_000);
        simpleRoute.getCircuitConfiguration().setThresholdPercentage(51);
        simpleRoute.getCircuitConfiguration().setSleepInMillis(Integer.MAX_VALUE);
        simpleRoute.getCircuitConfiguration().setMinimalCountInWindow(2);
        simpleRoute.getCircuitConfiguration().setCircuitName("CIRCUIT-DISABLED");

        externalSystemMock.reset();
        externalSystemMock.whenExchangeReceived(2, exchange -> {
            throw new RuntimeException("FAILED REQUEST2");
        });
        externalSystemMock.whenExchangeReceived(3, exchange -> {
            throw new RuntimeException("FAILED REQUEST3");
        });

        externalSystemMock.expectedMessageCount(4);

        producer.sendBody("PAYLOAD1");
        sendBodyInTryCatch("PAYLOAD2");
        sendBodyInTryCatch("PAYLOAD3");
        // threshold should be triggered
        sendBodyInTryCatch("PAYLOAD4");

        externalSystemMock.assertIsSatisfied();
    }

    public static class Context {
        @Bean
        public CircuitBreaker circuitBreaker() {
            return new CircuitBreakerInMemoryImpl();
        }
    }

}