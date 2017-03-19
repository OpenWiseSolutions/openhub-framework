/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import javax.annotation.Nullable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.map.EntryProcessor;
import org.springframework.util.Assert;

import org.openhubframework.openhub.core.config.CacheNames;
import org.openhubframework.openhub.spi.throttling.ThrottleCounter;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Shared memory (aka memory grid or cache) implementation of {@link ThrottleCounter} interface
 * by Hazelcast' {@link IMap map} and {@link EntryProcessor}.
 * Suitable for running OpenHub in the cluster environment.
 * <p/>
 * Implementation prerequisites Hazelcast's map definition with the name '{@value #MAP_THROTTLING}'.
 *
 * @author Petr Juza
 * @since 2.0
 * @see HazelcastThrottleScope
 */
public class ThrottleCounterHazelcastImpl extends AbstractThrottleCounter {

    private final HazelcastInstance hazelcast;

    public ThrottleCounterHazelcastImpl(HazelcastInstance hazelcast) {
        Assert.notNull(hazelcast, "hazelcast must not be null");

        this.hazelcast = hazelcast;
    }

    @Override
    protected int doCount(ThrottleScope throttleScope, int intervalSec) {
        IMap<HazelcastThrottleScope, List<Long>> map = hazelcast.getMap(CacheNames.THROTTLING);

        Assert.notNull(map, "shared map must not be null");

        HazelcastThrottleScope sharedThrottleScope = new HazelcastThrottleScope(throttleScope);
        return (Integer) map.executeOnKey(sharedThrottleScope, new CounterEntryProcessor(intervalSec));
    }

    @Override
    @Nullable
    String getCacheInfo() {
        IMap<Object, Object> map = hazelcast.getMap(CacheNames.THROTTLING);
        return "Throttling Hazelcast statistics dump:\n" + map.getLocalMapStats().toString();
    }

    /**
     * {@link EntryProcessor} for specific map entry.
     */
    private static class CounterEntryProcessor extends AbstractEntryProcessor<HazelcastThrottleScope, List<Long>> {

        private int intervalSec;

        /**
         * Creates {@link EntryProcessor} for specific map entry.
         *
         * @param intervalSec the time interval in seconds
         */
        CounterEntryProcessor(int intervalSec) {
            // backups are disabled for this shared map
            super(false);

            this.intervalSec = intervalSec;
        }

        @Override
        public Object process(Map.Entry<HazelcastThrottleScope, List<Long>> entry) {
            Integer counter = 0;

            List<Long> timestamps;

            if (entry.getValue() == null) {
                timestamps = new Stack<>();
            } else {
                timestamps = entry.getValue();
            }

            long now = Instant.now().toEpochMilli();
            long from = now - (intervalSec * 1000);

            // get timestamps for specified throttling scope
            timestamps.add(now);

            // count requests for specified intervalSec
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

            entry.setValue(timestamps);

            return counter;
        }
    }
}
