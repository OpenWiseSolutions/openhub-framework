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

package org.openhubframework.openhub.config;

import java.util.Map;
import javax.servlet.DispatcherType;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import org.openhubframework.openhub.admin.config.WebAdminConfigurer;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.web.common.CorsProperties;
import org.openhubframework.openhub.web.common.JavaMelodyConfigurationProperties;
import org.openhubframework.openhub.web.config.WebServiceConfigurer;


/**
 * Web configurer which is responsible for configuration of web runtime layer of OpenHub.
 *
 * @author Tomas Hanus
 * @see WebServiceConfigurer
 * @see WebAdminConfigurer
 * @since 2.0
 */
@ComponentScan(basePackages = {
        "org.openhubframework.openhub.admin",
        "org.openhubframework.openhub.web"
},
        excludeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml"})
public class WebConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfigurer.class);

    @Autowired
    private JavaMelodyConfigurationProperties javaMelodyProps;

    @Autowired
    private CorsProperties corsConfiguration;

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
        registration.setEnabled(javaMelodyProps.isEnabled());

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
