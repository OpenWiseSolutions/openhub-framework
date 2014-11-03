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

package org.cleverbus.api.asynch;

import org.cleverbus.api.asynch.model.CallbackResponse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


/**
 * Abstract processor that helps to create response for asynchronous incoming request.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see "AsynchInMessageRoute"
 */
public abstract class AsynchResponseProcessor implements Processor {

    @Override
    public final void process(Exchange exchange) throws Exception {
        // check error
        CallbackResponse callbackResponse = (CallbackResponse)
                exchange.removeProperty(AsynchConstants.ERR_CALLBACK_RES_PROP);

        if (callbackResponse == null) {
            // no error
            callbackResponse = exchange.getIn().getBody(CallbackResponse.class);
        }

        exchange.getIn().setBody(setCallbackResponse(callbackResponse));
    }

    /**
     * Sets {@link CallbackResponse} to specific response and returns it.
     *
     * @param callbackResponse the callback response
     * @return response
     */
    protected abstract Object setCallbackResponse(CallbackResponse callbackResponse);
}
