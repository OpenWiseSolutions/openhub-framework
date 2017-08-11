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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.spring.boot.RoutesCollector;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.test.route.ActiveRoutesCollector;


/**
 * Basic configuration for Spring Boot tests.
 * <p>
 * Usage in your test classes:
 * <pre class="code">
        &#064;SpringApplicationConfiguration(classes = {TestConfig.class, TestAopConfig.class})
 * </pre>
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
@EnableAutoConfiguration(
        excludeName = {"org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"})
@ComponentScan(basePackages = {"org.openhubframework.openhub.common",
        "org.openhubframework.openhub.core", "org.openhubframework.openhub.test"},
        excludeFilters = @ComponentScan.Filter(
                value = {RestController.class, Controller.class, AutoConfiguration.class},
                type = FilterType.ANNOTATION))
@EnableConfigurationProperties
@EnableSpringConfigured
@EnableAspectJAutoProxy
public class TestConfig {

    @Bean(name = WS_AUTH_POLICY)
    public DummyPolicy dummyPolicy() {
        return new DummyPolicy();
    }

    /**
     * Implementation of {@link RoutesCollector} for tests that adds only active routes into camel context.
     */
    @Bean
    public ActiveRoutesCollector activeRoutesCollector(ApplicationContext ctx, CamelConfigurationProperties config) {
        Assert.notNull(config, "config must not be null");

        Collection<CamelContextConfiguration> configurations
                = ctx.getBeansOfType(CamelContextConfiguration.class).values();
        return new ActiveRoutesCollector(ctx, new ArrayList<>(configurations), config);
    }

    @Bean
    public CamelContextConfiguration testContextConfiguration() {
      return new CamelContextConfiguration() {

          @Override
          public void beforeApplicationStart(CamelContext camelContext) {
              camelContext.getShutdownStrategy().setTimeout(1); // no shutdown timeout:
              camelContext.getShutdownStrategy().setTimeUnit(TimeUnit.NANOSECONDS);
              camelContext.getShutdownStrategy().setShutdownNowOnTimeout(true); // no pending exchanges
          }

          @Override
          public void afterApplicationStart(CamelContext camelContext) {
              // nothing to set
          }
      };
    }
}

