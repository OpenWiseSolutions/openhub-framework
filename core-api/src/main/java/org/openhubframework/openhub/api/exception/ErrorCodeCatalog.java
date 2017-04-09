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

package org.openhubframework.openhub.api.exception;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;


/**
 * Marker annotation to describe details about {@link ErrorExtEnum}.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ErrorCodeCatalog {

    /**
     * The value may indicate a suggestion for a logical catalog name, for example CRM, Billing and so on.
     *
     * @return the suggested catalog name, if any
     */
    @AliasFor("name")
    String value() default "";

    /**
     * @see #value()
     */
    @AliasFor("value")
    String name() default "";
}
