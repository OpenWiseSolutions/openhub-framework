package org.openhubframework.openhub.core.circuitbreaker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.hazelcast.CircuitDownEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.hazelcast.FailedCallEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.hazelcast.RemoveOldCallsEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.hazelcast.SuccessCallEntryProcessor;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Implementation of {@link CircuitBreaker}, that uses HazelCast already configured in OpenHub.
 * It uses new key - {@link CircuitBreakerHazelcastImpl#HAZELCAST_KEY}.
 *
 * Each circuit is stored as hazelcast distributed Map. (http://docs.hazelcast.org/docs/2.3/manual/html/ch02s03.html)
 * To cope with possible race conditions, map is updated using {@link IMap#executeOnKey(Object, EntryProcessor)},
 * that should handle it.
 *
 * @author Karel Kovarik
 * @see CircuitBreaker
 * @since 2.2
 */
public class CircuitBreakerHazelcastImpl extends AbstractCircuitBreaker {

    /**
     * Key to hazelcast map, as defined in hazelcast configuration.
     */
    private static final String HAZELCAST_KEY = "circuitbreaker";

    /**
     * Instance of SuccessCallEntryProcessor.
     */
    private static final EntryProcessor<String, CircuitState> SUCCESS_CALL_ENTRY_PROCESSOR = new SuccessCallEntryProcessor();

    /**
     * Instance of FailedCallEntryProcessor.
     */
    private static final EntryProcessor<String, CircuitState> FAILED_CALL_ENTRY_PROCESSOR = new FailedCallEntryProcessor();

    /**
     * Instance of CircuitDownEntryProcessor.
     */
    private static final EntryProcessor<String, CircuitState> CIRCUIT_DOWN_ENTRY_PROCESSOR = new CircuitDownEntryProcessor();

    /**
     * Hazelcast instance that should be already configured.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;


    /**
     * Get global 'circuitbreaker' map from hazelCast.
     */
    private IMap<String, CircuitState> getMap() {
        final IMap<String, CircuitState> ret = hazelcastInstance.getMap(HAZELCAST_KEY);
        Assert.notNull(ret, "the map was not found in hazelcast");
        return ret;
    }

    /**
     * Get circuitInfo for circuit identified by name.
     */
    protected CircuitState getCircuitState(String circuitName) {
        final IMap<String, CircuitState> map = getMap();

        if (!map.containsKey(circuitName)) {
            // first run for circuit identified by name
            final CircuitState circuitState = new CircuitState();
            map.put(circuitName, circuitState);
        }

        final CircuitState ret = map.get(circuitName);
        Assert.notNull(ret, "the circuitState domain object must not be null");
        return ret;
    }

    @Override
    protected void onSuccessCall(String circuitName) {
        Assert.hasText(circuitName, "the circuitName must not be empty");

        getMap().executeOnKey(circuitName, SUCCESS_CALL_ENTRY_PROCESSOR);
    }

    @Override
    protected void onCircuitSwitchDown(String circuitName) {
        Assert.hasText(circuitName, "the circuitName must not be empty");

        getMap().executeOnKey(circuitName, CIRCUIT_DOWN_ENTRY_PROCESSOR);
    }

    @Override
    protected void onFailedCall(String circuitName) {
        Assert.hasText(circuitName, "the circuitName must not be empty");

        getMap().executeOnKey(circuitName, FAILED_CALL_ENTRY_PROCESSOR);
    }

    @Override
    protected void removeOldEntries(CircuitConfiguration circuitConfiguration) {
        Assert.notNull(circuitConfiguration, "the circuitConfiguration must not be null");

        getMap().executeOnKey(
                circuitConfiguration.getCircuitName(),
                new RemoveOldCallsEntryProcessor(circuitConfiguration));
    }
}
