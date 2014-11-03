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

package org.cleverbus.core.conf;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.route.RouteConstants;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;


/**
 * Configuration checker that is called when application context is initialized.
 * There are some predefined checks or you can define your own checking via {@link ConfCheck} interface.
 * <p/>
 * Checking of {@link #checkLocalhostUri() localhost URI} must be explicitly enabled by calling
 * {@link #setCheckUrl(boolean)} because there were few problems on some platforms during testing.
 * <p/>
 * Initialized this listener in child "Spring WS" application context.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 * @see CheckingConfMessageDispatcherServlet
 * @see ConfCheck
 */
public class ConfigurationChecker implements ApplicationListener<ContextRefreshedEvent> {

    private static final String ENDPOINTS_INCLUDE_PATTERN = "endpoints.includePattern";

    private static final String LOCALHOST_URI = "contextCall.localhostUri";

    private static final String ENDPOINT_FILTER = "requestSaving.endpointFilter";

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    @Value("${" + ENDPOINTS_INCLUDE_PATTERN + "}")
    private String endpointsIncludePattern;

    /**
     * URI of this localhost application, including port number.
     */
    @Value("${" + LOCALHOST_URI + "}")
    private String localhostUri;

    /**
     * Pattern for filtering endpoints URI which requests/response should be saved.
     */
    @Value("${" + ENDPOINT_FILTER + "}")
    private String endpointFilter;

    private boolean checkUrl = false;

    @Autowired(required = false)
    private List<ConfCheck> checks;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        checkConfiguration(event.getApplicationContext());
    }

    /**
     * Checks configuration.
     *
     * @param context the application context
     */
    void checkConfiguration(ApplicationContext context) {
        Log.debug("Checking configuration validity ...");

        Assert.state(context.getParent() != null,
                "ConfigurationChecker must be initialized in child context");

        try {
            if (checkUrl) {
                checkLocalhostUri();
            }

            checkPatterns();

            // go through "local" checks
            if (checks != null) {
                for (ConfCheck check : checks) {
                    check.check();
                }
            }
        } catch (ConfigurationException ex) {
            Log.error("Configuration error", ex);

            // stop parent context (I don't know how to stop it in other way)
            ConfigurableApplicationContext rootContext =
                    (ConfigurableApplicationContext)context.getParent();
            rootContext.close();
        }
    }

    /**
     * Checks if configuration parameter "contextCall.localhostUri" is valid - calls PING service.
     */
    private void checkLocalhostUri() {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // for example: http://localhost:8080/esb/http/ping
            HttpGet httpGet = new HttpGet(localhostUri + RouteConstants.HTTP_URI_PREFIX + "ping");

            httpClient.execute(httpGet);
        } catch (IOException ex) {
            throw new ConfigurationException("Configuration error - parameter '" + LOCALHOST_URI + "' with value '"
                    + localhostUri + "' is probably wrong, URI isn't reachable.", ex);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }

        Log.debug("Parameter '" + LOCALHOST_URI + "' is OK");
    }

    /**
     * Checks the following configuration parameters if they can be compiled as {@link Pattern}:
     * <ul>
     *     <li>{@value #ENDPOINTS_INCLUDE_PATTERN}
     *     <li>{@value #ENDPOINT_FILTER}
     * </ul>
     */
    private void checkPatterns() {
        checkPattern(endpointsIncludePattern, ENDPOINTS_INCLUDE_PATTERN);
        checkPattern(endpointFilter, ENDPOINT_FILTER);
    }

    private void checkPattern(String pattern, String paramName) {
        try {
            Pattern.compile(pattern);
        } catch (PatternSyntaxException ex) {
            throw new ConfigurationException(
                    "Configuration error - parameter '" + paramName + "' with value '"
                    + pattern + "' has wrong syntax, can't be compiled.", ex);
        }

        Log.debug("Parameter '" + paramName + "' is OK");
    }

    /**
     * Enables checking of localhost URI.
     *
     * @param checkUrl {@code true} to enable checking otherwise disable it
     */
    public void setCheckUrl(boolean checkUrl) {
        this.checkUrl = checkUrl;
    }
}
