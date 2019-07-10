package org.openhubframework.openhub.component.circuitbreaker;

import static org.springframework.util.StringUtils.hasText;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.springframework.util.Assert;

/**
 * Camel Producer for Circuit breaker component.
 *
 * @author Karel Kovarik
 * @see org.apache.camel.Producer
 * @see CircuitComponent
 * @since 2.2
 */
public class CircuitProducer extends DefaultProducer {

    /**
     * New instance of circuit producer.
     *
     * @param endpoint the related circuit endpoint.
     */
    public CircuitProducer(CircuitEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // verify configuration is present, otherwise fail as it is misconfigured
        final CircuitConfiguration circuitConfiguration = exchange.getProperty(
                CircuitBreaker.CONFIGURATION_PROPERTY, CircuitConfiguration.class);
        Assert.notNull(circuitConfiguration, "the circuitConfiguration was not found,"
                + "it is expected to be set in exchange property with name ["
                + CircuitBreaker.CONFIGURATION_PROPERTY + "].");

        // fill circuitName (if not set in configuration already)
        if (!hasText(circuitConfiguration.getCircuitName())) {
            circuitConfiguration.setCircuitName(getCircuitEndpoint().getCircuitName());
        }

        final CircuitEndpoint endpoint = getCircuitEndpoint();
        try {
            // check circuit state, will throw exception if circuit is down
            endpoint.getCircuitBreaker().checkCircuitIsOpen().process(exchange);
            // invoke targetUri
            exchange = endpoint.getProducerTemplate().send(endpoint.getTargetUri(), exchange);
        } finally {
            // update circuitState
            endpoint.getCircuitBreaker().updateCircuitState().process(exchange);
        }
    }

    /**
     * Get related circuit endpoint.
     */
    public CircuitEndpoint getCircuitEndpoint() {
        return (CircuitEndpoint) getEndpoint();
    }
}
