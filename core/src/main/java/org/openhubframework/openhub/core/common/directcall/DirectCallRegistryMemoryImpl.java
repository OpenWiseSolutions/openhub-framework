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

package org.openhubframework.openhub.core.common.directcall;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openhubframework.openhub.common.log.Log;

import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Memory implementation of {@link DirectCallRegistry} interface.
 *
 * @author Petr Juza
 */
public class DirectCallRegistryMemoryImpl implements DirectCallRegistry {

    private static final int OLD_PARAMS_INTERVAL = 60 * 1000;

    private Map<String, DirectCallParams> registry = new ConcurrentHashMap<String, DirectCallParams>();

    @Override
    public void addParams(String callId, DirectCallParams params) {
        Assert.hasText(callId, "the callId must not be empty");
        Assert.notNull(params, "the params must not be null");

        if (registry.get(callId) != null) {
            throw new IllegalStateException("there are already call params with call ID = " + callId);
        }

        registry.put(callId, params);

        Log.debug("Call params with callId=" + callId + " added to registry: " + params);

        removeOldParams();
    }

    @Override
    public DirectCallParams getParams(String callId) {
        DirectCallParams params = registry.get(callId);

        if (params == null) {
            throw new IllegalStateException("there are no parameters for callId = '" + callId + "' ");
        }

        return params;
    }

    @Override
    public void removeParams(String callId) {
        if (registry.remove(callId) != null) {
            Log.debug("Call params with callId=" + callId + " were removed from registry");
        }
    }

    /**
     * Removes old params.
     */
    private void removeOldParams() {
        DateTime threshold = DateTime.now().minus(OLD_PARAMS_INTERVAL);

        Set<String> removes = new HashSet<String>();

        // find old params
        for (Map.Entry<String, DirectCallParams> en : registry.entrySet()) {
            if (en.getValue().getCreationTimestamp().isBefore(threshold)) {
                removes.add(en.getKey());
            }
        }

        // remove old params
        for (String callId : removes) {
            removeParams(callId);
        }
    }
}
