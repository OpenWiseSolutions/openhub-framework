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

package org.openhubframework.openhub.core.confcheck;

import static org.openhubframework.openhub.api.configuration.CoreProps.*;
import static org.openhubframework.openhub.api.route.RouteConstants.HTTP_URI_PREFIX;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.exception.validation.ConfigurationException;


/**
 * Configuration checker that is called when application context is initialized.
 * There are some predefined checks or you can define your own checking via {@link ConfCheck} interface.
 * <p/>
 * Checks will be performed after the application is fully started, if running with embedded servlet container,
 * it will wait for its full initialization, see Spring {@link ApplicationReadyEvent} for more info.
 * <p/>
 * Checking of {@link #checkLocalhostUri() localhost URI} must be explicitly enabled by setting
 * property "{@code ohf.server.localhostUri.check}".
 * <p/>
 * Initialize this listener in child web application context only.
 *
 * @author Petr Juza
 * @since 0.4
 * @see ConfCheck
 */
public class ConfigurationChecker implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationChecker.class);

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    @ConfigurableValue(key = ENDPOINTS_INCLUDE_PATTERN)
    private ConfigurationItem<String> endpointsIncludePattern;

    /**
     * URI of this localhost application, including port number.
     */
    @ConfigurableValue(key = SERVER_LOCALHOST_URI)
    private ConfigurationItem<String> localhostUri;

    /**
     * Pattern for filtering endpoints URI which requests/response should be saved.
     */
    @ConfigurableValue(key = REQUEST_SAVING_ENDPOINT_FILTER)
    private ConfigurationItem<String> endpointFilter;

    /**
     * Enable/disable checking of localhostUri (because there were few problems on some platforms during testing).
     */
    @ConfigurableValue(key = SERVER_LOCALHOST_URI_CHECK)
    private ConfigurationItem<Boolean> checkUrl;

    @Autowired(required = false)
    private List<ConfCheck> checks;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            checkConfiguration(ctx);
        }
    }

    /**
     * Checks configuration.
     *
     * @param context the application context
     * @throws ConfigurationException when there is error in configuration
     */
    void checkConfiguration(ApplicationContext context) {
        LOG.debug("Checking configuration validity ...");

        try {
            if (checkUrl.getValue()) {
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
            LOG.error("Configuration error", ex);

            // stop parent context (I don't know how to stop it in other way)
            if (context.getParent() != null) {
                context = context.getParent();
            }

            ((ConfigurableApplicationContext)context).close();
        }
    }

    /**
     * Checks if configuration parameter "ohf.server.localhostUri" is valid - calls PING service.
     */
    private void checkLocalhostUri() {
        if (StringUtils.isEmpty(localhostUri.getValue())) {
            LOG.debug("Parameter '" + SERVER_LOCALHOST_URI + "' is skipped because it's empty");
            return;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // for example: http://localhost:8080/esb/http/ping
            HttpGet httpGet = new HttpGet(localhostUri.getValue() + HTTP_URI_PREFIX + "ping");

            httpClient.execute(httpGet);
        } catch (IOException ex) {
            throw new ConfigurationException("Configuration error - parameter '" + SERVER_LOCALHOST_URI + "' with value '"
                    + localhostUri + "' is probably wrong, URI isn't reachable.", ex, SERVER_LOCALHOST_URI);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }

        LOG.debug("Parameter '" + SERVER_LOCALHOST_URI + "' is OK");
    }

    /**
     * Checks the following configuration parameters if they can be compiled as {@link Pattern}:
     * <ul>
     *     <li>{@value CoreProps#ENDPOINTS_INCLUDE_PATTERN}
     *     <li>{@value CoreProps#REQUEST_SAVING_ENDPOINT_FILTER}
     * </ul>
     */
    private void checkPatterns() {
        checkPattern(endpointsIncludePattern.getValue(), ENDPOINTS_INCLUDE_PATTERN);
        checkPattern(endpointFilter.getValue(), REQUEST_SAVING_ENDPOINT_FILTER);
    }

    private void checkPattern(String pattern, String paramName) {
        try {
            Pattern.compile(pattern);
        } catch (PatternSyntaxException ex) {
            throw new ConfigurationException(
                    "Configuration error - parameter '" + paramName + "' with value '"
                    + pattern + "' has wrong syntax, can't be compiled.", ex, paramName);
        }

        LOG.debug("Parameter '" + paramName + "' is OK");
    }
}
