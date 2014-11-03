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

package org.cleverbus.api.event;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Exchange;
import org.springframework.util.Assert;


/**
 * Event for successfully completed message, i.e. the message is in {@link MsgStateEnum#OK} state.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class CompletedMsgAsynchEvent extends AbstractAsynchEvent {

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message  the message
     */
    public CompletedMsgAsynchEvent(Exchange exchange, Message message) {
        super(exchange, message);

        Assert.isTrue(message.getState() == MsgStateEnum.OK, "the message must be in the state " + MsgStateEnum.OK);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": the message " + getMessage().toHumanString()
                + " successfully processed message (state = " + MsgStateEnum.OK + ").";
    }
}
