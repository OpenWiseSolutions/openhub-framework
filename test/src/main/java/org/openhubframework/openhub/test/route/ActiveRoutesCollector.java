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

package org.openhubframework.openhub.test.route;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotationDeclaringClass;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.spring.boot.*;
import org.apache.camel.test.spring.CamelSpringTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;


/**
 * Implementation of {@link RoutesCollector} for tests that adds only active routes into camel context.
 * <p>
 * If there is defined property '{@value TEST_CAMEL_INIT_ALL_ROUTES}' with true value
 * then all routes from Spring application context are added into Camel context otherwise only active routes
 * are added - those listed in annotation {@link ActiveRoutes}.
 * <p>
 * Note: if I have to extends RoutesCollector because I need to mis-match condition in {@link CamelAutoConfiguration}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class ActiveRoutesCollector extends RoutesCollector {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveRoutesCollector.class);

    public static final String TEST_CAMEL_INIT_ALL_ROUTES = "test.camel.initAllRoutes";

    private final ApplicationContext applicationContext;

    private final List<CamelContextConfiguration> camelContextConfigurations;

    private final CamelConfigurationProperties configurationProperties;

    public ActiveRoutesCollector(ApplicationContext applicationContext, List<CamelContextConfiguration> camelContextConfigurations,
            CamelConfigurationProperties configurationProperties) {
        super(applicationContext, camelContextConfigurations, configurationProperties);

        this.applicationContext = applicationContext;
        this.camelContextConfigurations = camelContextConfigurations;
        this.configurationProperties = configurationProperties;

        Assert.state(CamelSpringTestHelper.getTestClass() != null, "test class must not be null");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        // only listen to context refresh of "my" applicationContext
        if (this.applicationContext.equals(applicationContext)) {

            CamelContext camelContext = event.getApplicationContext().getBean(CamelContext.class);

            // only add and start Camel if its stopped (initial state)
            if (camelContext.getStatus().isStopped()) {
                try {
                    LOG.debug("Adding active routes into CamelContext: {}", camelContext.getName());
                    addRoutes(camelContext);

                    for (CamelContextConfiguration camelContextConfiguration : camelContextConfigurations) {
                        LOG.debug("CamelContextConfiguration found. Invoking beforeApplicationStart: {}", camelContextConfiguration);
                        camelContextConfiguration.beforeApplicationStart(camelContext);
                    }

                    if (configurationProperties.isMainRunController()) {
                        LOG.info("Starting CamelMainRunController to ensure the main thread keeps running");
                        CamelMainRunController controller = new CamelMainRunController(applicationContext, camelContext);
                        // controller will start Camel
                        controller.start();
                    } else {
                        // start camel manually
                        maybeStart(camelContext);
                    }

                    for (CamelContextConfiguration camelContextConfiguration : camelContextConfigurations) {
                        LOG.debug("CamelContextConfiguration found. Invoking afterApplicationStart: {}", camelContextConfiguration);
                        camelContextConfiguration.afterApplicationStart(camelContext);
                    }
                } catch (Exception e) {
                    throw new CamelSpringBootInitializationException(e);
                }
            } else {
                LOG.debug("Camel already started, not adding routes.");
            }
        } else {
            LOG.debug("Ignore ContextRefreshedEvent: {}", event);
        }
    }

    /**
     * Adds routes into Camel context. If there is defined property '{@value TEST_CAMEL_INIT_ALL_ROUTES}' with true value
     * then all routes from Spring application context are added into Camel context otherwise only active routes
     * are added - those listed in annotation {@link ActiveRoutes}.
     */
    private void addRoutes(CamelContext camelContext) throws Exception {
        // check if all routes should be added
        boolean initAllRoutes = applicationContext.getEnvironment()
                .getProperty(TEST_CAMEL_INIT_ALL_ROUTES, Boolean.class, false);

        Set<Class> activeRoutesClasses = getActiveRoutes();
        for (RoutesBuilder routesBuilder : applicationContext.getBeansOfType(RoutesBuilder.class).values()) {
            // filter out abstract classes
            boolean abs = Modifier.isAbstract(routesBuilder.getClass().getModifiers());
            if (!abs) {
                try {
                    if (initAllRoutes) {
                        LOG.debug("Injecting following route into the CamelContext: {}", routesBuilder);
                        camelContext.addRoutes(routesBuilder);
                    } else {
                        for (Class routesClass : activeRoutesClasses) {
                            if (routesBuilder.getClass().isAssignableFrom(routesClass)) {
                                LOG.debug("Injecting following route into the CamelContext: {}", routesBuilder);
                                camelContext.addRoutes(routesBuilder);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new CamelSpringBootInitializationException(e);
                }
            }
        }
    }

    private void maybeStart(CamelContext camelContext) throws Exception {
        // for example from unit testing we want to start Camel later and not when Spring framework
        // publish a ContextRefreshedEvent
        boolean skip = "true".equalsIgnoreCase(System.getProperty("skipStartingCamelContext"));
        if (skip) {
            LOG.info("Skipping starting CamelContext as system property skipStartingCamelContext is set to be true.");
        } else {
            camelContext.start();
        }
    }

    /**
     * Gets set of active route definitions which should be added into Camel context.
     *
     * @return set of classes
     */
    private Set<Class> getActiveRoutes() {
        Set<Class> routeClasses = new HashSet<>();
        Class<ActiveRoutes> annotationType = ActiveRoutes.class;
        Class<?> testClass = CamelSpringTestHelper.getTestClass();

        Class<?> declaringClass = findAnnotationDeclaringClass(annotationType, testClass);
        if (declaringClass == null) {
            return routeClasses;
        }

        // get active routes from the class hierarchy
        while (declaringClass != null) {
            ActiveRoutes activeRoutes = declaringClass.getAnnotation(annotationType);
            routeClasses.addAll(Arrays.asList(activeRoutes.classes()));
            declaringClass = findAnnotationDeclaringClass(annotationType, declaringClass.getSuperclass());
        }

        return routeClasses;
    }
}

