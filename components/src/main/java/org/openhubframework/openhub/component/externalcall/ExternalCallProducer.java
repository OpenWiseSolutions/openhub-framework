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

package org.openhubframework.openhub.component.externalcall;

import static org.apache.commons.lang.BooleanUtils.isNotTrue;
import static org.springframework.util.StringUtils.hasText;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.api.extcall.ExtCallComponentParams;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.spi.extcall.ExternalCallService;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.util.Assert;


/**
 * See {@link ExternalCallComponent}
 */
public class ExternalCallProducer extends DefaultProducer {

    public ExternalCallProducer(ExternalCallEndpoint externalCallEndpoint) {
        super(externalCallEndpoint);
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        Message message = getMessage(exchange);
        String targetURI = getTargetURI(exchange);
        String operation = getOperation(exchange);
        String key = getOperationKey(exchange);
        ExternalCallService service = getService(exchange);

        Log.debug("External call check: operation URI = {}, operation key = {}, msgTimestamp = {}",
                operation, key, message.getMsgTimestamp());

        ExternalCall externalCall = prepareExternalCall(operation, key, message, service);
        if (externalCall != null) {
            try {
                executeExternalCall(exchange, targetURI);
            } finally {
                finalizeExternalCall(exchange, externalCall, service); // in either case release the external call
            }
        } else {
            Log.debug("External call was skipped. See external call service log for detailed info. " +
                    "Call: target={} operation={} key={} msgId={} msgTimestamp={}",
                    targetURI, operation, key, message.getMsgId(), message.getMsgTimestamp());
        }
    }

    protected ExternalCall prepareExternalCall(
            String operation, String key, Message message, ExternalCallService service) {
        try {
            return service.prepare(operation, key, message);
        } catch (LockFailureException exc) {
            throw exc; // don't re-wrap lock failure
        } catch (Exception exc) {
            throw new LockFailureException(String.format(
                    "External call lock failure, please retry. " +
                            "Call: operation=%s key=%s msgId=%s msgTimestamp=%s",
                    operation, key, message.getMsgId(), message.getMsgTimestamp()), exc);
        }
    }

    protected void executeExternalCall(Exchange exchange, String targetURI) {
        try {
            // success should not be determined before the call is made
            exchange.removeProperty(ExtCallComponentParams.EXTERNAL_CALL_SUCCESS);
            // route exchange to external call target
            getEndpoint().getProducerTemplate().send(targetURI, exchange);
        } catch (Exception exc) {
            exchange.setException(exc); // this also marks exchange as failed
        }
    }

    protected void finalizeExternalCall(Exchange exchange, ExternalCall externalCall, ExternalCallService service) {
        Boolean success = exchange.getProperty(ExtCallComponentParams.EXTERNAL_CALL_SUCCESS, Boolean.class);
        if (success == null) {
            // success is not defined forcefully -> determine success normally
            success = !exchange.isFailed()
                    && isNotTrue(exchange.getProperty(Exchange.ROUTE_STOP, Boolean.class));
        }
        if (success) {
            service.complete(externalCall);
        } else {
            service.failed(externalCall);
        }
        // cleanup:
        exchange.removeProperty(ExtCallComponentParams.EXTERNAL_CALL_OPERATION);
        exchange.removeProperty(ExtCallComponentParams.EXTERNAL_CALL_KEY);
        exchange.removeProperty(ExtCallComponentParams.EXTERNAL_CALL_SUCCESS);
    }

    protected Message getMessage(Exchange exchange) {
        Message message = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
        Assert.notNull(message, "Message must be provided in header '" + AsynchConstants.MSG_HEADER + "'");
        return message;
    }

    protected String getTargetURI(Exchange exchange) {
        return getEndpoint().getTargetURI();
    }

    protected String getOperation(Exchange exchange) {
        String operation = exchange.getProperty(ExtCallComponentParams.EXTERNAL_CALL_OPERATION, String.class);
        return hasText(operation) ? operation : getTargetURI(exchange);
    }

    protected String getOperationKey(Exchange exchange) {
        return getEndpoint().getKeyType().getExpression().evaluate(exchange, Object.class).toString();
    }

    protected ExternalCallService getService(Exchange exchange) {
        return getEndpoint().getService();
    }

    @Override
    public ExternalCallEndpoint getEndpoint() {
        return (ExternalCallEndpoint) super.getEndpoint();
    }
}
