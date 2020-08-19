/*
 * Copyright 2014-2020 the original author or authors.
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

import java.util.EventObject;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.StatefulService;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.spi.ManagementStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.event.AbstractAsynchEvent;


/**
 * Helper class for easily sending event notifications in a single line of code.
 *
 * @author Petr Juza
 */
public final class AsynchEventHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AsynchEventHelper.class);

    private static final AsynchEventFactory factory = new DefaultAsynchEventFactory();

    private AsynchEventHelper() {
    }

    public static void notifyMsgCompleted(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCompletedEvent();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createCompletedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgPartlyFailed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailedEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createPartlyFailedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgFailed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailedEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createFailedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgWaiting(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createWaitingMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgProcessing(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createProcessingMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgPostponed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createPostponedMsgEvent(exchange);
            }
        });
    }

    public static void notifyGuaranteedOrderMsgCompleted(Exchange exchange){
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createCompletedGuaranteedOrderMsgEvent(exchange);
            }
        });
    }

    /**
     * Notifies event notifiers.
     *
     * @param exchange the exchange
     * @param callback the callback contract for creating new events.
     */
    public static void notifyMsg(Exchange exchange, EventNotifierCallback callback) {
        Assert.notNull(exchange, "the exchange must not be null");

        if (exchange.getProperty(Exchange.NOTIFY_EVENT, false, Boolean.class)) {
            // do not generate events for an notify event
            return;
        }

        CamelContext context = exchange.getContext();

        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return;
        }

        List<EventNotifier> notifiers = management.getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (callback.ignore(notifier)) {
                continue;
            }

            // we want to have new event instance for all notifiers
            EventObject event = callback.createEvent(exchange);

            doNotifyEvent(notifier, event);
        }
    }

    private static void doNotifyEvent(EventNotifier notifier, EventObject event) {
        // only notify if notifier is started
        boolean started = true;
        if (notifier instanceof StatefulService) {
            started = ((StatefulService) notifier).isStarted();
        }

        if (!started) {
            LOG.debug("Ignoring notifying event {}. The EventNotifier has not been started yet: {}", event, notifier);
            return;
        }

        if (!notifier.isEnabled(event)) {
            LOG.debug("Notification of event is disabled: {}", event);
            return;
        }

        try {
            LOG.debug("Event {} arrived to notifier {}", event, notifier.getClass().getName());

            notifier.notify(event);
        } catch (Throwable e) {
            LOG.warn("Error notifying event " + event + ". This exception will be ignored. ", e);
        }
    }
}
