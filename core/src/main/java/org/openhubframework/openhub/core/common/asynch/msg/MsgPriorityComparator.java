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

package org.openhubframework.openhub.core.common.asynch.msg;

import java.util.Comparator;

import org.openhubframework.openhub.api.entity.Message;

import org.apache.camel.Exchange;
import org.springframework.util.Assert;


/**
 * Comparator sorts exchanges in the queue by {@link Message#getProcessingPriority() message priority of processing}.
 *
 * @author Petr Juza
 * @since 0.4
 */
public class MsgPriorityComparator implements Comparator<Exchange> {

    @Override
    public int compare(Exchange ex1, Exchange ex2) {
        Message msg1 = ex1.getIn().getBody(Message.class);
        Message msg2 = ex2.getIn().getBody(Message.class);

        Assert.notNull(msg1, "msg1 must not be null");
        Assert.notNull(msg2, "msg2 must not be null");

        return ((Integer)msg1.getProcessingPriority()).compareTo(msg2.getProcessingPriority());
    }
}
