/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.web.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX_MAPPING;

import java.util.Locale;
import java.util.Map;
import javax.servlet.DispatcherType;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.web.RequestResponseLoggingFilter;
import org.openhubframework.openhub.web.common.CorsProperties;
import org.openhubframework.openhub.web.common.JavaMelodyConfigurationProperties;


/**
 * OpenHub web configuration.
 * <p/>
 * This class configures child context of {@link org.openhubframework.openhub.OpenHubApplication root context}.
 *
 * @author Tomas Hanus
 * @see WebConfigurer
 * @see AdminMvcConfig
 * @since 2.0
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = {
        "org.openhubframework.openhub.web",
        "org.openhubframework.openhub.admin"
},
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml"})
public class WebContextConfig {

    private static final Logger LOG = LoggerFactory.getLogger(WebContextConfig.class);

    /**
     * The ID of web context.
     */
    public static final String CONTEXT_ID = "WebContext";

    @Autowired
    private JavaMelodyConfigurationProperties javaMelodyProps;

    @Autowired
    private CorsProperties corsConfiguration;

    /**
     * Registers {@link RequestResponseLoggingFilter}.
     */
    @Bean
    public FilterRegistrationBean loggingRest() {
        LOG.info("REQ/RES logging initialization");

        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();
        filter.setLogUnsupportedContentType(false);
        final FilterRegistrationBean bean = new FilterRegistrationBean(filter);
        // we use logging filter only for administration endpoints to avoid duplication log events
        bean.addUrlPatterns(WEB_URI_PREFIX_MAPPING);
        
        return bean;
    }

    /**
     * Creates {@link MonitoringFilter} filter.
     */
    @Bean
    public FilterRegistrationBean monitoringJavaMelody() {
        LOG.info("JavaMelody initialization: " + javaMelodyProps.getInitParameters());

        final MonitoringFilter filter = new MonitoringFilter();
        filter.setApplicationType("OpenHub");
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setAsyncSupported(true);
        registration.setName("javamelody");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);

        // Set the initialization parameter for the monitoring filter.
        for (final Map.Entry<String, String> parameter : javaMelodyProps.getInitParameters().entrySet()) {
            registration.addInitParameter(parameter.getKey(), parameter.getValue());
        }

        // Set the URL patterns to activate the monitoring filter for.
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<SessionListener> sessionListener() {
        // add session listener for JavaMelody
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }

    /**
     * Defines localized messages for admin console.
     */
    //TODO PJUZA configure MessageSource as in WIS ?
    @Bean
    @ConditionalOnMissingBean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Defines default {@link LocaleResolver}.
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * Configures servlet for admin interface of OpenHub.
     *
     * @param context           current context
     * @param dispatcherServlet as actually registered dispatcher
     * @return registration bean of {@link DispatcherServlet} to handling {@link RouteConstants#WEB_URI_PREFIX_MAPPING}.
     */
    @Bean(name = CONTEXT_ID)
    @ConditionalOnMissingBean(name = CONTEXT_ID)
    public ServletRegistrationBean adminServlet(
            ConfigurableApplicationContext context,
            DispatcherServlet dispatcherServlet) {

        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        // sets corresponding ID (name) of web context
        context.setId(CONTEXT_ID);
        bean.addUrlMappings(WEB_URI_PREFIX_MAPPING);

        return bean;
    }

    @ConditionalOnProperty(value = CorsProperties.CORS_ENABLED)
    @Bean
    public FilterRegistrationBean corsFilter() {
        // it could be used also as http://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/htmlsingle/#boot-features-cors
        // but filter approach has more options
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CorsProperties.ALL_PATHS, corsConfiguration);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}
