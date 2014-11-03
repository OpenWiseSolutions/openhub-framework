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

package org.cleverbus.component.funnel;

import java.util.Map;

import org.cleverbus.api.entity.Message;
import org.cleverbus.spi.AsyncEventNotifier;
import org.cleverbus.spi.msg.MessageService;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Apache Camel component "msg-funnel" for filtering processing messages.
 * This component ensures that there is only one processing message with same
 * {@link Message#getFunnelValue() funnel value}.
 *
 * <p/>
 * Syntax: {@code msg-funnel:default[?options]} where options are
 * <ul>
 *     <li>idleInterval - Interval (in seconds) that determines how long can be message processing.
 *     <li>guaranteedOrder - if funnel component should guaranteed order of processing messages
 *     <li>excludeFailedState - if FAILED state should be involved in guaranteed order
 *     <li>id - funnel component identifier
 * </ul>
 *
 * <p/>
 * Valid for asynchronous messages only.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MsgFunnelComponent extends DefaultComponent {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AsyncEventNotifier asyncEventNotifier;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        MsgFunnelEndpoint endpoint = new MsgFunnelEndpoint(uri, this);

        return endpoint;
    }

    MessageService getMessageService() {
        return messageService;
    }

    AsyncEventNotifier getAsyncEventNotifier() {
        return asyncEventNotifier;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // checking references
        Assert.notNull(messageService, "messageService mustn't be null");
        Assert.notNull(asyncEventNotifier, "asyncEventNotifier mustn't be null");
    }
}
