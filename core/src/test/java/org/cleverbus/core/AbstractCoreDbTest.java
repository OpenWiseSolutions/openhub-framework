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

package org.cleverbus.core;

import java.util.HashMap;
import java.util.Map;

import org.cleverbus.api.asynch.model.TraceHeader;
import org.cleverbus.api.asynch.model.TraceIdentifier;
import org.cleverbus.core.common.asynch.AsynchInMessageRoute;
import org.cleverbus.core.common.asynch.TraceHeaderProcessor;
import org.cleverbus.test.AbstractDbTest;
import org.cleverbus.test.ActiveRoutes;

import org.joda.time.DateTime;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;


/**
 * Parent class for all tests with database in core module.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ContextConfiguration(locations = {"classpath:/META-INF/test_core_db_conf.xml", "classpath:/META-INF/sp_async.xml"})
@ActiveRoutes(classes = {AsynchInMessageRoute.class})
public abstract class AbstractCoreDbTest extends AbstractDbTest {

    private TraceHeader traceHeader;

    private Map<String, Object> headers;

    /**
     * Prepares {@link TraceHeaderProcessor#TRACE_HEADER trace header} data.
     */
    @Before
    public void prepareTraceHeaderData() {
        headers = new HashMap<String, Object>();

        traceHeader = new TraceHeader();

        TraceIdentifier traceId = new TraceIdentifier();
        traceId.setCorrelationID("123-456-789");
        traceId.setApplicationID("crm");
        traceId.setTimestamp(DateTime.now());

        getTraceHeader().setTraceIdentifier(traceId);

        getHeaders().put(TraceHeaderProcessor.TRACE_HEADER, getTraceHeader());
    }

    protected TraceHeader getTraceHeader() {
        return traceHeader;
    }

    protected Map<String, Object> getHeaders() {
        return headers;
    }
}
