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

package org.openhubframework.openhub.api.event;

import org.apache.camel.Exchange;
import org.apache.camel.management.event.AbstractExchangeEvent;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openhubframework.openhub.api.entity.Message;
import org.springframework.util.Assert;


/**
 * Base class for asynchronous {@link Message message} events.
 *
 * @author Petr Juza
 */
public abstract class AbstractAsynchEvent extends AbstractExchangeEvent {

    private final Message message;

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message the message
     */
    public AbstractAsynchEvent(Exchange exchange, Message message) {
        super(exchange);

        Assert.notNull(message, "message must not be null");

        this.message = message;
    }

    /**
     * Gets asynchronous message.
     *
     * @return message
     */
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("exchange", getExchange())
            .append("message", getMessage())
            .toString();
    }
}
