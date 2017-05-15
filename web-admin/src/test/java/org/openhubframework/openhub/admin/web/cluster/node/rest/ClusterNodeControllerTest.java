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

package org.openhubframework.openhub.admin.web.cluster.node.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createJson;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;

import javax.json.JsonObject;

import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * Test suite that verifies {@link ClusterNodeController} (REST API).
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Transactional
public class ClusterNodeControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = ClusterNodeController.REST_URI;

    private Node firstNode;
    private Node secondNode;

    @Autowired
    private NodeService nodeService;

    @Before
    public void prepareData() {
        firstNode = new MutableNode("codeFirst", "nameFirst");
        secondNode = new MutableNode("codeSecond", "nameSecond", NodeState.STOPPED);
        ((MutableNode) secondNode).setDescription("descriptionSecond");

        firstNode = nodeService.insert(firstNode);
        secondNode = nodeService.insert(secondNode);
    }

    @Test
    public void findAll() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI);

        // performs GET: /api/cluster/nodes
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data[0].id", equalTo(firstNode.getId().intValue())))
                .andExpect(jsonPath("data[0].code", is(firstNode.getCode())))
                .andExpect(jsonPath("data[0].name", is(firstNode.getName())))
                .andExpect(jsonPath("data[0].description").doesNotExist())
                .andExpect(jsonPath("data[0].state", is(firstNode.getState().name())))
                .andExpect(jsonPath("data[1].id", equalTo(secondNode.getId().intValue())))
                .andExpect(jsonPath("data[1].code", is(secondNode.getCode())))
                .andExpect(jsonPath("data[1].name", is(secondNode.getName())))
                .andExpect(jsonPath("data[1].description", is(secondNode.getDescription())))
                .andExpect(jsonPath("data[1].state", is(secondNode.getState().name())));
    }

    @Test
    public void testGetById() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + firstNode.getId());

        // performs GET: /api/cluster/nodes/1
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(firstNode.getId().intValue())))
                .andExpect(jsonPath("code", is(firstNode.getCode())))
                .andExpect(jsonPath("name", is(firstNode.getName())))
                .andExpect(jsonPath("description").doesNotExist())
                .andExpect(jsonPath("state", is(firstNode.getState().name())));
    }

    @Test
    public void testUpdate() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + firstNode.getId());

        JsonObject request = createJson()
                .add("name", "updatedFirstNode")
                .add("description", "updated")
                .add("state", "STOPPED")
                .build();

        // performs PUT: /api/cluster/nodes/1
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));

        // check updated property
        Node tested = nodeService.getNodeById(firstNode.getId());
        assertThat(tested.getName(), is("updatedFirstNode"));
        assertThat(tested.getDescription(), is("updated"));
        assertThat(tested.getState(), is(NodeState.STOPPED));
    }

    @Test
    public void testUpdate_error() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + firstNode.getId());

        JsonObject request = createJson()
                .add("name", "updatedFirstNode")
                .add("description", "updated")
                .build();

        // performs PUT: /api/cluster/nodes/1
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode", is("E109")))
                .andExpect(jsonPath("type", is("InputValidationException")))
                .andExpect(jsonPath("message",
                        Matchers.is("Input validation error in object 'nodeRpc'")))
                .andExpect(jsonPath("httpStatus", is(400)))
                .andExpect(jsonPath("inputFields[0].objectName", is("nodeRpc")))
                .andExpect(jsonPath("inputFields[0].field", is("state")))
                .andExpect(jsonPath("inputFields[0].code", is("NotEmpty")))
                .andExpect(jsonPath("inputFields[0].message", is("may not be empty")))                
                .andExpect(jsonPath("httpDesc", is("Bad Request")));
    }

    @Test
    public void testDelete() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + firstNode.getId());

        // performs PUT: /api/cluster/nodes/1
        mockMvc.perform(delete(toUrl(uriBuilder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNoContent());

        try {
            nodeService.getNodeById(firstNode.getNodeId());
            fail("Node cannot not exist");
        } catch (Exception e) {
            assertThat(e, instanceOf(NoDataFoundException.class));
        }
    }
}