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

package org.cleverbus.component.throttling;

import org.cleverbus.api.entity.Message;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.util.Assert;


/**
 * Producer for {@link ThrottlingComponent throttling} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingProducer extends DefaultProducer {

    /**
     * Creates new producer.
     *
     * @param endpoint the endpoint
     */
    public ThrottlingProducer(ThrottlingEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ThrottlingEndpoint endpoint = (ThrottlingEndpoint) getEndpoint();

        ThrottleScope throttleScope;

        if (endpoint.getRequestType() == RequestTypeEnum.SYNC) {
            // sync
            throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, endpoint.getOperationName());

        } else {
            // async
            Message msg = exchange.getIn().getBody(Message.class);

            Assert.notNull(msg, "the msg must not be null");

            throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(), msg.getOperationName());
        }

        endpoint.getThrottlingProcessor().throttle(throttleScope);
    }
}
