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

package org.openhubframework.openhub.admin.web.catalog.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;
import org.openhubframework.openhub.api.catalog.CatalogEntry;
import org.openhubframework.openhub.core.catalog.SimpleCatalogEntry;
import org.openhubframework.openhub.spi.catalog.CatalogService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;


/**
 * Simple test suite for {@link CatalogController}.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class CatalogControllerTest extends AbstractAdminModuleRestTest {

    @MockBean
    private CatalogService catalogService;

    @Test
    public void getCatalog_ok() throws Exception {
        final List<CatalogEntry> sourceSystems = Stream.of(
                new SimpleCatalogEntry("CRM", "CRM system"),
                new SimpleCatalogEntry("BILLING", "Billing system"))
                .collect(Collectors.toList());
        when(catalogService.getEntries(eq("sourceSystems")))
                .thenReturn(sourceSystems);

        final URIBuilder uriBuilder = createGetUrl(CatalogController.REST_URI + "/sourceSystems");

        // GET /api/catalogs/sourceSystems
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].code", is("CRM")))
                .andExpect(jsonPath("$.data[0].description", is("CRM system")))
                .andExpect(jsonPath("$.data[1].code", is("BILLING")))
                .andExpect(jsonPath("$.data[1].description", is("Billing system")))
        ;
    }

    @Test
    public void getCatalog_noEntriesInCatalog() throws Exception {
        when(catalogService.getEntries(eq("EMPTY_CATALOG")))
                .thenReturn(Collections.emptyList());

        final URIBuilder uriBuilder = createGetUrl(CatalogController.REST_URI + "/EMPTY_CATALOG");

        // GET /api/catalogs/NOT_EXISTING
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
        ;
    }

    @Test
    public void getCatalog_notExistingCatalog() throws Exception {
        when(catalogService.getEntries(eq("NOT_EXISTING_CATALOG")))
                .thenThrow(new IllegalArgumentException());

        final URIBuilder uriBuilder = createGetUrl(CatalogController.REST_URI + "/NOT_EXISTING_CATALOG");

        // GET /api/catalogs/NOT_EXISTING
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNotFound())
        ;
    }
}