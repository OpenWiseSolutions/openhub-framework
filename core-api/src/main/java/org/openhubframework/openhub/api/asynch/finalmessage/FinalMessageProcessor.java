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

import org.openhubframework.openhub.api.entity.Message;
import org.springframework.core.Ordered;

/**
 * Contract for final messages processor.
 * Messages that are no-longer considered relevant for openhub async workflow are expected
 * to be processed by this processor.
 *
 * There can be multiple processors, that are invoked in order defined in getOrder method.
 *
 * All processor are expected to be executed in transaction, if any should throw
 * an exception, transaction will be rolled back.
 *
 * @author Karel Kovarik
 * @since 2.1
 */
@FunctionalInterface
public interface FinalMessageProcessor extends Ordered {

    /**
     * Default order, if {@link FinalMessageProcessor#getOrder()} method is not implemented.
     */
    int DEFAULT_ORDER = 0;

    /**
     * Process message in OpenHub datastore.
     *
     * @param message the message to be processed.
     */
    void processMessage(Message message);

    /**
     * Get the order for processor.
     *
     * @return the order of processor, lower value is expected to be invoked first.
     */
    default int getOrder() {
        return DEFAULT_ORDER;
    }
}
