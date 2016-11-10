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

import org.openhubframework.openhub.api.event.AbstractAsynchEvent;

import org.apache.camel.Exchange;
import org.apache.camel.spi.EventNotifier;


/**
 * Callback contract for creating new events.
 *
 * @author Petr Juza
 */
public interface EventNotifierCallback {

    /**
     * Ignore specified notifier?
     *
     * @param notifier the event notifier
     * @return {@code true} for ignoring otherwise {@code false}
     */
    boolean ignore(EventNotifier notifier);


    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @return new event
     */
    AbstractAsynchEvent createEvent(Exchange exchange);

}
