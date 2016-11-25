/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.throttling;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import org.openhubframework.openhub.common.synchronization.SynchronizationBlock;
import org.openhubframework.openhub.common.synchronization.SynchronizationExecutor;
import org.openhubframework.openhub.spi.throttling.ThrottleCounter;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * In-memory implementation of {@link ThrottleCounter} interface.
 * <p>
 * Fast and enough-solution for one server solution but it's not sufficient for cluster environment.
 *
 * @author Petr Juza
 */
public class ThrottleCounterMemoryImpl extends AbstractThrottleCounter {

    private static final String SYNC_THROTTLING_TYPE = "SYNCHRONIZATION_THROTTLING";

    /**
     * List of timestamps of incoming requests for specified interval per throttling scope.
     */
    private Map<ThrottleScope, List<Long>> requests = new ConcurrentHashMap<>();

    @Override
    protected int doCount(final ThrottleScope throttleScope, final int intervalSec) {
        return SynchronizationExecutor.getInstance().execute(new SynchronizationBlock() {

            @Override
            @SuppressWarnings("unchecked")
            public <T> T syncBlock() {
                Integer counter = 0;

                requests.computeIfAbsent(throttleScope, k -> new Stack<Long>());

                long now = Instant.now().toEpochMilli();
                long from = now - (intervalSec * 1000);

                // get timestamps for specified throttling scope
                List<Long> timestamps = requests.get(throttleScope);
                timestamps.add(now);

                // count requests for specified interval
                int lastIndex = -1;
                for (int i = timestamps.size() - 1; i >= 0; i--) {
                    long timestamp = timestamps.get(i);

                    if (timestamp >= from) {
                        counter++;
                    } else {
                        lastIndex = i;
                        break;
                    }
                }

                // remove old timestamps
                if (lastIndex > 0) {
                    for (int i = 0; i <= lastIndex; i++) {
                        timestamps.remove(0);
                    }
                }
                return (T) counter;
            }
        }, SYNC_THROTTLING_TYPE, throttleScope);
    }

    @Override
    @Nullable
    String getCacheInfo() {
        StringBuilder dump = new StringBuilder();
        dump.append("Throttling in-memory dump:\n");

        for (Map.Entry<ThrottleScope, List<Long>> en : requests.entrySet()) {
            dump.append("sourceSystem=");
            dump.append(en.getKey().getSourceSystem());
            dump.append(", serviceName=");
            dump.append(en.getKey().getServiceName());
            dump.append(": ");
            dump.append(en.getValue().size());
            dump.append("\n");
        }

        return dump.toString();
    }
}
