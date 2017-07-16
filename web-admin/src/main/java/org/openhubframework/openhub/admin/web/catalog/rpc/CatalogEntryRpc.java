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

package org.openhubframework.openhub.admin.web.catalog.rpc;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.api.catalog.CatalogEntry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

/**
 * Rpc with catalog entry.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class CatalogEntryRpc {

    private final String code;
    private final String description;

    public CatalogEntryRpc(String code, String description) {
        Assert.hasText(code, "the code must not be null");

        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Converter to convert domain object to rpc.
     * @return Converter from {@link CatalogEntry} to {@link CatalogEntryRpc}.
     * @see Converter
     */
    public static Converter<CatalogEntry, CatalogEntryRpc> fromCatalogEntry() {
        return source -> new CatalogEntryRpc(source.getCode(), source.getDescription());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("code", code)
                .append("description", description)
                .toString();
    }
}
