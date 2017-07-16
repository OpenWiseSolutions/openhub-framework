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

package org.openhubframework.openhub.core.common.camel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.spring.spi.ApplicationContextRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.Assert;

/**
 * Implementation {@link Registry} that load beans from all {@link ApplicationContext}s.
 * For every {@link ApplicationContext} is created own {@link ApplicationContextRegistry}.
 *
 * @author Roman Havlicek
 * @see Registry
 * @see DefaultCamelContext#setRegistry(Registry)
 * @since 2.0
 */
public class ApplicationContextsRegistry implements Registry, ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContextsRegistry.class);

    /**
     * List of registers for all application contexts.
     */
    private final Map<ApplicationContext, Registry> applicationContextsRegistry = new LinkedHashMap<>();

    @Override
    public Object lookupByName(String name) {
        for (Registry registry : applicationContextsRegistry.values()) {
            Object result = registry.lookupByName(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        for (Registry registry : applicationContextsRegistry.values()) {
            T result = registry.lookupByNameAndType(name, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        for (Registry registry : applicationContextsRegistry.values()) {
            Map<String, T> result = registry.findByTypeWithName(type);
            if (!MapUtils.isEmpty(result)) {
                return result;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        for (Registry registry : applicationContextsRegistry.values()) {
            Set<T> result = registry.findByType(type);
            if (!CollectionUtils.isEmpty(result)) {
                return result;
            }
        }
        return Collections.emptySet();
    }

    @Override
    public Object lookup(String name) {
        for (Registry registry : applicationContextsRegistry.values()) {
            Object result = registry.lookup(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public <T> T lookup(String name, Class<T> type) {
        for (Registry registry : applicationContextsRegistry.values()) {
            T result = registry.lookup(name, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public <T> Map<String, T> lookupByType(Class<T> type) {
        for (Registry registry : applicationContextsRegistry.values()) {
            Map<String, T> result = registry.lookupByType(type);
            if (!MapUtils.isEmpty(result)) {
                return result;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        Assert.notNull(event, "event must not be null");

        ApplicationContext applicationContext = event.getApplicationContext();

        //remove context from created registry
        applicationContextsRegistry.remove(applicationContext);

        //add new found contexts
        if (event instanceof ContextRefreshedEvent || event instanceof ContextStartedEvent) {
            LOG.info("Create new registry for context '{}.'", applicationContext.getDisplayName());
            applicationContextsRegistry.put(applicationContext, new ApplicationContextRegistry(applicationContext));
        }
    }
}
