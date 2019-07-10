package org.openhubframework.openhub.core.circuitbreaker.hazelcast;

import java.time.Instant;
import java.util.Map;

import com.hazelcast.map.AbstractEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.CircuitState;

/**
 * EntryProcessor for failed call.
 *
 * @author Karel Kovarik
 * @see com.hazelcast.map.EntryProcessor
 * @since 2.2
 */
public class FailedCallEntryProcessor extends AbstractEntryProcessor<String, CircuitState> {

    @Override
    public Object process(Map.Entry<String, CircuitState> entry) {
        final CircuitState circuitState = entry.getValue();
        // insert failed event
        circuitState.getFailedCallList().add(Instant.now().toEpochMilli());
        entry.setValue(circuitState);

        return null;
    }
}
