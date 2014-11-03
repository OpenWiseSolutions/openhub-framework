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

package org.cleverbus.core.common.asynch.confirm;

import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Starts new job for pooling confirmations from the queue.
 * <p/>
 * If previous job has still been running then skips this execution and try it next time.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class JobStarterForConfirmationPooling {

    private Boolean isRunning = Boolean.FALSE;

    private final Object lock = new Object();

    @Autowired
    private ConfirmationPollExecutor pollExecutor;

    public void start() throws Exception {
        synchronized (lock) {
            if (isRunning) {
                Log.debug("Job hasn't been started because previous job has still been running.");
                return;
            }

            isRunning = Boolean.TRUE;
        }

        try {
            pollExecutor.run();
        } catch (Exception ex) {
            Log.error("Error occurred during polling confirmations.", ex);
        } finally {
            isRunning = Boolean.FALSE;
        }
    }
}
