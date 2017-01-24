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

package org.openhubframework.openhub;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;

import javax.servlet.Filter;

import net.bull.javamelody.SessionListener;
import org.openhubframework.openhub.admin.web.config.AdminConsoleContextConfiguration;
import org.openhubframework.openhub.admin.web.config.MvcConfig;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.common.log.LogContextFilter;
import org.openhubframework.openhub.config.CamelRoutesConfig;
import org.openhubframework.openhub.config.WebSecurityConfig;
import org.openhubframework.openhub.core.config.CamelConfig;
import org.openhubframework.openhub.core.config.JpaConfig;
import org.openhubframework.openhub.core.config.WebServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * OpenHub application configuration.
 * <p/>
 * This class configures root Spring context. One child context for Spring MVC is created.
 *
 * @author Petr Juza
 * @see CamelRoutesConfig
 * @see MvcConfig
 * @see WebSecurityConfig
 * @see CamelConfig
 * @see WebServiceConfig
 * @see JpaConfig
 * @since 2.0
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication(exclude = DispatcherServletAutoConfiguration.class)
// note: all routes with @CamelConfiguration are configured in CamelRoutesConfig
@ComponentScan(basePackages = {
        "org.openhubframework.openhub.common",
        "org.openhubframework.openhub.core",
        "org.openhubframework.openhub.modules",
        "org.openhubframework.openhub.config"
},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = CamelConfiguration.class)
        })
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml", "classpath:sp_camelContext.xml"})
@PropertySource(value = {"classpath:/extensions.cfg"})
public class OpenHubApplication extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHubApplication.class);

    @Autowired
    private ApplicationContext rootContext;

    /**
     * Sets up filter for adding context information to logging.
     */
    @Bean
    public Filter logContextFilter() {
        return new LogContextFilter();
    }

/*    @Bean
    public DispatcherServlet adminConsoleDispatcherServlet() {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.getServletContext().addListener(new SessionListener());
        return servlet;
    }*/
    
    /**
     * Create admin console dispatcher servlet.
     *
     * @return {@link ServletRegistrationBean} for admin console.
     */
    @Bean
    public ServletRegistrationBean adminConsoleServletRegistrationBean() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setParent(rootContext);
        context.setDisplayName("Admin Console Application Context");
        context.setId("AdminConsoleApplicationContext");
        context.register(AdminConsoleContextConfiguration.class);
        dispatcherServlet.setApplicationContext(context);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, WEB_URI_PREFIX + "*");
        servletRegistrationBean.setName("adminConsoleDispatcherServlet");
        servletRegistrationBean.setLoadOnStartup(1);

        LOG.info("Child {}: initialization completed", context.getId());

        return servletRegistrationBean;
    }

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(OpenHubApplication.class)
                .run(args);
    }
}
