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

package org.openhubframework.openhub.admin.web.common.rpc.paging.metadata;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;


/**
 * Offset-based paging metadata descriptor.
 * <p>
 * It generates result for example in JSON:
 * <pre>
 * {
 *      "first": true,
 *      "last": true,
 *      "number": 1,
 *      "size": 20,
 *      "totalPages": 1,
 *      "totalElements": 1,
 *      "sort": [
 *          "null"
 *      ],
 *      "numberOfElements": 1
 * }
 * </pre>
 * </p>
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
@Configurable
public class OffsetBasedPagingMetadata<T> implements PagingMetadata {

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableResolver;
    private final Page<T> data;

    public OffsetBasedPagingMetadata(Page<T> data) {
        this.data = data;
    }

    /**
     * Returns whether the current {@link Page} is the first one.
     */
    public boolean isFirst() {
        return data.isFirst();
    }

    /**
     * Returns whether the current {@link Page} is the last one.
     */
    public boolean isLast() {
        return data.isLast();
    }

    /**
     * Returns the number of the current {@link Page}. Is always non-negative.
     *
     * @return the number of the current {@link Page}.
     */
    public int getNumber() {
        boolean oneBased;

        try {
            // only via reflection is possible to read state of one-based config
            // spring by default converts one-based attributes into zero-based for inner representation, but
            // for REST API we convert this attribute into one-based form
            oneBased = (Boolean)FieldUtils.readField(pageableResolver, "oneIndexedParameters", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return oneBased ? data.getNumber() + 1 : data.getNumber();
    }

    /**
     * Returns the size of the {@link Page}.
     *
     * @return the size of the {@link Page}.
     */
    public int getSize() {
        return data.getSize();
    }

    /**
     * Returns the number of elements currently on this {@link Page}.
     *
     * @return the number of elements currently on this {@link Page}.
     */
    public int getNumberOfElements() {
        return data.getContent().size();
    }

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    public int getTotalPages() {
        return data.getTotalPages();
    }

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    public long getTotalElements() {
        return data.getTotalElements();
    }

    /**
     * Returns the sorting parameters for the {@link Page}.
     */
    public Sort getSort() {
        return data.getSort();
    }
}
