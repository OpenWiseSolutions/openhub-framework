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

package org.openhubframework.openhub.core.common.asynch.confirm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Starts new job for pooling confirmations from the queue.
 * <p/>
 * If previous job has still been running then skips this execution and try it next time.
 *
 * @author Petr Juza
 */
@Service
public class JobStarterForConfirmationPooling {

    private static final Logger LOG = LoggerFactory.getLogger(JobStarterForConfirmationPooling.class);

    private Boolean isRunning = Boolean.FALSE;

    private final Object lock = new Object();

    @Autowired
    private ConfirmationPollExecutor pollExecutor;

    public void start() throws Exception {
        synchronized (lock) {
            if (isRunning) {
                LOG.debug("Job hasn't been started because previous job has still been running.");
                return;
            }

            isRunning = Boolean.TRUE;
        }

        try {
            pollExecutor.run();
        } catch (Exception ex) {
            LOG.error("Error occurred during polling confirmations.", ex);
        } finally {
            isRunning = Boolean.FALSE;
        }
    }
}
