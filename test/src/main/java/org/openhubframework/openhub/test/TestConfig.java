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

package org.openhubframework.openhub.test;

import static org.openhubframework.openhub.api.route.RouteConstants.WS_AUTH_POLICY;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import org.openhubframework.openhub.common.AutoConfiguration;


/**
 * Basic configuration for Spring Boot tests.
 * <p>
 * Usage in your test classes:
 * <pre class="code">
        &#064;SpringApplicationConfiguration(classes = {TestConfig.class, TestAopConfig.class})
 * </pre>
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@Configuration
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class, MultipartAutoConfiguration.class,
        JacksonAutoConfiguration.class, HttpEncodingAutoConfiguration.class})
@ComponentScan(basePackages = {"org.openhubframework.openhub.common",
        "org.openhubframework.openhub.core", "org.openhubframework.openhub.test"},
        excludeFilters = @ComponentScan.Filter(
                value = {RestController.class, Controller.class, AutoConfiguration.class},
                type = FilterType.ANNOTATION))
@EnableConfigurationProperties
public class TestConfig {

    @Bean(name = WS_AUTH_POLICY)
    public DummyPolicy dummyPolicy() {
        return new DummyPolicy();
    }
}

