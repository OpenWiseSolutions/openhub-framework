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

package org.openhubframework.openhub.admin.web.errorscatalog.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;


/**
 * Test suite that verifies {@link ErrorsCatalogController} (REST API).
 *
 * @author Tomas Hanus
 */
public class ErrorsCatalogControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = ErrorsCatalogController.REST_URI;

    @Test
    public void errorsCatalog() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI);

        // performs GET: /api/errors-catalog
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name", is("OHF")))
                .andExpect(jsonPath("[0].codes[0].code", is("E100")))
                .andExpect(jsonPath("[0].codes[0].desc", is("unspecified error")));
    }

}