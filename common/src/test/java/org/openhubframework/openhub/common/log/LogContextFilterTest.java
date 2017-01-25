/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.common.log;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openhubframework.openhub.common.log.LogContextFilter.CTX_REQUEST_ID;
import static org.openhubframework.openhub.common.log.LogContextFilter.CTX_REQUEST_URI;
import static org.openhubframework.openhub.common.log.LogContextFilter.CTX_SESSION_ID;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test suite for {@link LogContextFilter} class.
 *
 * @author Petr Juza
 * @since 2.0
 */
@RunWith(JUnit4.class)
public class LogContextFilterTest {

    @Test
    public void testLogContext() throws IOException, ServletException {
        // prepare data
        final HttpServletRequest req = new MockHttpServletRequest("GET", "www.openhubframework.org");
        ServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                super.doFilter(request, response);

                // I have to verify it here because context values are reset at the end of filter processing
                assertThat(LogContext.getContextValue(CTX_REQUEST_URI), is(req.getRequestURI()));
                assertThat(LogContext.getContextValue(CTX_REQUEST_ID), notNullValue());
                assertThat(LogContext.getContextValue(CTX_SESSION_ID), nullValue()); // session is not created
            }
        };

        // action
        LogContextFilter filter = new LogContextFilter();
        filter.doFilter(req, res, chain);

        // verify
        assertThat(req.getCharacterEncoding(), is("UTF-8"));
    }

    @Test
    public void testLogContextWithHttpSession() throws IOException, ServletException {
        // prepare data
        final HttpServletRequest req = new MockHttpServletRequest("GET", "www.openhubframework.org");
        req.getSession(true);

        ServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                super.doFilter(request, response);

                // I have to verify it here because context values are reset at the end of filter processing
                assertThat(LogContext.getContextValue(CTX_REQUEST_URI), is(req.getRequestURI()));
                assertThat(LogContext.getContextValue(CTX_REQUEST_ID), notNullValue());
                assertThat(LogContext.getContextValue(CTX_SESSION_ID), notNullValue());
            }
        };

        // action
        LogContextFilter filter = new LogContextFilter();
        filter.doFilter(req, res, chain);

        // verify
        assertThat(req.getCharacterEncoding(), is("UTF-8"));
    }
}
