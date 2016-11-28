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

package org.openhubframework.openhub.admin.web.filter;

import static org.springframework.util.StringUtils.hasText;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;


/**
 * Simple request/response logging filter that writes the request URI
 * (and optionally the query string) including request content <strong>and also response content</strong> to the LogContext.
 * <p/>
 * This filter is very useful to log REST layer.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 * @see #addSupportedContentTypes(String...)
 **/
public class RequestResponseLoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    public static final String DEFAULT_REQUEST_MESSAGE_PREFIX = "Request: ";
    public static final String DEFAULT_REQUEST_MESSAGE_SUFFIX = "";
    public static final String DEFAULT_RESPONSE_MESSAGE_PREFIX = "Response: ";
    public static final String DEFAULT_RESPONSE_MESSAGE_SUFFIX = "";

    private static final int DEFAULT_MAX_REQUEST_PAYLOAD_LENGTH = 1000;
    private static final int DEFAULT_MAX_RESPONSE_PAYLOAD_LENGTH = 10000;
    private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList("application/json", "application/xml");

    private String requestMessagePrefix = DEFAULT_REQUEST_MESSAGE_PREFIX;
    private String requestMessageSuffix = DEFAULT_REQUEST_MESSAGE_SUFFIX;
    private String responseMessagePrefix = DEFAULT_RESPONSE_MESSAGE_PREFIX;
    private String responseMessageSuffix = DEFAULT_RESPONSE_MESSAGE_SUFFIX;
    private boolean includeClientInfo = false;
    private int maxRequestPayloadLength = DEFAULT_MAX_REQUEST_PAYLOAD_LENGTH;
    private int maxResponsePayloadLength = DEFAULT_MAX_RESPONSE_PAYLOAD_LENGTH;
    private final List<String> supportedContentTypes = SUPPORTED_CONTENT_TYPES;
    private boolean logUnsupportedContentType = false;

    @Override
    public void init(FilterConfig filterConf) throws ServletException {
        // nothing
    }

    @Override
    public void destroy() {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // enable filter logic only for debugging purposes and http req/res
        if (!logger.isDebugEnabled() || !(request instanceof HttpServletRequest)
            || !(response instanceof HttpServletResponse)) {
            // do nothing more
            chain.doFilter(request, response);
            return;
        }

        boolean chainDone = false;
        boolean errorInChainCall = false;

        try (OutputStream ignored = response.getOutputStream()) {

            HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);
            HttpServletRequest requestToUse = (HttpServletRequest) request;
            requestToUse = new ContentCachingRequestWrapper(requestToUse);

            errorInChainCall = true;
            chain.doFilter(requestToUse, responseCopier);
            errorInChainCall = false;

            chainDone = true;
            responseCopier.flushBuffer();

            // enable only logging for supported content types
            if (!isLogUnsupportedContentType() && !isSupportedContentType(response.getContentType())) {
                logger.trace("Logging is skipped due support for logging unsupported type is disabled");
                // do nothing more
                return;
            }

            byte[] copy = responseCopier.getCopy();

            logger.debug(getRequestMessage(requestToUse));
            logger.debug(getResponseMessage(response, copy));

        } catch (Exception e) {
            if (!errorInChainCall) {
                logger.error("Error in req/res logging.", e);
            } else {
                // don't log chain call exception stack trace here ..
                logger.warn("Error in chain call: {}", e.getMessage());
                // don't catch chain call exceptions and rethrow it
                throw e;
            }
            if (!chainDone) {
                chain.doFilter(request, response);
            }
        }

    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setRequestMessagePrefix(String beforeMessagePrefix) {
        this.requestMessagePrefix = beforeMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setRequestMessageSuffix(String beforeMessageSuffix) {
        this.requestMessageSuffix = beforeMessageSuffix;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setResponseMessagePrefix(String afterMessagePrefix) {
        this.responseMessagePrefix = afterMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setResponseMessageSuffix(String afterMessageSuffix) {
        this.responseMessageSuffix = afterMessageSuffix;
    }

    /**
     * Set whether the client address and session id should be included in the
     * log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeClientInfo" in the filter definition in {@code web.xml}.
     */
    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    /**
     * Return whether the client address and session id should be included in the
     * log message.
     */
    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    /**
     * Sets the maximum length of the request payload body to be included in the log message.
     * Default is {@link #DEFAULT_MAX_REQUEST_PAYLOAD_LENGTH}
     */
    public void setMaxRequestPayloadLength(int maxRequestPayloadLength) {
        Assert.isTrue(maxRequestPayloadLength >= 0, "'maxRequestPayloadLength' should be larger than or equal to 0");
        this.maxRequestPayloadLength = maxRequestPayloadLength;
    }

    /**
     * Return the maximum length of the request payload body to be included in the log message.
     */
    protected int getRequestMaxPayloadLength() {
        return this.maxRequestPayloadLength;
    }

    /**
     * Sets the maximum length of the response payload body to be included in the log message.
     * Default is {@link #DEFAULT_MAX_RESPONSE_PAYLOAD_LENGTH}
     */
    public void setMaxResponsePayloadLength(int maxResponsePayloadLength) {
        Assert.isTrue(maxResponsePayloadLength >= 0, "'maxResponsePayloadLength' should be larger than or equal to 0");
        this.maxResponsePayloadLength = maxResponsePayloadLength;
    }

    /**
     * Return the maximum length of the payload body to be included in the log message.
     */
    protected int getMaxResponsePayloadLength() {
        return this.maxResponsePayloadLength;
    }

    /**
     * Returns the actually supported content types of request which should be logged.
     */
    protected List<String> getSupportedContentTypes() {
        return Collections.unmodifiableList(supportedContentTypes);
    }

    /**
     * Adds content types which should be logged.
     *
     * @param contentTypes as collection of supported content types
     */
    public void addSupportedContentTypes(String... contentTypes) {
        Assert.notNull(contentTypes, "contentTypes must not be null");
        Assert.isTrue(contentTypes.length >= 1, "'contentTypes' length should be larger than or equal to 1");
        supportedContentTypes.addAll(Arrays.asList(contentTypes));
    }

    /**
     * Set whether the unsupported content types should be logged or not.
     */
    public void setLogUnsupportedContentType(boolean logUnsupportedContentType) {
        this.logUnsupportedContentType = logUnsupportedContentType;
    }

    /**
     * Return whether the unsupported content type should be included in the
     * log message.
     */
    protected boolean isLogUnsupportedContentType() {
        return this.logUnsupportedContentType;
    }

    /**
     * Get the message to write to the log the request.
     *
     * @see #createRequestMessage(HttpServletRequest, String, String)
     */
    protected String getRequestMessage(HttpServletRequest request) {
        return createRequestMessage(request, this.requestMessagePrefix, this.requestMessageSuffix);
    }

    /**
     * Get the message to write to the log the response.
     */
    protected String getResponseMessage(ServletResponse response, byte[] copy) {
        try {
            StringBuilder msg = new StringBuilder();
            msg.append(responseMessagePrefix);

            String contentType = hasText(response.getContentType()) ? response.getContentType() : "unknown";
            int responseSize = copy.length;
            String copyString;

            String croppedMessage = "";

            if (!isSupportedContentType(contentType)) {
                copyString = "";
                croppedMessage = " cropped (" + contentType + ")";
            } else if (responseSize > getMaxResponsePayloadLength()) {
                croppedMessage = "(cropped to " + getMaxResponsePayloadLength() + " characters)";
                copyString = new String(copy, response.getCharacterEncoding());
            } else {
                copyString = new String(copy, 0, Math.min(responseSize, getMaxResponsePayloadLength()),
                    response.getCharacterEncoding());
            }

            // type
            msg.append("type=").append(contentType);
            // size
            msg.append(", size=").append(responseSize);
            // content
            msg.append(", content").append(croppedMessage).append("=").append(copyString);

            msg.append(responseMessageSuffix);
            return msg.toString();

        } catch (Exception e) {
            final String msg = "Error in req/res logging.";
            logger.error(msg, e);
            return msg + ": " + e.getMessage();
        }
    }

    /**
     * Checks whether provided content type is supported to be logged or not.
     *
     * @return {@code true} if the {@code contentType} is supported, otherwise return {@code false} (also if contentType
     * is {@literal null}
     */
    private boolean isSupportedContentType(String contentType) {
        if (hasText(contentType)) {
            final String normalized;
            if (contentType.contains(";")) {
                normalized = contentType.substring(0, contentType.indexOf(";"));
            } else {
                normalized = contentType;
            }

            return getSupportedContentTypes().contains(normalized);
        }

        return false;
    }

    /**
     * Create a log message for the given request, prefix and suffix.
     * <p>If {@code includeQueryString} is {@code true}, then the inner part
     * of the log message will take the form {@code request_uri?query_string};
     * otherwise the message will simply be of the form {@code request_uri}.
     * <p>The final message is composed of the inner part as described and
     * the supplied prefix and suffix.
     */
    private String createRequestMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(StringUtils.capitalize(request.getMethod()));
        msg.append(", uri=").append(request.getRequestURI());
        // if query string exists
        msg.append(hasText(request.getQueryString()) ? '?' + request.getQueryString() : "");
        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }
        String client = request.getRemoteAddr();
        if (StringUtils.hasLength(client)) {
            msg.append(";client=").append(client);
        }
        String user = request.getRemoteUser();
        if (user != null) {
            msg.append(";user=").append(user);
        }
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, getRequestMaxPayloadLength());
                String payload;
                try {
                    payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    payload = "[unknown]";
                }
                msg.append(";payload=").append(payload);
            }

        }
        msg.append(suffix);
        return msg.toString();
    }


    private class ServletOutputStreamCopier extends ServletOutputStream {

        private OutputStream outputStream;

        private ByteArrayOutputStream copy;

        public ServletOutputStreamCopier(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.copy = new ByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            copy.write(b);
        }

        public byte[] getCopy() {
            return copy.toByteArray();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // nothing to set
        }
    }

    private class HttpServletResponseCopier extends HttpServletResponseWrapper {

        private ServletOutputStream outputStream;

        private PrintWriter writer;

        private ServletOutputStreamCopier copier;


        public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
            super(response);
        }


        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }

            if (outputStream == null) {
                outputStream = getResponse().getOutputStream();
                copier = new ServletOutputStreamCopier(outputStream);
            }

            return copier;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }

            if (writer == null) {
                copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
                writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
            }

            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                copier.flush();
            }
        }

        public byte[] getCopy() {
            if (copier != null) {
                return copier.getCopy();
            } else {
                return new byte[0];
            }
        }
    }
}
