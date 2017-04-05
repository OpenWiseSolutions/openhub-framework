/*
 * Copyright 2014 the original author or authors.
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

package org.openhubframework.openhub.admin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * Controller for stopping ESB.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Controller
public class StopController {

    private static final String VIEW_NAME = "stop";

    @Autowired
    private NodeService nodeService;

    @Autowired
    private MessageService messageService;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getStoppingState(ModelMap model) {
        addStoppingState(model);

        if (isAllNodesInStopping()) {
            addMsgCounts(model);
        }

        return VIEW_NAME;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public String stopEsb(ModelMap model) {
        for (Node node : nodeService.getAllNodes()) {
            nodeService.update(node, new ChangeNodeCallback() {
                @Override
                public void updateNode(MutableNode node) {
                    node.setStoppedState();
                }
            });
        }

        addStoppingState(model);
        addMsgCounts(model);

        return VIEW_NAME;
    }

    @RequestMapping(value = "/cancelStop", method = RequestMethod.POST)
    public String cancelStopEsb(ModelMap model) {
        for (Node node : nodeService.getAllNodes()) {
            nodeService.update(node, new ChangeNodeCallback() {
                @Override
                public void updateNode(MutableNode node) {
                    node.setRunState();
                }
            });
        }

        addStoppingState(model);

        return VIEW_NAME;
    }

    private void addStoppingState(ModelMap model) {
        model.addAttribute("isStopping", isAllNodesInStopping());
    }

    private void addMsgCounts(ModelMap model) {
        model.addAttribute("processingCount", messageService.getCountMessages(MsgStateEnum.PROCESSING, null));
        model.addAttribute("waitingForResCount", messageService.getCountMessages(MsgStateEnum.WAITING_FOR_RES, null));
    }

    /**
     * Gets if all nodes are in {@link NodeState#STOPPED}.
     *
     * @return {@code true} all node is in {@link NodeState#STOPPED}, {@code false} - otherwise
     */
    private boolean isAllNodesInStopping() {
        for (Node node : nodeService.getAllNodes()) {
            if (!node.isStopped()) {
                return false;
            }
        }
        return true;
    }
}
