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

package org.openhubframework.openhub.common.log;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A filter that provides web-application specific context information to the logging subsystem. The pieces of
 * context information is:
 * <ul>
 * <li><b>REQUEST_URI</b> - the URI of the current request,</li>
 * <li><b>REQUEST_ID</b> - the globally unique identifier of the current request,</li>
 * <li><b>SESSION_ID</b> - the identifier of the current HTTP session,</li>
 * <li><b>CORRELATION_ID</b> - the identifier of asynchronous message</li>
 * <li><b>SOURCE_SYSTEM</b> - the identifier of external source system</li>
 * </ul>
 *
 * @author Michal Palicka
 * @author Jan Loose
 * @since 0.1
 */
public class LogContextFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LogContextFilter.class);

    // ----------------------------------------------------------------------
    // class (static) fields
    // ----------------------------------------------------------------------

    private static final String LOG_SESSION_ID = "LOG_SESSION_ID";

    /**
     * The URI of the current request.
     */
    public static final String CTX_REQUEST_URI = "REQUEST_URI";

    /**
     * The globally unique identifier of the current request.
     */
    public static final String CTX_REQUEST_ID = "REQUEST_ID";

    /**
     * The identifier of the current HTTP session.
     */
    public static final String CTX_SESSION_ID = "SESSION_ID";

    /**
     * The identifier of asynchronous message.
     * <p>
     * Note: unique is combination source system and correlationID but there is low probability that
     * there will be two same correlationIDs.
     */
    public static final String CTX_CORRELATION_ID = "CORRELATION_ID";

    /**
     * The identifier of source system.
     * <p>
     * Note: source system that calls Integration Platform
     */
    public static final String CTX_SOURCE_SYSTEM = "SOURCE_SYSTEM";

    /**
     * The identifier of process.
     * <p>
     * Note: process ID serves for pairing more requests with one process.
     */
    public static final String CTX_PROCESS_ID = "PROCESS_ID";

    // ----------------------------------------------------------------------
    // public methods
    // ----------------------------------------------------------------------

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        try {
            // initialize the request context
            initContext((HttpServletRequest) request);

            // pass the request to the process chain
            chain.doFilter(request, response);
        } finally {
            // clear the context as soon as the request completes
            clearContext();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Filter initialized successfully");
    }

    @Override
    public void destroy() {
        LOG.info("Filter destroyed successfully");
    }

    // ----------------------------------------------------------------------
    // private methods
    // ----------------------------------------------------------------------

    private void initContext(HttpServletRequest request) {
        LogContext.setContextValue(CTX_REQUEST_URI, request.getRequestURI());

        // request identifier
        LogContext.setContextValue(CTX_REQUEST_ID, new GUID().toString());

        // session identifier
        final HttpSession session = request.getSession(false);
        if (session != null) {
            // create session log if -> 3 characters + ***** + 4 characters
            String logId = (String) session.getAttribute(LOG_SESSION_ID);
            if (logId == null) {
                session.setAttribute(LOG_SESSION_ID, logId = createSessionLogId(session.getId()));
            }

            LogContext.setContextValue(CTX_SESSION_ID, logId);
        }
    }

    /**
     * Creates the session log id -&gt; 3 characters + ***** + 4 characters.
     *
     * @param id the original session id
     * @return the safe session id
     */
    public static String createSessionLogId(String id) {
        String logId = id;
        if (id.length() > 10) {
            logId = id.subSequence(0, 3) + "*****" + id.substring(id.length() - 4);
        }
        return logId;
    }

    private void clearContext() {
        LogContext.clearContext();
    }
}
