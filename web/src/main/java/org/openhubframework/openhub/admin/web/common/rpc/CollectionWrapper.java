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

package org.openhubframework.openhub.admin.web.common.rpc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;


/**
 * Collection wrapper of items.
 *
 * @param <T> - type of item in collection result
 * @author Tomas Hanus
 * @since 2.0
 */
public class CollectionWrapper<T> {

    private final List<T> data;

    /**
     * Constructor of {@code CollectionJson}.
     *
     * @param data the content, must not be {@literal null}.
     */
    public CollectionWrapper(List<T> data) {
        Assert.notNull(data, "Data must not be null");
        this.data = data;
    }

    /**
     * Constructor of {@code CollectionJson}.
     *
     * @param data the content, must not be {@literal null}.
     */
    public <S> CollectionWrapper(Converter<? super S, ? extends T> converter, List<S> data) {
        Assert.notNull(converter, "Converter must not be null");
        Assert.notNull(data, "Data must not be null");

        List<T> result = new ArrayList<>();

        for (S element : data) {
            result.add(converter.convert(element));
        }

        this.data = result;
    }

    /**
     * Gets the content.
     *
     * @return collection of items
     */
    public List<T> getData() {
        return data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .toString();
    }
}