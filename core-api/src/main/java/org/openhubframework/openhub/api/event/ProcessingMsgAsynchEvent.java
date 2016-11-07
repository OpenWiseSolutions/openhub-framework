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

import org.openhubframework.openhub.api.entity.MsgStateEnum;

import org.apache.camel.Exchange;
import org.openhubframework.openhub.api.entity.Message;
import org.springframework.util.Assert;


/**
 * Event for processing message, i.e. the message is in {@link MsgStateEnum#PROCESSING} state.
 * Event occurs when the message is really being processed (when processing starts).
 *
 * @author Petr Juza
 */
public class ProcessingMsgAsynchEvent extends AbstractAsynchEvent {

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message  the message
     */
    public ProcessingMsgAsynchEvent(Exchange exchange, Message message) {
        super(exchange, message);

        Assert.isTrue(message.getState() == MsgStateEnum.PROCESSING,
                "the message must be in the state " + MsgStateEnum.PROCESSING);
    }


    @Override
    public String toString() {
        return this.getClass().getName() + ": the message " + getMessage().toHumanString()
                + " is being processed (state = " + MsgStateEnum.PROCESSING + ").";
    }
}
