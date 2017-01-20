/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.configuration;

import java.lang.annotation.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

/**
 * Marks a configurable {@link ConfigurationItem parameter} as being eligible for system configuration.
 * <p>
 * Annotation is used to inject configuration parameter wrapper that encapsulates system property and resource provider.
 * <p>
 * Example how to use:
 * <pre>
 *   {@link ConfigurableValue @ConfigurableValue}{@code ("ohf.asynch.confirmation.intervalSec")
 *    private ConfigurationItem<Integer> interval;}
 * </pre>
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Qualifier
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurableValue {

    /**
     * Alias for {@link #key}.
     * <p>Intended to be used instead of {@link #key} when {@link #key}
     * is not declared &mdash; for example: {@code @ConfigurableValue("key.name")} instead of
     * {@code @ConfigurableValue(key = "key.name")}.
     *
     * @return the key name of system parameter
     * @see #key()
     */
    @AliasFor("key")
    String value() default "";

    /**
     * The key as ID of system parameter to inject.
     *
     * @return the key name of system parameter
     * @see #value()
     */
    @AliasFor("value")
    String key() default "";

    /**
     * Declares whether the annotated system parameter is required.
     * <p>
     * Defaults is {@code true}.
     *
     * @return <code>true</code> if the parameter is required. <code>false</code> otherwise.
     */
    boolean required() default true;
}
