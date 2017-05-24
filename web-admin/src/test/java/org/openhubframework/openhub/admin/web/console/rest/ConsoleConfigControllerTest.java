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

package org.openhubframework.openhub.admin.web.console.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;

import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;


/**
 * Test suite that verifies {@link ConsoleConfigController} (REST API).
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@TestPropertySource(properties = {
        "ohf.admin.console.config.menu.external-links.enabled=true",
        "ohf.admin.console.config.menu.external-links.items[0].title=title1",
        "ohf.admin.console.config.menu.external-links.items[0].link=link1",
        "ohf.admin.console.config.menu.external-links.items[0].enabled=true",
        "ohf.admin.console.config.menu.external-links.items[1].title=title2",
        "ohf.admin.console.config.menu.external-links.items[1].link=link2",
        "ohf.admin.console.config.menu.external-links.items[1].enabled=true",
})
public class ConsoleConfigControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = ConsoleConfigController.REST_URI;

    @Test
    public void testGet() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI);

        // performs GET: /api/errors-catalog
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("config.menu.changes.enabled", is(true)))
                .andExpect(jsonPath("config.menu.externalLinks.enabled", is(true)))
                .andExpect(jsonPath("config.menu.externalLinks.items[0].title", is("title1")))
                .andExpect(jsonPath("config.menu.externalLinks.items[0].link", is("link1")))
                .andExpect(jsonPath("config.menu.externalLinks.items[0].enabled", is(true)))                
                .andExpect(jsonPath("config.menu.externalLinks.items[1].title", is("title2")))
                .andExpect(jsonPath("config.menu.externalLinks.items[1].link", is("link2")))
                .andExpect(jsonPath("config.menu.externalLinks.items[1].enabled", is(true)));
    }

}