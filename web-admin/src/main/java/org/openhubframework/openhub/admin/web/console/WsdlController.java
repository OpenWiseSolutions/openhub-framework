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

package org.openhubframework.openhub.admin.web.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.openhubframework.openhub.core.common.ws.WsdlRegistry;


/**
 * Controller for displaying overview of WSDLs.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Controller
public class WsdlController {

    private static final String VIEW_NAME = "wsdl";

    @Autowired
    private WsdlRegistry wsdlRegistry;

    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> wsdls = wsdlRegistry.getWsdls();

        // note: wsdls will be always != null
        if (wsdls != null) {
            List<String> sortedWsdls = new ArrayList<String>(wsdls);

            Collections.sort(sortedWsdls);

            model.addAttribute("wsdls", sortedWsdls);
        }

        return VIEW_NAME;
    }
}
