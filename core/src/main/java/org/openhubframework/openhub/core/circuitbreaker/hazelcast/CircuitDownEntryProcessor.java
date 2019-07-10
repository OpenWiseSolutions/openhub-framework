package org.openhubframework.openhub.core.circuitbreaker.hazelcast;

import java.time.Instant;
import java.util.Map;

import com.hazelcast.map.AbstractEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.CircuitState;

/**
 * Processor to be executed, when circuit should be set to down.
 *
 * @author Karel Kovarik
 * @see com.hazelcast.map.EntryProcessor
 * @since 2.2
 */
public class CircuitDownEntryProcessor extends AbstractEntryProcessor<String, CircuitState> {

    @Override
    public Object process(Map.Entry<String, CircuitState> entry) {
        final CircuitState circuitState = entry.getValue();
        // reset counters
        circuitState.resetFailedCallList();
        circuitState.resetSuccessCallList();
        // set lastShortcutTimestamp
        circuitState.setLastShortcutTimestamp(Instant.now().toEpochMilli());

        entry.setValue(circuitState);
        return null;
    }

}
