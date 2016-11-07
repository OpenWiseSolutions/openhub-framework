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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.spi.throttling.ThrottleCounter;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * In-memory implementation of {@link ThrottleCounter} interface.
 * <p/>
 * Fast and enough-solution for one server solution but it's not sufficient for cluster environment.
 *
 * @author Petr Juza
 */
public class ThrottleCounterMemoryImpl implements ThrottleCounter {

    private static final int DUMP_PERIOD = 60;

    /**
     * List of timestamps of incoming requests for specified interval per throttling scope.
     */
    private Map<ThrottleScope, List<Long>> requests = new ConcurrentHashMap<ThrottleScope, List<Long>>();

    private volatile Date lastDumpTimestamp = new Date();

    private final ReentrantLock lock = new ReentrantLock();

    private final Set<ThrottleScope> scopesInProgress = new HashSet<ThrottleScope>();

    private static final Object OBJ_LOCK = new Object();

    @Override
    public int count(ThrottleScope throttleScope, int interval) {
        Assert.notNull(throttleScope, "the throttleScope must not be null");
        Assert.isTrue(interval > 0, "the interval must be positive value");

        int counter = 0;
        boolean toLock = false;

        // is it necessary to lock thread? Only if two same throttle scopes are processed at the same time
        synchronized (OBJ_LOCK) {
            if (scopesInProgress.contains(throttleScope)) {
                toLock = true;
            } else {
                scopesInProgress.add(throttleScope);
            }
        }

        if (toLock) {
            lock.lock();
        }

        try {
            if (requests.get(throttleScope) == null) {
                requests.put(throttleScope, new Stack<Long>());
            }

            long now = DateTime.now().getMillis();
            long from = now - (interval * 1000);

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
        } finally {
            synchronized (OBJ_LOCK) {
                scopesInProgress.remove(throttleScope);
            }

            if (toLock) {
                lock.unlock();
            }
        }


        // make dump only once in the specified interval
        if (Log.isDebugEnabled() && (DateUtils.addSeconds(new Date(), -DUMP_PERIOD).after(lastDumpTimestamp))) {
            dumpMemory();

            lastDumpTimestamp = new Date();
        }

        return counter;
    }

    /**
     * Dumps throttling memory to log on "debug" level.
     */
    void dumpMemory() {
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

        Log.debug(dump.toString());
    }
}
