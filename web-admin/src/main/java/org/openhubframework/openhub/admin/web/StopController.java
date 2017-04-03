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
import org.openhubframework.openhub.core.common.asynch.stop.StopService;
import org.openhubframework.openhub.spi.msg.MessageService;


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
    private StopService stopService;

    @Autowired
    private MessageService messageService;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getStoppingState(ModelMap model) {
        addStoppingState(model);

        if (stopService.isStopping()) {
            addMsgCounts(model);
        }

        return VIEW_NAME;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public String stopEsb(ModelMap model) {
        stopService.stop();

        addStoppingState(model);
        addMsgCounts(model);

        return VIEW_NAME;
    }

    @RequestMapping(value = "/cancelStop", method = RequestMethod.POST)
    public String cancelStopEsb(ModelMap model) {
        stopService.cancelStopping();

        addStoppingState(model);

        return VIEW_NAME;
    }

    private void addStoppingState(ModelMap model) {
        model.addAttribute("isStopping", stopService.isStopping());
    }

    private void addMsgCounts(ModelMap model) {
        model.addAttribute("processingCount", messageService.getCountMessages(MsgStateEnum.PROCESSING, null));
        model.addAttribute("waitingForResCount", messageService.getCountMessages(MsgStateEnum.WAITING_FOR_RES, null));
    }
}
