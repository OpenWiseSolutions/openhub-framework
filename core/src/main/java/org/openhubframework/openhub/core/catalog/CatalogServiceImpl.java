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

package org.openhubframework.openhub.core.catalog;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.openhubframework.openhub.api.catalog.Catalog;
import org.openhubframework.openhub.api.catalog.CatalogEntry;
import org.openhubframework.openhub.spi.catalog.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link CatalogService}.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private Optional<List<Catalog>> catalogList;

    @Override
    public List<CatalogEntry> getEntries(final String catalogName) {
        Assert.hasText(catalogName, "the catalog name must not be empty!");

        return catalogList.flatMap(
                catalog -> catalog.stream()
                        // find catalog with given name
                        .filter(c -> catalogName.equalsIgnoreCase(c.getName()))
                        .findFirst()
                        .map(Catalog::getEntries))
                // if catalog with given name was not found, throw IllegalArgumentException.
                .orElseThrow(() ->
                        new IllegalArgumentException(MessageFormat.format("Catalog with name {0} was not found.", catalogName))
                );
    }
}
