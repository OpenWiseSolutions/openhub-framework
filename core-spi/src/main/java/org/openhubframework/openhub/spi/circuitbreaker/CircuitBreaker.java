package org.openhubframework.openhub.spi.circuitbreaker;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.Assert;


/**
 * Contract for <b>Circuit Breaker</b>.
 * <p>
 * Recommended to be used with {@link CircuitComponent}, see its javadoc.
 * </p>
 * <b>Usage:</b>
 * <pre>
 * .doTry()
 *   .process(checkCircuitIsOpen())
 *   .to("external-system")
 * .doCatch(CircuitDownException.class)
 *   .process(fallBackProcessor(if needed, otherwise exception will be thrown))
 * .doFinally()
 *   .process(updateCircuitState())
 * .end()
 * </pre>
 *
 * <p>
 * <b>Configuration:</b>
 * <ul><li>instance of {@link CircuitConfiguration} is expected to be set in Exchange property
 * {@link CircuitBreaker#CONFIGURATION_PROPERTY}. See CircuitConfiguration for more info.
 * </li></ul>
 * </p>
 *
 * @author Karel Kovarik
 * @see CircuitConfiguration
 * @see CircuitDownException
 * @since 2.2
 */
public interface CircuitBreaker {

    /**
     * Exchange property to store configuration.
     */
    String CONFIGURATION_PROPERTY = "OHF-CircuitBreaker-CONFIGURATION";

    /**
     * Check whether circuit is open. Does nothing if yes,
     * however if circuit is breaked down, it does throw exception.
     *
     * @return processor to check circuit.
     * @throws CircuitDownException if circuit is in shortcut state.
     */
    Processor checkCircuitIsOpen() throws CircuitDownException;

    /**
     * Update circuit state. Should be called if service call
     * fail, or if succeeds.
     * Based on exception present, it should decide that call
     * was success or not.
     *
     * @return processor to update circuit state.
     */
    Processor updateCircuitState();

    /**
     * Get current circuit configuration.
     *
     * @param exchange the camel exchange.
     * @return the circuit configuration.
     */
    default CircuitConfiguration getCircuitConfiguration(Exchange exchange) {
        final CircuitConfiguration circuitConfiguration =
                exchange.getProperty(CONFIGURATION_PROPERTY, CircuitConfiguration.class);
        Assert.notNull(circuitConfiguration, "the circuitConfiguration was not found,"
                + "it is expected to be set in exchange property with name ["
                + CircuitBreaker.CONFIGURATION_PROPERTY + "].");

        return circuitConfiguration;
    }
}
