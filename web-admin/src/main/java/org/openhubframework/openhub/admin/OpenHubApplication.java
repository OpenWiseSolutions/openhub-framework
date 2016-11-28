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
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import org.openhubframework.openhub.admin.web.filter.RequestResponseLoggingFilter;
import org.openhubframework.openhub.common.log.LogContextFilter;


/**
 * OpenHub application configuration.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 1.1
 */
//@SpringBootApplication
//@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
//@EnableConfigurationProperties
//@ComponentScan(basePackages = "com.openwise.wiseporter",
//        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class))
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml",
        "classpath:rootApplicationContext.xml",
        "classpath:rootSecurity.xml",
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

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        // add session listener for JavaMelody
        container.addListener(SessionListener.class);

        super.onStartup(container);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(OpenHubApplication.class, args);
    }
}
