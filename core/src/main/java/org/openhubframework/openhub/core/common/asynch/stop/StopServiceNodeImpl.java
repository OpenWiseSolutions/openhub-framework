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

package org.openhubframework.openhub.core.common.asynch.stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Implementation of {@link StopService} for one ESB node/instance.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Service
public class StopServiceNodeImpl implements StopService {

    private static final Logger LOG = LoggerFactory.getLogger(StopServiceNodeImpl.class);

    private boolean stopping = false;

    @Override
    public boolean isStopping() {
        return stopping;
    }

    @Override
    public synchronized void stop() {
        this.stopping = true;

        LOG.info("ESB starts stopping ...");
    }

    @Override
    public synchronized void cancelStopping() {
        this.stopping = false;

        LOG.info("ESB stopping was canceled.");
    }
}
