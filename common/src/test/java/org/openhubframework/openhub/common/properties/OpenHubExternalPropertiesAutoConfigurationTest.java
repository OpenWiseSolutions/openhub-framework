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

package org.openhubframework.openhub.common.properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.PathResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;


/**
 * Test that {@code openhub.properties} is properly loaded and used in right location.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public class OpenHubExternalPropertiesAutoConfigurationTest {

    @Test
    public void test() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, URISyntaxException {

        final URL resource = OpenHubExternalPropertiesAutoConfiguration.class.getClassLoader()
                .getResource("org/openhubframework/openhub/common/properties/openhub.properties");
        notNull(resource, "Resource must not be null");
        URI wisPropertiesPath = resource.toURI();
        final Path virtualRootPath = Paths.get(wisPropertiesPath).getParent();

        ClassLoader current = Thread.currentThread().getContextClassLoader();

        try {

            // prepare special class loader with special application.properties and openhub.properties.
            ClassLoader loader = new ClassLoader(OpenHubExternalPropertiesAutoConfiguration.class.getClassLoader()) {
                @Override
                protected URL findResource(String name) {
                    try {
                        PathResource resource = new PathResource(virtualRootPath.resolve(name));
                        // FileSystemResource resource = new FileSystemResource(new URL(virtualRootPath + name).getFile());
                        if (resource.isReadable()) {
                            return resource.getURL();
                        }
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                }
            };
            Thread.currentThread().setContextClassLoader(loader);

            Class<?> springApplicationClass = loader.loadClass(SpringApplication.class.getName());
            Class<?> testApplicationClass = loader
                    .loadClass(TestApplication.class.getName());
            Object instance = springApplicationClass.getConstructor(Object[].class)
                    .newInstance(new Object[] { new Object[] { testApplicationClass } });
            // without web
            ReflectionTestUtils.setField(instance, "webEnvironment", false);

            @SuppressWarnings("resource")
            ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) ReflectionUtils
                    .findMethod(springApplicationClass, "run", String[].class)
                    .invoke(instance, new Object[] { new String[] {} });

            TestProperties testProperties = ctx.getBean(TestProperties.class);

            // openhub is defined in openhub.properties, application in application.properties
            assertThat(testProperties.getName(), is("openhub"));

        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableConfigurationProperties
    @EnableSpringConfigured
    static class TestApplication extends SpringApplication {

        @Bean
        public TestProperties testProperties() {
            return new TestProperties();
        }
    }

    @ConfigurationProperties(prefix = "openhub.test")
    static class TestProperties {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}