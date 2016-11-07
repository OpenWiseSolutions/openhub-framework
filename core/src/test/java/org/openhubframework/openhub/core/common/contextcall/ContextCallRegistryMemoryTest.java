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

package org.openhubframework.openhub.core.common.contextcall;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.openhubframework.openhub.api.exception.NoDataFoundException;

import org.junit.Test;


/**
 * Test suite for {@link ContextCallRegistryMemoryImpl}.
 *
 * @author Petr Juza
 */
public class ContextCallRegistryMemoryTest {

    @Test
    public void testBasicScenarios() {
        ContextCallRegistry callRegistry = new ContextCallRegistryMemoryImpl();

        String callId = UUID.randomUUID().toString();

        // params
        ContextCallParams params = new ContextCallParams(String.class, "indexOf");

        callRegistry.addParams(callId, params);

        assertThat(callRegistry.getParams(callId), is(params));
        assertThat(callRegistry.getParams(callId), is(params));

        // response
        String res = "response";

        callRegistry.addResponse(callId, res);

        assertThat(callRegistry.getResponse(callId, String.class), is(res));
        assertThat(callRegistry.getResponse(callId, String.class), is(res));

        // clear all
        callRegistry.clearCall(callId);

        try {
            callRegistry.getParams(callId);
            fail("there is no params with call ID=" + callId);
        } catch (NoDataFoundException ex) {
            // everything OK
        }

        try {
            callRegistry.getResponse(callId, String.class);
            fail("there is no response with call ID=" + callId);
        } catch (NoDataFoundException ex) {
            // everything OK
        }
    }
}
