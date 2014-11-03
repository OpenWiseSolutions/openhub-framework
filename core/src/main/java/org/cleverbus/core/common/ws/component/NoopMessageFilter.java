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

package org.cleverbus.core.common.ws.component;

import org.apache.camel.Exchange;
import org.apache.camel.component.spring.ws.filter.MessageFilter;
import org.apache.camel.component.spring.ws.filter.impl.BasicMessageFilter;
import org.springframework.ws.WebServiceMessage;

/**
 * Does no message filtering (post-processing).
 * <p/>
 * Used instead of the default {@link BasicMessageFilter}, which adds Camel Message headers to Soap Message headers,
 * and Camel Message attachments to Soap Message attachments.
 */
public class NoopMessageFilter implements MessageFilter {

    @Override
    public void filterProducer(Exchange exchange, WebServiceMessage producerResponse) {
    }

    @Override
    public void filterConsumer(Exchange exchange, WebServiceMessage consumerResponse) {
    }
}
