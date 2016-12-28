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

package org.openhubframework.openhub.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;


/**
 * Special marker annotation, to indicate that the bean should be only initialized by Spring Boot Auto Configuration.
 *
 * <p>
 * The application should be configured the way that the bean annotated with this annotation should be ignored when doing component classpath
 * scanning.
 *
 * <p>
 * Very likely, if you have bean defined in {@literal spring.factories} under
 * {@literal org.springframework.boot.autoconfigure.EnableAutoConfiguration} property you want to have bean annotated with this annotation.
 * Note that the bean should not be also annotated with {@link Configuration} annotation.
 *
 * @author Tomas Hanus
 * @since 2.0
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html">Spring Boot
 * Auto-configuration</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
public @interface AutoConfiguration {
}
