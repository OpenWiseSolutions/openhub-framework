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

package org.cleverbus.spi;

import org.cleverbus.api.event.CompletedMsgAsynchEvent;
import org.cleverbus.api.event.FailedMsgAsynchEvent;
import org.cleverbus.api.event.PartlyFailedMsgAsynchEvent;
import org.cleverbus.api.event.PostponedMsgAsynchEvent;
import org.cleverbus.api.event.ProcessingMsgAsynchEvent;
import org.cleverbus.api.event.WaitingMsgAsynchEvent;

import org.apache.camel.Exchange;


/**
 * Contract for sending event notifications about states of asynchronous message processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface AsyncEventNotifier {

    /**
     * Creates an {@link CompletedMsgAsynchEvent} for successfully completed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgCompleted(Exchange exchange);

    /**
     * Creates an {@link PartlyFailedMsgAsynchEvent} for partly failed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgPartlyFailed(Exchange exchange);

    /**
     * Creates an {@link FailedMsgAsynchEvent} for failed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgFailed(Exchange exchange);

    /**
     * Creates an {@link WaitingMsgAsynchEvent} for waiting asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgWaiting(Exchange exchange);

    /**
     * Creates an {@link ProcessingMsgAsynchEvent} for processing asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgProcessing(Exchange exchange);

    /**
     * Creates an {@link PostponedMsgAsynchEvent} for postponed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgPostponed(Exchange exchange);
}


