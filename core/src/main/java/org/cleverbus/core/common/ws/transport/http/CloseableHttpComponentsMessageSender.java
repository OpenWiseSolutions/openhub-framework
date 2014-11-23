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

package org.cleverbus.core.common.ws.transport.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/**
 * {@code WebServiceMessageSender} implementation that uses <a href="http://hc.apache.org/httpcomponents-client">Apache
 * HttpClient</a> ({@link CloseableHttpClient}) to execute POST requests.
 * <p/>
 * Allows to use a pre-configured HttpClient instance, potentially with authentication, HTTP connection pooling, etc.
 * Authentication can also be set by injecting a {@link Credentials} instance (such as the {@link
 * UsernamePasswordCredentials}). Out of box contains {@link RemoveSoapHeadersInterceptor}.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @see HttpClient
 * @see CloseableHttpClient
 * @see PoolingHttpClientConnectionManager
 * @see UsernamePasswordCredentials
 */
public class CloseableHttpComponentsMessageSender extends HttpComponentsMessageSender {

    protected static final String MACHINE_NAME = "CleverBus-ESB";

    private static final int DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = (60 * 1000);
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    private static RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

    private HttpClientBuilder clientBuilder = HttpClients.custom();
    private ConnPoolControl connPoolControl;
    private HttpClient httpClient;

    private CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private Credentials credentials;
    private AuthScope authScope = AuthScope.ANY;

    private ThreadLocal<AuthCache> authCache = new ThreadLocal<AuthCache>();

