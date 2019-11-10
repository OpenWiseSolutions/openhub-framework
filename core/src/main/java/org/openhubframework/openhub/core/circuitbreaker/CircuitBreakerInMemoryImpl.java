package org.openhubframework.openhub.core.circuitbreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link CircuitBreaker}, that uses in-memory storage.
 * It is supposed to be used for scenarios not requiring cluster-synchronized nor persistent
 * circuit breaker.
 *
 * All the circuit states are stored in map directly in implementation bean.
 *
 * @author Karel Kovarik
 * @see CircuitBreaker
 * @since 2.2
 */
public class CircuitBreakerInMemoryImpl extends AbstractCircuitBreaker {
    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerInMemoryImpl.class);

    // circuit state storage
    private Map<String, CircuitState> circuitBreakerMap = new ConcurrentHashMap<>();

    @Override
    protected CircuitState getCircuitState(String circuitName) {
        CircuitState circuitState = circuitBreakerMap.get(circuitName);

        if (null == circuitState) {
            LOG.trace("Do create circuit {}, as it is invoked for the first time.", circuitName);
            // probably first run, insert new circuit
            circuitBreakerMap.put(circuitName, new CircuitState());
        }

        return circuitBreakerMap.get(circuitName);
    }
}
