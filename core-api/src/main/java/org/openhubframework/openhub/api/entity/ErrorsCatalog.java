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

package org.openhubframework.openhub.api.entity;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.exception.ErrorCodeCatalog;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;


/**
 * Error catalog entity that represents catalog and values of error codes.
 *
 * @author Tomas Hanus
 * @since 2.0
 * @see #resolveName(Class)
 * @see ErrorCodeCatalog
 */
public class ErrorsCatalog {

    private final String name;
    private final ErrorExtEnum[] codes;

    /**
     * Default constructor to create error catalog based upon registered error codes.
     *
     * @param codes as error code entries
     * @throws IllegalArgumentException if {@code codes} does not contain some entry
     * @see ErrorExtEnum
     */
    public ErrorsCatalog(ErrorExtEnum[] codes) throws IllegalArgumentException {
        Assert.isTrue(codes.length > 0, "");
        this.name = resolveName(codes[0].getClass());
        this.codes = codes;
    }

    /**
     * Constructor to create {@link ErrorsCatalog} based upon name and error code entries.
     *
     * @param name as name of catalog
     * @param codes as error code entries
     * @throws IllegalArgumentException if {@code name} is empty
     */
    public ErrorsCatalog(String name, ErrorExtEnum[] codes) throws IllegalArgumentException {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
        this.codes = codes;
    }

    /**
     * Constructor to create {@link ErrorsCatalog} based upon type and error code entries.
     * 
     * @param type of error code
     * @param codes as entries of error codes
     * @see #resolveName(Class) 
     */
    public ErrorsCatalog(Class<? extends ErrorExtEnum> type, ErrorExtEnum[] codes) {
        this.name = resolveName(type);
        this.codes = codes;
    }

    /**
     * Get name of error catalog.
     *
     * @return catalog name
     */
    public String getName() {
        return name;
    }

    /**
     * Get error codes of catalog.
     *
     * @return error catalog entries
     */
    public ErrorExtEnum[] getCodes() {
        return codes;
    }

    /**
     * Resolves name of catalog based upon error catalog entry.
     *
     * @param clazz that represents type of error catalog entry
     * @return name of catalog
     */
    public String resolveName(Class clazz) throws IllegalArgumentException {
        final ErrorCodeCatalog annotation = AnnotationUtils.findAnnotation(clazz, ErrorCodeCatalog.class);

        if (annotation != null) {
            return annotation.value();
        } else {
            return clazz.getClass().getSimpleName();
        }
    }
}
