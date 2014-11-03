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

package org.cleverbus.api.common;

import static org.cleverbus.api.common.ExchangeConstants.TRACE_HEADER;

import javax.annotation.Nullable;

import org.cleverbus.api.asynch.model.TraceHeader;
import org.cleverbus.api.exception.IllegalDataException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.util.Assert;

/**
 * Some helper methods for working with {@link Exchange} objects.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
public final class ExchangeHelper {

    /**
     * Returns the {@link TraceHeader} or throws an {@link IllegalDataException} if it is not present and is mandatory.
     * @param exchange the exchange message
     * @param mandatory true if trace header is mandatory output
     * @return the {@link TraceHeader} object as header value from exchange
     */
    @Nullable
    public static TraceHeader getTraceHeader(Exchange exchange, boolean mandatory) {
        Assert.notNull(exchange, "the exchange must not be null");

        TraceHeader answer = exchange.getIn().getHeader(TRACE_HEADER, TraceHeader.class);

        if (mandatory) {
            Constraints.notNull(answer, "TraceHeader is mandatory and is not included in this exchange.");
        }
        return answer;
    }

    /**
     * Propagates (copies) the {@code Body}, the {@code Attachments} and the {@code Headers} of the {@link Message}
     * from from IN to OUT.
     *
     * @param exchange the exchange message
     */
    public static void propagateMessage(Exchange exchange) {
        Assert.notNull(exchange, "the exchange must not be null");

        // copy headers from IN to OUT to propagate them
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        // copy attachments from IN to OUT to propagate them
        exchange.getOut().setAttachments(exchange.getIn().getAttachments());
        // copy body from IN to OUT to propagate it
        exchange.getOut().setBody(exchange.getIn().getBody());
    }

    /**
     * Utility classes should not have a public constructor.
     */
    private ExchangeHelper() {
    }
}
