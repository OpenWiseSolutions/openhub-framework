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

package org.openhubframework.openhub.core;

import java.util.HashMap;
import java.util.Map;

import org.openhubframework.openhub.api.asynch.model.TraceHeader;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.core.common.asynch.AsynchInMessageRoute;
import org.openhubframework.openhub.core.common.asynch.TraceHeaderProcessor;
import org.openhubframework.openhub.test.AbstractDbTest;
import org.openhubframework.openhub.test.ActiveRoutes;

import org.joda.time.DateTime;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;


/**
 * Parent class for all tests with database in core module.
 *
 * @author Petr Juza
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
