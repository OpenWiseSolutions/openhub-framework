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

package org.openhubframework.openhub.core.common.extension;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.route.AbstractExtRoute;


/**
 * Parent class for loading OpenHub extensions.
 * There is collection of Spring XML configurations and each XML configuration represents root configuration
 * for each extension. New child Spring context will be created for each extension - see {@link #loadExtensions(String...)}.
 * <p/>
 * If {@link #isAutoRouteAdding()} is true then route definitions which extends {@link AbstractExtRoute})
 * will be automatically added to {@link CamelContext}.
 *
 * @author Petr Juza
 * @see ClassPathXmlApplicationContext
 */
public abstract class AbstractExtensionConfigurationLoader implements ApplicationContextAware, CamelContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExtensionConfigurationLoader.class);

    private ApplicationContext parentContext;

    private CamelContext camelContext;

    private boolean autoRouteAdding = true;

    /**
     * Creates new configuration loader.
     */
    protected AbstractExtensionConfigurationLoader() {
    }

    /**
     * Creates new configuration loader.
     *
     * @param parentContext the Spring parent context
     * @param camelContext the Camel context
     */
    protected AbstractExtensionConfigurationLoader(ApplicationContext parentContext, CamelContext camelContext) {
        Assert.notNull(parentContext, "parentContext must not be null");
        Assert.notNull(camelContext, "camelContext must not be null");

        this.parentContext = parentContext;
        this.camelContext = camelContext;
    }

    /**
     * Loads Spring configuration files.
     * Each config represents root configuration file for one extension and new child Spring context will be
     * created for each extension.
     *
     * @param extConfigLocations Extension configuration locations
     */
    protected final void loadExtensions(String... extConfigLocations) {
        Assert.state(parentContext != null, "parent context is not defined");
        Assert.state(camelContext != null, "camel context is not defined");

        if (extConfigLocations == null) {
            return;
        }

        int count = 0;
        for (String extConfigLocation : extConfigLocations) {
            try {
                loadExtension(extConfigLocation, ++count);
            } catch (Exception ex) {
                String msg = "error during extension configuration '" + extConfigLocation + "' loading";

                LOG.error(msg, ex);

                throw new ExtensionConfigurationException(msg, ex);
            }
        }
    }

    private void loadExtension(String extConfigLocation, int extNumber) throws Exception {
        LOG.debug("new extension context for '" + extConfigLocation + "' started ...");

        ClassPathXmlApplicationContext extContext = new ClassPathXmlApplicationContext(parentContext);
        extContext.setId("OpenHub extension nr. " + extNumber);
        extContext.setDisplayName("OpenHub extension context for '" + extConfigLocation + '"');
        extContext.setConfigLocation(extConfigLocation);

        extContext.refresh();

        // add routes into Camel context
        if (isAutoRouteAdding()) {
            Map<String, AbstractExtRoute> beansOfType = extContext.getBeansOfType(AbstractExtRoute.class);
            for (Map.Entry<String, AbstractExtRoute> entry : beansOfType.entrySet()) {
                AbstractExtRoute route = entry.getValue();

                // note: route with existing route ID will override the previous one
                //  it's not possible automatically change route ID before adding to Camel context
                camelContext.addRoutes(route);
            }
        }

        LOG.debug("new extension context for '" + extConfigLocation + "' was successfully created");
    }

    /**
     * Sets parent Spring application context.
     *
     * @param parentContext the application context
     */
    @Override
    public void setApplicationContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
    }

    /**
     * Sets Camel context.
     *
     * @param camelContext the Camel context
     */
    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    /**
     * Returns whether automatically detect Camel routes (those which extends {@link AbstractExtRoute}) and add them
     * to Camel context.
     *
     * @return {@code true} for automatic adding routes, otherwise {@code false}
     */
    public boolean isAutoRouteAdding() {
        return autoRouteAdding;
    }

    /**
     * Sets whether automatically detect Camel routes (those which extends {@link AbstractExtRoute}) and add them
     * to Camel context.
     * <p/>
     * There is possibility to create own Camel context for specific extension and then it's not suitable
     * to add routes automatically.
     * <p/>Default value is {@code true}.
     *
     * @param autoRouteAdding {@code true} for automatic adding routes, otherwise {@code false}
     */
    public void setAutoRouteAdding(boolean autoRouteAdding) {
        this.autoRouteAdding = autoRouteAdding;
    }
}
