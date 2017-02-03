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

package org.openhubframework.openhub.core.common.asynch.queue;

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_POSTPONED_INTERVAL_WHEN_FAILED_SEC;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.core.common.asynch.LogContextHelper;
import org.openhubframework.openhub.core.common.event.AsynchEventHelper;
import org.openhubframework.openhub.spi.msg.MessageService;


/**
 * Reads messages from DB and sends them for next processing.
 * Execution will stop when there is no further message for processing.
 * <p/>
 * This executor is invoked by {@link JobStarterForMessagePooling}.
 *
 * @author Petr Juza
 */
@Service
public class MessagePollExecutor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePollExecutor.class);

    private static final int LOCK_FAILURE_LIMIT = 5;

    @Autowired
    private MessagesPool messagesPool;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private MessageService messageService;

    /**
     * Interval (in seconds) after that postponed messages will fail.
     */
    @ConfigurableValue(key = ASYNCH_POSTPONED_INTERVAL_WHEN_FAILED_SEC)
    private ConfigurationItem<Seconds> postponedIntervalWhenFailed;

    // note: this is because of setting different target URI for tests
    private String targetURI = AsynchMessageRoute.URI_ASYNC_PROCESSING_MSG;

    @Override
    public void run() {
        LOG.debug("Message pooling starts ...");

        // is there message for processing?
        Message msg = null;
        int lockFailureCount = 0;
        while (true) {
            try {
                msg = messagesPool.getNextMessage();

                if (msg != null) {
                    LogContextHelper.setLogContextParams(msg, null);

                    startMessageProcessing(msg);
                } else {
                    //there is no new message for processing
                    //  => finish this executor and try it again after some time
                    break;
                }
            } catch (LockFailureException ex) {
                // try again to acquire next message with lock
                lockFailureCount++;

                if (lockFailureCount > LOCK_FAILURE_LIMIT) {
                    LOG.warn("Probably problem with locking messages - count of lock failures exceeds limit ("
                            + LOCK_FAILURE_LIMIT + ").");
                    break;
                }
            } catch (Exception ex) {
                LOG.error("Error occurred during getting message "
                        + (msg != null ? msg.toHumanString() : ""), ex);
            }
        }

        LOG.debug("Message pooling finished.");
    }

    void startMessageProcessing(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        if (isMsgInGuaranteedOrder(msg)) {
            // sends message for next processing
            producerTemplate.sendBodyAndHeader(targetURI, ExchangePattern.InOnly, msg,
                    AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());

        } else {
            LocalDateTime failedDate = LocalDateTime.now().minus(postponedIntervalWhenFailed.getValue());

            final Message paramMsg = msg;

            if (msg.getReceiveTimestamp().before(failedDate.toDate())) {

                // change to failed message => redirect to "FAILED" route
                producerTemplate.send(AsynchConstants.URI_ERROR_FATAL, ExchangePattern.InOnly,
                        new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                IntegrationException ex = new IntegrationException(InternalErrorEnum.E121,
                                        "Message " + paramMsg.toHumanString() + " exceeded interval for starting "
                                                + "processing => changed to FAILED state");

                                exchange.setProperty(Exchange.EXCEPTION_CAUGHT, ex);

                                exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, paramMsg);
                            }
                        });

            } else {
                // postpone message
                messageService.setStatePostponed(msg);

                // create Exchange for event only
                ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(producerTemplate.getCamelContext());
                Exchange exchange = exchangeBuilder.build();

                exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, paramMsg);

                AsynchEventHelper.notifyMsgPostponed(exchange);
            }
        }
    }

    /**
     * Checks if specified message should be processed in guaranteed order and if yes
     * then checks if the message is in the right order.
     *
     * @param msg the asynchronous message
     * @return {@code true} if message's order is ok otherwise {@code false}
     */
    private boolean isMsgInGuaranteedOrder(Message msg) {
        if (!msg.isGuaranteedOrder()) {
            // no guaranteed order => continue
            return true;
        } else {
            // guaranteed order => is the message in the right order?
            List<Message> messages = messageService.getMessagesForGuaranteedOrderForRoute(msg.getFunnelValue(),
                    msg.isExcludeFailedState());

            if (messages.size() == 1) {
                LOG.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                        + " => continue");

                return true;

            // is specified message first one for processing?
            } else if (messages.get(0).equals(msg)) {
                LOG.debug("Processing message (msg_id = {}, funnel value = '{}') is the first one"
                        + " => continue", msg.getMsgId(), msg.getFunnelValue());

                return true;

            } else {
                LOG.debug("There is at least one processing message with funnel value '{}'"
                                + " before current message (msg_id = {}); message {} will be postponed.",
                        msg.getFunnelValue(), msg.getMsgId(), msg.toHumanString());

                return false;
            }
        }
    }
}
