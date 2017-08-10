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

package org.openhubframework.openhub.test.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.test.AbstractTest;


/**
 * Test configuration of REST web layer.
 * <p>
 * Inherited classes must define {@link ComponentScan @ComponentScan} for specified module.
 * <p>
 * Recommended way for testing REST API is via {@link MockMvc}. It is possible to implement abstract
 * {@link AbstractRestTest test skeleton} or implement similar own-customized parent.
 *
 * @author Petr Juza
 * @since 2.0
 */
@EnableAutoConfiguration(
        excludeName = {"org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"})
@EnableConfigurationProperties
@WebAppConfiguration
public class TestRestConfig extends WebMvcConfigurerAdapter {

    /**
     * Creates bean with default {@link LocaleResolver}.
     */
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        SessionLocaleResolver result = new SessionLocaleResolver();
        result.setDefaultLocale(AbstractTest.DEFAULT_LOCALE);

        return result;
    }
}
