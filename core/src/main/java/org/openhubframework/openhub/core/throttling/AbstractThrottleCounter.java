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
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.openhubframework.openhub.spi.throttling.ThrottleCounter;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Abstract implementation of {@link ThrottleCounter} interface.
 *
 * @author Petr Juza
 * @since 2.0
 */
public abstract class AbstractThrottleCounter implements ThrottleCounter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractThrottleCounter.class);

    private static final int DUMP_PERIOD_SEC = 60;

    private volatile Instant lastDumpTimestamp = Instant.now();

    @Override
    public final int count(ThrottleScope throttleScope, int intervalSec) {
        Assert.notNull(throttleScope, "the throttleScope must not be null");
        Assert.isTrue(intervalSec > 0, "the intervalSec must be positive value");

        Integer count = doCount(throttleScope, intervalSec);

        // make dump only once in the specified intervalSec
        if (LOG.isDebugEnabled() && (Instant.now().minusSeconds(DUMP_PERIOD_SEC).isAfter(lastDumpTimestamp))) {
            String cacheInfo = getCacheInfo();

            if (StringUtils.isNotEmpty(cacheInfo)) {
                LOG.debug(cacheInfo);
            }

            lastDumpTimestamp = Instant.now();
        }

        return count;
    }

    /**
     * Counts requests for specified throttle scope and time interval.
     *
     * @param throttleScope the throttle scope
     * @param intervalSec the time interval in seconds
     * @return count of requests
     */
    protected abstract int doCount(ThrottleScope throttleScope, int intervalSec);

    /**
     * Returns memory info/statistics for logging.
     *
     * @return cache info or {@code null} if there is no info to log
     */
    @Nullable
    String getCacheInfo() {
        return null;
    }
}
