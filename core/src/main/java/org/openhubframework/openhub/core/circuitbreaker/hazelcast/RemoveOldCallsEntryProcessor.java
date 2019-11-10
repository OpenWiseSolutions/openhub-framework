package org.openhubframework.openhub.core.circuitbreaker.hazelcast;

import java.time.Instant;
import java.util.Map;

import com.hazelcast.map.AbstractEntryProcessor;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.openhubframework.openhub.core.circuitbreaker.CircuitState;

/**
 * Remove all "old" calls from cache.
 *
 * All calls out of the scope of circuit sliding window, are not useful anymore, so clean them.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class RemoveOldCallsEntryProcessor extends AbstractEntryProcessor<String, CircuitState> {

    private CircuitConfiguration configuration;

    public RemoveOldCallsEntryProcessor(CircuitConfiguration circuitConfiguration) {
        this.configuration = circuitConfiguration;
    }

    @Override
    public Object process(Map.Entry<String, CircuitState> entry) {
        final CircuitState circuitState = entry.getValue();
        circuitState.getSuccessCallList()
                .removeIf(it -> it < Instant.now().toEpochMilli() - configuration.getWindowSizeInMillis());
        circuitState.getFailedCallList()
                .removeIf(it -> it < Instant.now().toEpochMilli() - configuration.getWindowSizeInMillis());

        entry.setValue(circuitState);
        return null;
    }
}
