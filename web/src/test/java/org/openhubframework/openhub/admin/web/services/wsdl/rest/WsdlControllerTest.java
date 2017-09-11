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

package org.openhubframework.openhub.admin.web.services.wsdl.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;
import org.openhubframework.openhub.core.common.ws.WsdlRegistry;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

/**
 * Simple test suite for {@link WsdlController}.
 *
 * @author Karel Kovarik
 */
public class WsdlControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = WsdlController.REST_URI;

    // mocked wsdlRegistry, not to have only empty list
    @MockBean
    private WsdlRegistry wsdlRegistry;

    @Test
    public void wsdlList() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI);

        Mockito.when(wsdlRegistry.getWsdls())
                .thenReturn(Collections.singletonList("hello"));

        // GET /api/services/wsdl
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.[0].name", is("hello")))
                .andExpect(jsonPath("data.[0].wsdl", is("http://localhost/ws/hello.wsdl")))
        ;
    }
}
