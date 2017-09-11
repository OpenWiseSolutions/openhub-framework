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

import org.openhubframework.openhub.admin.web.catalog.rpc.CatalogEntryRpc;
import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.common.rpc.CollectionWrapper;
import org.openhubframework.openhub.spi.catalog.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for OpenHub catalogs.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
@RestController
@RequestMapping(CatalogController.REST_URI)
public class CatalogController extends AbstractOhfController {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogController.class);

    public static final String REST_URI = BASE_PATH + "/catalogs";

    @Autowired
    private CatalogService catalogService;

    /**
     * Get full catalog identified by its name.
     *
     * @param name the name of catalog.
     * @return list of catalog entries, 404 response code if catalog is not found.
     */
    @GetMapping(path = "/{name}", produces = {"application/xml", "application/json"})
    public ResponseEntity<CollectionWrapper<CatalogEntryRpc>> getCatalog(@PathVariable final String name) {
        try {
            return new ResponseEntity<>(new CollectionWrapper<>(
                    CatalogEntryRpc.fromCatalogEntry(),
                    catalogService.getEntries(name)), HttpStatus.OK
            );
        } catch (final IllegalArgumentException ex) {
            LOG.info("Catalog service IllegalArgumentException: ", ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
