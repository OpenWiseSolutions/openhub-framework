/*
 * Copyright 2016-2017 the original author or authors.
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

package org.openhubframework.openhub.web.config;

import static org.springframework.util.StringUtils.hasText;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.WebMvcProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


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
public class AdminMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AdminMvcConfig.class);

    @Autowired
    private ResourceProperties resourceProperties = new ResourceProperties();

    @Autowired
    private WebMvcProperties mvcProperties = new WebMvcProperties();

    @Autowired
    private MappingJackson2HttpMessageConverter jackson;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //TODO (thanus, 27/01/2017, TASK: OHFJIRA-4) can be removed after new admin console will be developed 
        registry.addResourceHandler("/**")
                .addResourceLocations("/")
                .setCachePeriod(0);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        Resource page = this.resourceProperties.getWelcomePage();
        if (page != null) {
            final String welcomePagePath = getWelcomePagePath(mvcProperties);
            logger.info("Adding welcome page ({}): {}", page, welcomePagePath);
            registry.addViewController(welcomePagePath).setViewName("forward:index.html");
        }
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

    /**
     * Configures HTTP converters, overrides default list of converters in Spring MVC.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // add default converters
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<>());
        converters.add(new AllEncompassingFormHttpMessageConverter());

        converters.add(jackson);

        super.configureMessageConverters(converters);
    }
}   
