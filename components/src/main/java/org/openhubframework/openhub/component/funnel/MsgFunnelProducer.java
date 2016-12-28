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

package org.openhubframework.openhub.component.funnel;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;


/**
 * Producer for {@link MsgFunnelComponent msg-funnel} component.
 *
 * @author Petr Juza
 */
public class MsgFunnelProducer extends DefaultProducer {

    private static final String FUNNEL_COMP_PREFIX = "funnel_";

    private static final Logger LOG = LoggerFactory.getLogger(MsgFunnelProducer.class);

    /**
     * Creates new producer.
     *
     * @param endpoint the endpoint
     */
    public MsgFunnelProducer(MsgFunnelEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);

        Assert.notNull(msg, "message must be defined, msg-funnel component is for asynchronous messages only");

        if (StringUtils.isEmpty(msg.getFunnelValue())) {
            LOG.debug("Message " + msg.toHumanString() + " doesn't have funnel value => won't be filtered");
        } else {
            MsgFunnelEndpoint endpoint = (MsgFunnelEndpoint) getEndpoint();

            // set ID to message
            String funnelCompId = getFunnelCompId(exchange, endpoint);
            if (!funnelCompId.equals(msg.getFunnelComponentId())) {
                endpoint.getMessageService().setFunnelComponentId(msg, funnelCompId);
            }

            if (endpoint.isGuaranteedOrder()) {
                // By default classic funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
                // and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED [and FAILED]
                // messages should be involved
                List<Message> messages = endpoint.getMessageService().getMessagesForGuaranteedOrderForFunnel(
                        msg.getFunnelValue(), endpoint.getIdleInterval(), endpoint.isExcludeFailedState(),
                        funnelCompId);

                if (messages.size() == 1) {
                    LOG.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                            + " => no filtering");

                // is specified message first one for processing?
                } else if (messages.get(0).equals(msg)) {
                    LOG.debug("Processing message (msg_id = {}, funnel value = '{}') is the first one"
                            + " => no filtering", msg.getMsgId(), msg.getFunnelValue());

                } else {
                    LOG.debug("There is at least one processing message with funnel value '{}'"
                                    + " before current message (msg_id = {}); message {} will be postponed.",
                            msg.getFunnelValue(), msg.getMsgId(), msg.toHumanString());

                    postponeMessage(exchange, msg, endpoint);
                }

            } else {
                // is there processing message with same funnel value?
                int count = endpoint.getMessageService().getCountProcessingMessagesForFunnel(msg.getFunnelValue(),
                        endpoint.getIdleInterval(), funnelCompId);

                if (count > 1) {
                    // note: one processing message is this message
                    LOG.debug("There are more processing messages with funnel value '" + msg.getFunnelValue()
                            + "', message " + msg.toHumanString() + " will be postponed.");

                    postponeMessage(exchange, msg, endpoint);

                } else {
                    LOG.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                            + " => no filtering");
                }
            }
        }
    }

    private String getFunnelCompId(Exchange exchange, MsgFunnelEndpoint endpoint) {
        if (endpoint.getId() != null) {
            // use custom ID
            return endpoint.getId();
        } else {
            // create ID from route ID
            return FUNNEL_COMP_PREFIX + exchange.getFromRouteId();
        }
    }

    private void postponeMessage(Exchange exchange, Message msg, MsgFunnelEndpoint endpoint) {
        // change state
        endpoint.getMessageService().setStatePostponed(msg);

        // generates event
        endpoint.getAsyncEventNotifier().notifyMsgPostponed(exchange);

        // set StopProcessor - mark the exchange to stop continue routing
        exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }
}
