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

import org.openhubframework.openhub.spi.AsyncEventNotifier;

import org.apache.camel.Exchange;


/**
 * Implementation of {@link AsyncEventNotifier}.
 *
 * @author Petr Juza
 */
public class AsyncEventNotifierImpl implements AsyncEventNotifier {

    @Override
    public void notifyMsgCompleted(Exchange exchange) {
        AsynchEventHelper.notifyMsgCompleted(exchange);
    }

    @Override
    public void notifyMsgPartlyFailed(Exchange exchange) {
        AsynchEventHelper.notifyMsgPartlyFailed(exchange);
    }

    @Override
    public void notifyMsgFailed(Exchange exchange) {
        AsynchEventHelper.notifyMsgFailed(exchange);
    }

    @Override
    public void notifyMsgWaiting(Exchange exchange) {
        AsynchEventHelper.notifyMsgWaiting(exchange);
    }

    @Override
    public void notifyMsgProcessing(Exchange exchange) {
        AsynchEventHelper.notifyMsgProcessing(exchange);
    }

    @Override
    public void notifyMsgPostponed(Exchange exchange) {
        AsynchEventHelper.notifyMsgPostponed(exchange);
    }
}
