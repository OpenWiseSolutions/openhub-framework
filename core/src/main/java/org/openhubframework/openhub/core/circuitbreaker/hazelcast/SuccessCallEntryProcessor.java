package org.openhubframework.openhub.core.circuitbreaker.hazelcast;

import java.time.Instant;
import java.util.Map;

import com.hazelcast.map.AbstractEntryProcessor;
import org.openhubframework.openhub.core.circuitbreaker.CircuitState;

/**
 * EntryProcessor to insert new successful call to the entry.
 *
 * @author Karel Kovarik
 * @see com.hazelcast.map.EntryProcessor
 * @since 2.2
 */
public class SuccessCallEntryProcessor extends AbstractEntryProcessor<String, CircuitState> {

    @Override
    public Object process(Map.Entry<String, CircuitState> entry) {
        final CircuitState circuitState = entry.getValue();
        // insert success event
        circuitState.getSuccessCallList().add(Instant.now().toEpochMilli());
        entry.setValue(circuitState);

        return null;
    }
}
