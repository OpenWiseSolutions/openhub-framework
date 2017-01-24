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

package org.openhubframework.openhub.admin.web.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.ErrorPageFilter;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.admin.web.filter.RequestResponseLoggingFilter;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.modules.ErrorEnum;


/**
 * OpenHub admin console configuration.
 * <p/>
 * This class configures child context of {@link org.openhubframework.openhub.OpenHubApplication} as root context.
 *
 * @author Tomas Hanus
 * @see MvcConfig
 * @since 2.0
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = "org.openhubframework.openhub.admin",
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml"})
public class AdminConsoleContextConfiguration extends WebMvcConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AdminConsoleContextConfiguration.class);

    private static final String JAVAMELODY_URL = "/monitoring/javamelody";

    /*
 * The bean name for a DispatcherServlet that will be mapped to the root URL "/"
 */
    /*
    
     */
    public static final String DISPATCHER_SERVLET_BEAN_NAME = "adminConsoleDispatcherServlet";

    /*
     * The bean name for a ServletRegistrationBean for the DispatcherServlet "/"
     */
    public static final String DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME = "adminConsoleDispatcherServletRegistration";

    /**
     * Registers {@link RequestResponseLoggingFilter}.
     */
    @Bean
    public FilterRegistrationBean loggingRest() {
        LOG.info("REQ/RES logging initialization");

        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();
        filter.setLogUnsupportedContentType(true);

        return new FilterRegistrationBean(filter);
    }

    /**
     * Creates JavaMelody filter.
     */
    @Bean
    public FilterRegistrationBean monitoringJavaMelody() {
        LOG.info("JavaMelody initialization: " + JAVAMELODY_URL);

        FilterRegistrationBean registration = new FilterRegistrationBean(new MonitoringFilter());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("monitoring-path", JAVAMELODY_URL);
        initParams.put("disabled", "true");
        registration.setInitParameters(initParams);

        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<SessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }

    /**
     * Defines localized messages for admin GUI.
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * Defines error codes catalogue.
     */
    @Bean
    public Map<String, ErrorExtEnum[]> errorCodesCatalog() {
        Map<String, ErrorExtEnum[]> map = new HashMap<>();
        map.put("core", ErrorEnum.values());
        return map;
    }

    // ----------------------------------------------
    // reason of this code snippet: http://stackoverflow.com/questions/30170586/how-to-disable-errorpagefilter-in-spring-boot

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
    // ----------------------------------------------    
}
