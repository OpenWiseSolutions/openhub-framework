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

package org.openhubframework.openhub.core.common.event;

import org.openhubframework.openhub.api.event.CompletedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.FailedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.PartlyFailedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.PostponedMsgAsynchEvent;
import org.openhubframework.openhub.api.event.ProcessingMsgAsynchEvent;
import org.openhubframework.openhub.api.event.WaitingMsgAsynchEvent;

import org.apache.camel.Exchange;


/**
 * Factory to create {@link java.util.EventObject events} that are emitted when such an event occur.
 *
 * @author Petr Juza
 */
public interface AsynchEventFactory {

    /**
     * Creates an {@link CompletedMsgAsynchEvent} for successfully completed asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    CompletedMsgAsynchEvent createCompletedMsgEvent(Exchange exchange);

    /**
     * Creates an {@link PartlyFailedMsgAsynchEvent} for partly failed asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    PartlyFailedMsgAsynchEvent createPartlyFailedMsgEvent(Exchange exchange);

    /**
     * Creates an {@link FailedMsgAsynchEvent} for failed asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    FailedMsgAsynchEvent createFailedMsgEvent(Exchange exchange);

    /**
     * Creates an {@link WaitingMsgAsynchEvent} for waiting asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    WaitingMsgAsynchEvent createWaitingMsgEvent(Exchange exchange);

    /**
     * Creates an {@link ProcessingMsgAsynchEvent} for processing asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    ProcessingMsgAsynchEvent createProcessingMsgEvent(Exchange exchange);

    /**
     * Creates an {@link PostponedMsgAsynchEvent} for postponed asynch message.
     *
     * @param exchange the exchange
     * @return the created event
     */
    PostponedMsgAsynchEvent createPostponedMsgEvent(Exchange exchange);

}
