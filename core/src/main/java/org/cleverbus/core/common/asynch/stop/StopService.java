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

package org.cleverbus.core.common.asynch.stop;

/**
 * Contract for managing ESB stopping.
 * <p/>
 * It depends on service implementation if stopping is valid for one node or whole cluster.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public interface StopService {

    /**
     * Is ESB stopping?
     *
     * @return {@code true} if ESB is in "stopping mode" otherwise {@code false}
     */
    boolean isStopping();


    /**
     * Stop ESB, switches to stopping mode. Can be called repeatedly.
     * <p/>
     * ESB won't to process next asynchronous messages.
     */
    void stop();


    /**
     * Cancels ESB stopping, switches back to normal mode.
     * Can be called repeatedly.
     */
    void cancelStopping();

}
