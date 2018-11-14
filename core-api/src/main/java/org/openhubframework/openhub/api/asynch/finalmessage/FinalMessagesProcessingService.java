/*
 * Copyright 2018 the original author or authors.
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

package org.openhubframework.openhub.api.asynch.finalmessage;

/**
 * Service to find final messages that are relevant for processing and
 * invoke all {@link FinalMessageProcessor} beans on them.
 *
 * @author Karel Kovarik
 * @since 2.1
 */
@FunctionalInterface
public interface FinalMessagesProcessingService {

    /**
     * Do trigger messages processing.
     */
    void processMessages();
}
