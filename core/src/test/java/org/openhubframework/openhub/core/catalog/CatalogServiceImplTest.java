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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.openhubframework.openhub.api.catalog.Catalog;
import org.openhubframework.openhub.api.catalog.CatalogComponent;
import org.openhubframework.openhub.api.catalog.CatalogEntry;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.spi.catalog.CatalogService;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Simple test suite for {@link CatalogServiceImpl}.
 *
 * @author Karel Kovarik
 */
@ContextConfiguration(classes = {
        CatalogServiceImpl.class,
        CatalogServiceImplTest.SourceSystemTestCatalog.class
})
public class CatalogServiceImplTest extends AbstractCoreTest {

    @Autowired
    private CatalogService catalogService;

    @Test
    public void test_ok() {
        final List<CatalogEntry> catalogEntries = catalogService.getEntries("sourceSystems");
        assertThat(catalogEntries, hasSize(2));
        assertThat(catalogEntries.get(0).getCode(), is("CRM"));
        assertThat(catalogEntries.get(0).getDescription(), is("CRM system"));
        assertThat(catalogEntries.get(1).getCode(), is("BILLING"));
        assertThat(catalogEntries.get(1).getDescription(), is("Billing system"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_catalogNotFound() {
        catalogService.getEntries("FAKE");
    }

    /**
     * Simple test implementation of catalog.
     */
    @CatalogComponent("sourceSystems")
    static class SourceSystemTestCatalog implements Catalog {

        @Override
        public List<CatalogEntry> getEntries() {
            return Stream.of(
                    new SimpleCatalogEntry(ExternalSystemTestEnum.CRM.getSystemName(), "CRM system"),
                    new SimpleCatalogEntry(ExternalSystemTestEnum.BILLING.getSystemName(), "Billing system")
            ).collect(Collectors.toList());
        }
    }
}