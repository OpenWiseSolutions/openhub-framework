package org.openhubframework.openhub.core.circuitbreaker;

import java.time.Instant;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitDownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Simple {@link CircuitBreaker} implementation base, does use {@link CircuitState} domain object
 * to store state.
 *
 * All the interface methods are implemented, in its minimal variant only method
 * to resolve CircuitState for given circuit is needed to be implemented.
 *
 * There are multiple "hooks" method that can be overriden - onSuccessCall, onCircuitDown,
 * see javadoc directly on each of them.
 *
 * @author Karel Kovarik
 * @see CircuitState
 * @since 2.2
 */
public abstract class AbstractCircuitBreaker implements CircuitBreaker {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCircuitBreaker.class);

    @Override
    public Processor updateCircuitState() {
        return exchange -> {
            final CircuitConfiguration circuitConfiguration = getCircuitConfiguration(exchange);
            final String circuitName = circuitConfiguration.getCircuitName();

            // logic based on exception
            if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null
                    || exchange.getException() != null) {
                final Throwable ex = exchange.getException() != null
                                     ? exchange.getException()
                                     : exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

                if (!(ex instanceof CircuitDownException)) {
                    LOG.trace("Circuit '{}' increase error count.", circuitName);
                    // failed call handling
                    onFailedCall(circuitName);

                    // update circuit state and check whether to break circuit or not
                    final CircuitState circuitState = getCircuitState(circuitName);
                    if (shouldBreakCircuit(circuitState, circuitConfiguration)) {
                        onCircuitSwitchDown(circuitName);
                    }
                } else {
                    // do nothing, as circuit is down
                    LOG.trace("Circuit '{}' is down, skipping.", circuitName);
                }

            } else {
                LOG.trace("Circuit '{}' increase success count.", circuitName);
                // no exception - just update success count
                try {
                    // success call handling
                    onSuccessCall(circuitName);
                } catch (final Exception e) {
                    LOG.error("Unable to process circuit breaker data (step 1)", e);
                }
            }

            // remove old values (if present)
            try {
                removeOldEntries(circuitConfiguration);
            } catch (final Exception e) {
                LOG.error("Unable to process circuit breaker data (step 2)", e);
            }
        };
    }

    @Override
    public Processor checkCircuitIsOpen() throws CircuitDownException {
        return exchange -> {
            final CircuitConfiguration circuitConfiguration = getCircuitConfiguration(exchange);

            // if circuitBreaker for circuit is disabled, do not check anything
            if (!circuitConfiguration.isEnabled()) {
                return;
            }

            final CircuitState circuitState = getCircuitState(circuitConfiguration.getCircuitName());
            Assert.notNull(circuitState, "the circuitState must not be null");

            // if circuit is down, do not continue, throw exception
            if (circuitState.getLastShortcutTimestamp() + circuitConfiguration.getSleepInMillis() > Instant.now().toEpochMilli()) {
                LOG.trace("Circuit is down, lastShortcutTimestamp is {}, will try again after {}.",
                        circuitState.getLastShortcutTimestamp(),
                        circuitState.getLastShortcutTimestamp() + circuitConfiguration.getSleepInMillis());
                // do not continue, as circuit is down
                throw new CircuitDownException("circuit '" + circuitConfiguration.getCircuitName() + "'");
            }

            // otherwise just continue
        };
    }

    /**
     * Logic to resolve if circuit should change it state to down.
     *
     * @param circuitState the circuit state.
     * @param circuitConfiguration the circuit configuration.
     * @return flag, true if circuit should switch down.
     */
    protected boolean shouldBreakCircuit(CircuitState circuitState, CircuitConfiguration circuitConfiguration) {
        final long windowStart = Instant.now().toEpochMilli() - circuitConfiguration.getWindowSizeInMillis();

        // algorithm to determine whether it should break the circuit
        final long successCount = circuitState.getSuccessCallList().stream()
                .filter(it -> it > windowStart)
                .count();
        final long failedCount = circuitState.getFailedCallList().stream()
                .filter(it -> it > windowStart)
                .count();

        // sum requests in window, if not enough, just continue
        if (successCount + failedCount < circuitConfiguration.getMinimalCountInWindow()
                || successCount + failedCount < 1) {
            LOG.trace("Not enough requests in window, skipping.");
            return false;
        }

        final double failedCountPercentage = ((double) failedCount / (successCount + failedCount)) * 100;
        LOG.trace("FailedCountPercentage {} %, successCount = {}, failedCount = {}",
                failedCountPercentage, successCount, failedCount);
        if (failedCountPercentage >= circuitConfiguration.getThresholdPercentage()) {
            LOG.trace("Threshold triggered with '{}'% request failed, threshold is '{}'",
                    failedCountPercentage,
                    circuitConfiguration.getThresholdPercentage());
            return true;
        }

        return false;
    }

    /**
     * Get circuit state for circuit identified by its name.
     *
     * @param circuitName the name of circuit.
     * @return CircuitState, not null, if circuit is misconfigured, exception should be thrown.
     */
    protected abstract CircuitState getCircuitState(String circuitName);

    /**
     * On each successful exchange in given circuit.
     *
     * @param circuitName the name of circuit.
     */
    protected void onSuccessCall(String circuitName) {
        final CircuitState circuitState = getCircuitState(circuitName);
        Assert.notNull(circuitState, "the circuitState must not be null.");

        // insert success event
        circuitState.getSuccessCallList().add(Instant.now().toEpochMilli());
    }

    /**
     * On each failed exchange in given circuit.
     * Failed means exception is present.
     *
     * @param circuitName the name of circuit.
     */
    protected void onFailedCall(String circuitName) {
        final CircuitState circuitState = getCircuitState(circuitName);
        Assert.notNull(circuitState, "the circuitState must not be null.");

        // insert failed event
        circuitState.getFailedCallList().add(Instant.now().toEpochMilli());
    }

    /**
     * Invoked once, when circuit state is changed to down.
     *
     * @param circuitName the name of circuit.
     */
    protected void onCircuitSwitchDown(String circuitName) {
        final CircuitState circuitState = getCircuitState(circuitName);
        Assert.notNull(circuitState, "the circuitState must not be null.");

        // reset counters
        circuitState.resetFailedCallList();
        circuitState.resetSuccessCallList();
        // set lastShortcutTimestamp
        circuitState.setLastShortcutTimestamp(Instant.now().toEpochMilli());
    }

    /**
     * Invoked each time exchange is passed via CircuitBreaker. It is expected to clean
     * old entries in circuit state.
     *
     * @param configuration the configuration of circuit.
     */
    protected void removeOldEntries(CircuitConfiguration configuration) {
        Assert.notNull(configuration, "the circuitConfiguration must not be null");

        final CircuitState circuitState = getCircuitState(configuration.getCircuitName());
        Assert.notNull(circuitState, "the circuitState must not be null.");

        circuitState.getSuccessCallList()
                .removeIf(it -> it < Instant.now().toEpochMilli() - configuration.getWindowSizeInMillis());
        circuitState.getFailedCallList()
                .removeIf(it -> it < Instant.now().toEpochMilli() - configuration.getWindowSizeInMillis());
    }
}
