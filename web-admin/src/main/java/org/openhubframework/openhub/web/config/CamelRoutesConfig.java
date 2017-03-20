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

package org.openhubframework.openhub.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import org.openhubframework.openhub.core.common.route.RouteBeanNameGenerator;
import org.openhubframework.openhub.core.common.spring.SystemExcludeRegexPatternTypeFilter;
import org.openhubframework.openhub.core.common.spring.SystemIncludeRegexPatternTypeFilter;


/**
 * Camel routes configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
@ComponentScan(basePackages = {"org.openhubframework.openhub.core", "org.openhubframework.openhub.modules"},
        useDefaultFilters = false,
        nameGenerator = RouteBeanNameGenerator.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = SystemIncludeRegexPatternTypeFilter.class),
        excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = SystemExcludeRegexPatternTypeFilter.class))
public class CamelRoutesConfig {
}