    /**
     * Create a new instance of the {@code HttpClientMessageSender} with a default {@link HttpClient}
     * that uses a default {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager}.
     */
    public CloseableHttpComponentsMessageSender() {
        super();

        PoolingHttpClientConnectionManager connPoolControl = new PoolingHttpClientConnectionManager();
        connPoolControl.closeExpiredConnections();

        this.connPoolControl = connPoolControl;

        // default values which can be overridden
        setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS);
        setReadTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS);

        clientBuilder
                .addInterceptorFirst(new RemoveSoapHeadersInterceptor())
                .setUserAgent(MACHINE_NAME);

        authCache.set(new BasicAuthCache());
    }

    /**
     * Create a new instance of the {@code HttpClientMessageSender} with a default {@link HttpClient}
     * that uses a default {@link PoolingHttpClientConnectionManager}.
     */
    public CloseableHttpComponentsMessageSender(boolean usePreemptiveAuth) {
        this();

        if (usePreemptiveAuth) {
            getClientBuilder().addInterceptorFirst(new PreemptiveAuthInterceptor());
        }
    }

    /**
     * Sets the credentials to be used. If not set, no authentication is done.
     *
     * @see UsernamePasswordCredentials
     * @see NTCredentials
     */
    @Override
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Returns the {@code HttpClient} used by this message sender.
     */
    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Sets the timeout until a connection is established. A value of 0 means <em>never</em> timeout.
     *
     * @param timeout the timeout value in milliseconds
     * @see RequestConfig.Builder#setConnectTimeout(int)
     * @see RequestConfig.Builder#setConnectionRequestTimeout(int)
     */
    @Override
    public void setConnectionTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be a non-negative value");
        }
        requestConfigBuilder.setConnectTimeout(2 * timeout).setConnectionRequestTimeout(timeout);
    }

    /**
     * Set the socket read timeout for the underlying HttpClient. A value of 0 means <em>never</em> timeout.
     *
     * @param timeout the timeout value in milliseconds
     * @see RequestConfig.Builder#setSocketTimeout(int)
     */
    @Override
    public void setReadTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be a non-negative value");
        }
        requestConfigBuilder.setSocketTimeout(timeout);
    }

    /**
     * Closes connections that have been idle for at least the given amount of time. A value of 0 means
     * <em>never</em> timeout.
     *
     * @param timeout the timeout value in milliseconds
     * @see PoolingHttpClientConnectionManager#closeIdleConnections(long, java.util.concurrent.TimeUnit)
     */
    public void closeIdleConnections(long timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be a non-negative value");
        }
        ((PoolingHttpClientConnectionManager)getConnPoolControl()).closeIdleConnections(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the maximum number of connections allowed for the underlying HttpClient.
     *
     * @param maxTotalConnections the maximum number of connections allowed
     * @see HttpClientBuilder#setMaxConnTotal(int)
     * @see PoolingHttpClientConnectionManager#setMaxTotal(int)
     */
    @Override
    public void setMaxTotalConnections(int maxTotalConnections) {
        if (maxTotalConnections <= 0) {
            throw new IllegalArgumentException("maxTotalConnections must be a positive value");
        }
        getClientBuilder().setMaxConnTotal(maxTotalConnections);
        getConnPoolControl().setMaxTotal(maxTotalConnections);
    }

    /**
     * Sets the default maximum number of connections allowed per host for the underlying HttpClient.
     *
     * @param defaultMaxPerHost the maximum number of connections allowed per host
     * @see HttpClientBuilder#setMaxConnPerRoute(int)
     * @see PoolingHttpClientConnectionManager#setDefaultMaxPerRoute(int)
     */
    public void setDefaultMaxPerHost(int defaultMaxPerHost) {
        if (defaultMaxPerHost <= 0) {
            throw new IllegalArgumentException("defaultMaxPerHost must be a positive value");
        }
        getClientBuilder().setMaxConnPerRoute(defaultMaxPerHost);
        getConnPoolControl().setDefaultMaxPerRoute(defaultMaxPerHost);
    }

    /**
     * Sets the maximum number of connections per host for the underlying HttpClient. The maximum number of connections
     * per host can be set in a form accepted by the {@code java.util.Properties} class, like as follows:
     * <p/>
     * <pre>
     * https://esb.cleverbus.org/esb/=1
     * http://esb.cleverbus.org:8080/esb/=7
     * http://esb.cleverbus.org/esb/=10
     * </pre>
     * <p/>
     * The host can be specified as a URI (with scheme and port).
     *
     * @param maxConnectionsPerHost a properties object specifying the maximum number of connection
     * @see PoolingHttpClientConnectionManager#setMaxPerRoute(org.apache.http.conn.routing.HttpRoute, int)
     */
    @Override
    public void setMaxConnectionsPerHost(Map<String, String> maxConnectionsPerHost) throws URISyntaxException {
        for (Map.Entry<String, String> entry : maxConnectionsPerHost.entrySet()) {
            URI uri = new URI(entry.getKey());
            HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            HttpRoute route = new HttpRoute(host);

            PoolingHttpClientConnectionManager connectionManager =
                    (PoolingHttpClientConnectionManager) getConnPoolControl();

            int max = Integer.parseInt(entry.getValue());
            connectionManager.setMaxPerRoute(route, max);
            BasicScheme basicAuth = new BasicScheme();
            authCache.get().put(host, basicAuth);
        }
    }

    /**
     * Sets the authentication scope to be used. Only used when the {@code credentials} property has been set.
     * <p/>
     * By default, the {@link AuthScope#ANY} is used.
     *
     * @see #setCredentials(Credentials)
     */
    @Override
    public void setAuthScope(AuthScope authScope) {
        this.authScope = authScope;
    }

    /**
     * Template method that allows for creation of a {@link HttpContext} for the given uri. Default implementation
     * returns {@code null}.
     *
     * @param uri the uri to create the context for
     * @return the context, or {@code null}
     */
    @Override
    protected HttpContext createContext(URI uri) {
        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache.get());

        return context;
    }

    /**
     * Gets the {@link ConnPoolControl}.
     *
     * @return the {@link ConnPoolControl}
     */
    public ConnPoolControl getConnPoolControl() {
        return connPoolControl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (credentials != null) {
            credentialsProvider.setCredentials(authScope, credentials);
        }
        httpClient = getClientBuilder()
                .setConnectionManager((PoolingHttpClientConnectionManager) getConnPoolControl())
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(requestConfigBuilder.build())
                .build();
    }

    @Override
    public void destroy() throws Exception {
        ((CloseableHttpClient) getHttpClient()).close();
    }

    /**
     * Gets the {@code HttpClientBuilder}.
     *
     * @return the {@code HttpClientBuilder}
     */
    protected HttpClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    /**
     * Sets the implementation of {@link CredentialsProvider}.
     *
     * @param credentialsProvider the implementation of {@link CredentialsProvider}
     */
    protected void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * HttpClient {@link org.apache.http.HttpRequestInterceptor} implementation that removes {@code Content-Length} and
     * {@code Transfer-Encoding} headers from the request. Necessary, because some SAAJ and other SOAP implementations
     * set these headers themselves, and HttpClient throws an exception if they have been set.
     *
     */
    public static class RemoveSoapHeadersInterceptor implements HttpRequestInterceptor {

        /**
         * {@inheritDoc}
         */
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            if (request instanceof HttpEntityEnclosingRequest) {
                if (request.containsHeader(HTTP.TRANSFER_ENCODING)) {
                    request.removeHeaders(HTTP.TRANSFER_ENCODING);
                }
                if (request.containsHeader(HTTP.CONTENT_LEN)) {
                    request.removeHeaders(HTTP.CONTENT_LEN);
                }
            }
        }
    }

    /**
     * HttpClient {@link org.apache.http.HttpRequestInterceptor} implementation that configures the Apache Http Client
     * for preemptive authentication. In this mode, the client will send the basic authentication response even before
     * the server gives an unauthorized response in certain situations. This reduces the overhead of making requests
     * over authenticated connections.
     *
     * This behavior conforms to RFC2617: A client MAY preemptively send the corresponding Authorization header with
     * requests for resources in that space without receipt of another challenge from the server. Similarly, when
     * a client sends a request to a proxy, it may reuse a userid and password in the Proxy-Authorization header field
     * without receiving another challenge from the proxy server.
     *
     * The Apache Http Client does not support preemptive authentication out of the box, because if misused or used
     * incorrectly the preemptive authentication can lead to significant security issues, such as sending user
     * credentials in clear text to an unauthorized third party.
     */
    public static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

        /**
         * {@inheritDoc}
         */
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext
                        .CREDS_PROVIDER);
                HttpHost host = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                Credentials creds = credsProvider.getCredentials(new AuthScope(host.getHostName(), host.getPort()));
                if (creds == null) {
                    throw new HttpException("No credentials for preemptive authentication");
                }
                authState.update(new BasicScheme(), creds);
            }
        }
    }
}
