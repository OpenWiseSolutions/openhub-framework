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

package org.openhubframework.openhub.admin.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Web MVC configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
//note: If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc.
// If you want to keep Spring Boot MVC features, and you just want to add additional MVC configuration (interceptors,
// formatters, view controllers etc.) you can add your own @Bean of type WebMvcConfigurerAdapter, but without @EnableWebMvc.
public class MvcConfig extends WebMvcConfigurerAdapter {

}
