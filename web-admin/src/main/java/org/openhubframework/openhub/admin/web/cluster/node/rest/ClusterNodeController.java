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

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.web.cluster.node.rpc.NodeRpc;
import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.common.rpc.CollectionWrapper;
import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.api.exception.validation.ValidationException;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * REST controller with operations of {@link org.openhubframework.openhub.api.entity.Node} entity.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@RestController
@RequestMapping(value = ClusterNodeController.REST_URI)
public class ClusterNodeController extends AbstractOhfController {

    public static final String REST_URI = BASE_PATH + "/cluster/nodes";

    @Autowired
    private NodeService nodeService;

    /**
     * Gets list of all cluster nodes.
     *
     * @return list of nodes
     */
    @RequestMapping(method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CollectionWrapper<NodeRpc> findAll() {
        List<Node> nodes = nodeService.getAllNodes();

        return new CollectionWrapper<>(new NodeRpc.NodeRpcConverter(), nodes);
    }

    /**
     * Gets {@link NodeRpc} by ID.
     *
     * @param id The {@link NodeRpc#getId()}
     * @return node entity
     * @throws NoDataFoundException if entity not found
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NodeRpc getById(@PathVariable final Long id) throws NoDataFoundException {
        return new NodeRpc(nodeService.getNodeById(id));
    }

    /**
     * Updates existing {@link NodeRpc}.
     *
     * @param id      as identifier of {@link NodeRpc}
     * @param nodeRpc The {@link NodeRpc}
     * @throws ValidationException  if input object has wrong values
     * @throws NoDataFoundException entity not found for update
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void update(@PathVariable final Long id, @RequestBody @Valid final NodeRpc nodeRpc,
            BindingResult errors) throws ValidationException, NoDataFoundException {

        Assert.notNull(nodeRpc, "nodeRpc can not be null");
        if (nodeRpc.getId() != null) {
            Constraints.state(id.equals(nodeRpc.getId()), "IDs must be equal");
        }

        // get entity to update it
        Node dbNode = nodeService.getNodeById(id);

        // save entity
        nodeService.update(dbNode, new NodeRpc.ChangeCallback(errors, nodeRpc));
    }

    /**
     * Deletes existing {@link NodeRpc}.
     *
     * @param id as identifier of {@link NodeRpc}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void delete(@PathVariable final Long id) {
        nodeService.delete(nodeService.getNodeById(id));
    }
}
