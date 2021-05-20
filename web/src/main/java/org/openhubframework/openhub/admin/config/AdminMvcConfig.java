/*
 * Copyright 2016-2021 the original author or authors.
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

package org.openhubframework.openhub.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.util.StringUtils.hasText;


/**
 * Web MVC configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
// note: If you want to keep Spring Boot MVC features, and you just want to add additional MVC configuration 
// (interceptors, formatters, view controllers etc.) you can add your own @Bean of type WebMvcConfigurerAdapter, 
// but without @EnableWebMvc.
// WebMvcConfigurer interface, starting with Spring 5, contains default implementations for all its methods
public class AdminMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AdminMvcConfig.class);

    @Autowired
    private ResourceProperties resourceProperties = new ResourceProperties();

    @Autowired
    private WebMvcProperties mvcProperties = new WebMvcProperties();

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        final String welcomePagePath = getWelcomePagePath(mvcProperties);
        registry.addViewController(welcomePagePath).setViewName("forward:index.html");
    }

    /**
     * Gets welcome page path basically from {@link WebMvcProperties#getStaticPathPattern()} configuration
     * when pattern expression {@code "*"} is removed, for example static path pattern defines {@code /console/**}
     * and welcome page path will be resolved as {@code /console/}.
     *
     * @param mvcProperties as configuration for
     * @return welcome page path on which static resource {@code index.html} will be loaded
     */
    protected String getWelcomePagePath(WebMvcProperties mvcProperties) {
        if (hasText(mvcProperties.getStaticPathPattern())) {
            return mvcProperties.getStaticPathPattern().replace("*", "");
        }

        // default fallback
        return "/";
    }
}
