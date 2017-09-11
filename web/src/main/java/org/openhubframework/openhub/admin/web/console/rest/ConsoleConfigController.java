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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.console.rpc.ConsoleConfigRpc;


/**
 * REST controller that provides configuration for console used for example to initialize GUI.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@RestController
@RequestMapping(value = ConsoleConfigController.REST_URI)
public class ConsoleConfigController extends AbstractOhfController {

    public static final String REST_URI = BASE_PATH + "/console-config";

    @Autowired
    private ConsoleConfigRpc config;

    /**
     * Gets configuration of console.
     *
     * @return console config
     */
    @RequestMapping(method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ConsoleConfigRpc get() {
        return config;
    }

}
