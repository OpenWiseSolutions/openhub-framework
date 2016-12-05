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

package org.openhubframework.openhub.admin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.ErrorPageFilter;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.admin.web.filter.RequestResponseLoggingFilter;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.common.log.LogContextFilter;
import org.openhubframework.openhub.modules.ErrorEnum;


/**
 * OpenHub application configuration.
 * <p/>
 * This class configures root Spring context. Two child contexts are created:
 * <ul>
 *     <li>Spring MVC web context
 *     <li>Spring WS context
 * </ul>
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 1.1
 */
//@SpringBootApplication
@EnableAutoConfiguration
//@EnableConfigurationProperties
@ComponentScan(basePackages = {"org.openhubframework.openhub.core.common.route",
        "org.openhubframework.openhub.common.datasource",
//        "org.openhubframework.openhub.core.common.contextcall",
//        "org.openhubframework.openhub.core.common.directcall",
//        "org.openhubframework.openhub.core.common.version",
//        "org.openhubframework.openhub.core.common.asynch.stop",
//        "org.openhubframework.openhub.core.common.asynch.msg",
//        "org.openhubframework.openhub.core.reqres",
//        "org.openhubframework.openhub.core.common.file",
//        "org.openhubframework.openhub.core.common.dao",
//        "org.openhubframework.openhub.core.persistence",
//        "org.openhubframework.openhub.core.conf",
        "org.openhubframework.openhub.core",
        "org.openhubframework.openhub.modules",
        "org.openhubframework.openhub.admin"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml",
        "classpath:rootApplicationContext.xml",
        "classpath:rootSecurity.xml",
        "classpath:spring-admin-mvc-servlet.xml",
        "classpath:spring-ws-servlet.xml",
        "classpath:sp_h2.xml"})
public class OpenHubApplication extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHubApplication.class);

    private static final String JAVAMELODY_URL = "/monitoring/javamelody";

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
     * Registers URL prefix.
     */
    @Bean
    public ServletRegistrationBean dispatcherWebRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        registration.addUrlMappings("/web/admin/*");
        return registration;
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
    public Filter logContextFilter() {
        return new LogContextFilter();
    }


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

    @Bean
    public Map<String, ErrorExtEnum[]> errorCodesCatalog() {
          Map<String, ErrorExtEnum[]> map = new HashMap<>();
          map.put("core", ErrorEnum.values());
          return map;
      }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        // add session listener for JavaMelody
        container.addListener(SessionListener.class);

        super.onStartup(container);
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

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(OpenHubApplication.class)
//                .child(OpenHubWebApplication.class).web(true)
                .run(args);
    }
}
