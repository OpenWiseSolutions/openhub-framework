/*
 * Copyright 2017 the original author or authors.
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

package org.openhubframework.openhub.common.synchronization;

/**
 * Method {@link #syncBlock()} in this interface can be synchronized by one value in
 * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
 * <p>
 * If you need {@code void} method, use {@link SynchronizationNoResultBlock}.
 * </p>
 *
 * @author Roman Havlicek
 * @see SynchronizationExecutor#execute(SynchronizationBlock, String, Object)
 * @see SynchronizationNoResultBlock
 * @since 2.0
 */
public interface SynchronizationBlock {

    /**
     * Method will be synchronized by one value calling
     * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
     *
     * @param <T> type of return object
     * @return returned object of calling this method
     */
    <T> T syncBlock();
}
