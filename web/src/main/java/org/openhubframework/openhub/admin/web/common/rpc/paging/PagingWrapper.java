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

package org.openhubframework.openhub.admin.web.common.rpc.paging;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.Assert;

import org.openhubframework.openhub.admin.web.common.rpc.paging.metadata.OffsetBasedPagingMetadata;
import org.openhubframework.openhub.admin.web.common.rpc.paging.metadata.PagingMetadata;


/**
 * A page is a sub-list of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 * <p>
 * Default implementation of paging metadata is {@link OffsetBasedPagingMetadata} provided by
 * {@link #withMetadata(Page)}, but it is possible to provide custom {@link PagingMetadata} implementation
 * such as cursor-based and so on. If result set represents only non-paging list, it should be created with default
 * constructor {@link #PagingWrapper(Page)}, where paging info is not provided.
 * </p>
 * <p>
 * Example of usage:
 * <pre>
 *      Page<Order> foundOrders = service.findOrders(request);
 *      Page<OrderRpc> resultList = foundOrders.map(toRpc());
 *
 *      return PagingWrapper.withMetadata(resultList);
 * </pre>
 * </p>
 *
 * @param <T> - type of item in page result
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
public class PagingWrapper<T> {

    private final Page<T> data;

    private final Optional<PagingMetadata>paging;

    /**
     * Constructor of {@code PagingWrapper}.
     *
     * @param data the content of this page, must not be {@literal null}.
     */
    public PagingWrapper(Page<T> data) {
        Assert.notNull(data, "Data must not be null");

        this.data = data;
        this.paging = Optional.empty();
    }

    /**
     * Constructor of {@code PagingWrapper} which provides {@link OffsetBasedPagingMetadata} implementation of paging
     * metadata.
     *
     * @param data the content of this page, must not be {@literal null}.
     */
    public static <T> PagingWrapper<T> withMetadata(Page<T> data) {
        Assert.notNull(data, "Page must not be null");

        return new PagingWrapper<>(data, new OffsetBasedPagingMetadata<>(data));
    }

    /**
     * Constructor of {@code PagingWrapper} <strong>without</strong> paging metadata. Instead of it provided only data
     * element which contains {@code list}.
     *
     * @param list of data, must not be {@literal null}.
     */
    public static <T> PagingWrapper<T> withoutMetadata(List<T> list) {
        Assert.notNull(list, "List must not be null");

        return new PagingWrapper<>(new PageImpl<>(list));
    }

    /**
     * Constructor of {@code PagingWrapper} that allows to define custom paging metadata.
     * @param data the content of this page, must not be {@literal null}.
     * @param paging the custom paging metadata, must not be {@literal null}.
     */
    public PagingWrapper(Page<T> data, PagingMetadata paging) {
        Assert.notNull(data, "Data must not be null");
        Assert.notNull(paging, "PagingMetadata must not be null");

        this.data = data;
        this.paging = Optional.of(paging);
    }

    /**
     * Gets the content of the page.
     *
     * @return list of items
     */
    public List<T> getData() {
        return data.getContent();
    }

    /**
     * Gets the paging metadata.
     *
     * @return metadata of the page
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public PagingMetadata getPaging() {
        return paging.orElse(null);
    }
}
