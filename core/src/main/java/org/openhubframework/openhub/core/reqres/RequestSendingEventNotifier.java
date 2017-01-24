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

package org.openhubframework.openhub.core.reqres;

import static org.openhubframework.openhub.api.configuration.CoreProps.REQUEST_SAVING_ENABLE;
import static org.openhubframework.openhub.api.configuration.CoreProps.REQUEST_SAVING_ENDPOINT_FILTER;

import java.util.EventObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.management.event.ExchangeSendingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.api.event.EventNotifier;
import org.openhubframework.openhub.api.event.EventNotifierBase;


/**
 * Listens to request sending (event = {@link ExchangeSendingEvent}).
 * If endpoint URI is successfully filtered and saving is enabled then request is saved to DB.
 *
 * @author Petr Juza
 * @see ResponseReceiveEventNotifier
 * @since 0.4
 */
@EventNotifier
public class RequestSendingEventNotifier extends EventNotifierBase<ExchangeSendingEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestSendingEventNotifier.class);

    /**
     * Header name for saving request for next use.
     */
    static final String SAVE_REQ_HEADER = "SAVE_REQ_HEADER";

    /**
     * True for enabling saving requests/responses for filtered endpoints URI.
     */
    @ConfigurableValue(key = REQUEST_SAVING_ENABLE)
    private ConfigurationItem<Boolean> enable;

    /**
     * Pattern for filtering endpoints URI which requests/response should be saved.
     */
    @ConfigurableValue(key = REQUEST_SAVING_ENDPOINT_FILTER)
    private ConfigurationItem<Pattern> endpointFilterPattern;

    @Autowired
    private RequestResponseService requestResponseService;

    @Override
    public boolean isEnabled(EventObject event) {
        return enable.getValue() && super.isEnabled(event);
    }

    @Override
    protected void doNotify(ExchangeSendingEvent event) throws Exception {
        String endpointUri = event.getEndpoint().getEndpointUri();

        if (filter(endpointUri, endpointFilterPattern.getValue())) {
            Message msg = event.getExchange().getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);

            // create request
            String reqBody = ((Exchange) event.getSource()).getIn().getBody(String.class);
            String joinId = createResponseJoinId(event.getExchange());

            Request req = Request.createRequest(endpointUri, joinId, reqBody, msg);

            try {
                // save request
                requestResponseService.insertRequest(req);

                // add to exchange for later use when response arrives
                event.getExchange().getIn().setHeader(SAVE_REQ_HEADER, req);
            } catch (Exception ex) {
                LOG.error("Request didn't saved.", ex);
            }
        }
    }

    /**
     * Returns {@code true} if specified URI matches specified pattern.
     *
     * @param endpointURI the endpoint URI
     * @param pattern pattern
     * @return {@code true} if specified URI matches at least one of specified patterns otherwise {@code false}
     */
    static boolean filter(String endpointURI, Pattern pattern) {
        Assert.hasText(endpointURI, "the endpointURI must be defined");

        Matcher matcher = pattern.matcher(endpointURI);
        return matcher.matches();
    }

    /**
     * Creates response join ID.
     * <p/>
     * Request is uniquely identified by its URI and response join ID.
     * Both these attributes helps to join right request and response together.
     *
     * @param exchange the exchange
     * @return join ID
     */
    static String createResponseJoinId(Exchange exchange) {
        // exchange ID is unique but can be changed when the whole exchange changes
        //  - "the wire tap creates a copy of the incoming message, and the copied message will use a new exchange ID
        //  because it’s being routed as a separate process."
        String joinId = exchange.getExchangeId();

        Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
        if (msg != null) {
            // if it's asynchronous message then join ID is correlation ID
            joinId = msg.getCorrelationId();
        }

        return joinId;
    }
}
