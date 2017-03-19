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

package org.openhubframework.openhub.test;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.ToDefinition;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.model.CallbackResponse;
import org.openhubframework.openhub.api.asynch.model.ConfirmationTypes;


/**
 * Handy methods for testing.
 *
 * @author Petr Juza
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Replaces calling TO {@link AsynchConstants#URI_ASYNCH_IN_MSG} with processor that creates OK response.
     * <p>
     * Useful when you want to test input (IN) route of asynchronous process.
     *
     * @param builder the advice builder
     */
    public static void replaceToAsynch(AdviceWithRouteBuilder builder) {
        // remove AsynchInMessageRoute.URI_ASYNCH_IN_MSG
        builder.weaveByType(ToDefinition.class).replace().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // creates OK response
                CallbackResponse callbackResponse = new CallbackResponse();
                callbackResponse.setStatus(ConfirmationTypes.OK);

                exchange.getIn().setBody(callbackResponse);
            }
        });
    }
}
