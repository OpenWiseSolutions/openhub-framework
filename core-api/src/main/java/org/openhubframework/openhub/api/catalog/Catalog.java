/*
 * Copyright 2012-2017 the original author or authors.
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

package org.openhubframework.openhub.api.catalog;

import java.util.List;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;


/**
 * Contract for OpenHub catalogs. All catalogs that should be available through
 * catalog API, should be beans implementing this interface.
 * Note: designed to be used with helper annotation {@link CatalogComponent}.
 *
 * @author Karel Kovarik
 * @since 2.0
 * @see CatalogComponent
 */
public interface Catalog {

    /**
     * Get all entries in catalog.
     */
    List<CatalogEntry> getEntries();

    /**
     * Resolve catalog name. Default implementation gets name from CatalogComponent
     * annotation, if not found, class simpleName is used.
     */
    default String getName() {
        final CatalogComponent annotation = AnnotationUtils.findAnnotation(
                AopProxyUtils.ultimateTargetClass(this), CatalogComponent.class);

        if (annotation != null) {
            return annotation.value();
        } else {
            return getClass().getSimpleName();
        }
    }
}
