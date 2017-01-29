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

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.ErrorPageFilter;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;

import org.openhubframework.openhub.admin.web.config.AdminConsoleContextConfig;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.common.log.LogContextFilter;
import org.openhubframework.openhub.config.CamelRoutesConfig;
import org.openhubframework.openhub.config.WebSecurityConfig;
import org.openhubframework.openhub.core.config.CamelConfig;
import org.openhubframework.openhub.core.config.JpaConfig;
import org.openhubframework.openhub.core.config.WebServiceConfig;


/**
 * OpenHub application configuration.
 * <p/>
 * This class configures root Spring context.
 *
 * @author Petr Juza
 * @see AdminConsoleContextConfig
 * @see CamelRoutesConfig
 * @see WebSecurityConfig
 * @see CamelConfig
 * @see WebServiceConfig
 * @see JpaConfig
 * @since 2.0
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
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
@ImportResource("classpath:sp_camelContext.xml")
@PropertySource(value = {"classpath:/extensions.cfg"})
public class OpenHubApplication extends SpringBootServletInitializer {
    
    /**
     * Sets up filter for adding context information to logging.
     */
    @Bean
    public Filter logContextFilter() {
        return new LogContextFilter();
    }

    // ----------------------------------------------
    // reason of this code snippet: http://stackoverflow
    // .com/questions/30170586/how-to-disable-errorpagefilter-in-spring-boot
    
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


    @Override
    protected SpringApplicationBuilder createSpringApplicationBuilder() {
        return createOpenHubApplicationBuilder();
    }

    public static void main(String[] args) throws Exception {
        createOpenHubApplicationBuilder()
                .run(args);
    }

    private static SpringApplicationBuilder createOpenHubApplicationBuilder() {
        return new SpringApplicationBuilder(OpenHubApplication.class)
               .child(AdminConsoleContextConfig.class);
    }
    
}
