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

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;

import org.openhubframework.openhub.admin.web.filter.RequestResponseLoggingFilter;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter;
import org.openhubframework.openhub.modules.ErrorEnum;


/**
 * OpenHub admin console configuration.
 * <p/>
 * This class configures child context of {@link org.openhubframework.openhub.OpenHubApplication} as root context.
 *
 * @author Tomas Hanus
 * @see AdminConsoleMvcConfig
 * @since 2.0
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = "org.openhubframework.openhub.admin",
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
//@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml"})
public class AdminConsoleContextConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AdminConsoleContextConfig.class);

    private static final String JAVAMELODY_URL = "/monitoring/javamelody";

    /**
     * The bean name for a {@code DispatcherServlet} that will be mapped to the root URL
     * {@link org.openhubframework.openhub.api.route.RouteConstants#WEB_URI_PREFIX_MAPPING}.
     */
    public static final String DISPATCHER_SERVLET_BEAN_NAME = "adminConsoleDispatcher";

    /**
     * The ID (bean name) for a {@code ServletRegistrationBean} of {@link #DISPATCHER_SERVLET_BEAN_NAME}.
     */
    public static final String CONTEXT_ID = "AdminConsoleApplicationContext";

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
     * Creates {@link MonitoringFilter} filter.
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
        // add session listener for JavaMelody
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }

    /**
     * Defines localized messages for admin console.
     */
    @Bean
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

    /**
     * Registers URL prefix.
     */
    @Bean(name = CONTEXT_ID)
    public ServletRegistrationBean dispatcherWebRegistration(
            ConfigurableApplicationContext context, 
            DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        context.setId(CONTEXT_ID);
        registration.addUrlMappings(WEB_URI_PREFIX + "*");
        return registration;
    }

    /**
     * Configures servlet for HTTP communication.
     */
    @Bean
    public ServletRegistrationBean camelHttpServlet() {
        CamelHttpTransportServlet servlet = new CamelHttpTransportServlet();
        servlet.setServletName(RouteConstants.CAMEL_SERVLET);

        ServletRegistrationBean bean = new ServletRegistrationBean(servlet,
                RouteConstants.HTTP_URI_PREFIX + "*");
        bean.setName(servlet.getServletName());
        return bean;
    }

    @Bean
    public ServletRegistrationBean dispatcherWsRegistration(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        return new ServletRegistrationBean(servlet, RouteConstants.WS_URI_PREFIX + "*");
    }
    
    @Bean(name = MessageDispatcherServlet.DEFAULT_MESSAGE_RECEIVER_HANDLER_ADAPTER_BEAN_NAME)
    public WebServiceMessageReceiverHandlerAdapter messageReceiverHandlerAdapter() {
        return new ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter();
    }
}
