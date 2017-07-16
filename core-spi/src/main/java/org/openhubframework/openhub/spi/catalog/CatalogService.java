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

package org.openhubframework.openhub.spi.catalog;

import java.util.List;

import org.openhubframework.openhub.api.catalog.CatalogEntry;


/**
 * Service to provide OpenHub catalogs.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public interface CatalogService {

    /**
     * Fetch all entries from catalog identified by name.
     * @param catalogName the catalog name.
     * @return list of all entries.
     */
    List<CatalogEntry> getEntries(String catalogName);
}
