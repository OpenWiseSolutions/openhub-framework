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

import org.openhubframework.openhub.core.common.contextcall.ContextCall;
import org.openhubframework.openhub.core.common.route.EndpointRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for displaying overview of endpoints.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Controller
public class EndpointsController {

    public static final String VIEW_NAME = "endpoints";

    @Autowired
    private ContextCall contextCall;

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    @Value("${endpoints.includePattern}")
    private String endpointsIncludePattern;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> endpoints = contextCall.makeCall(EndpointRegistry.class, "getEndpointURIs", Collection.class,
                endpointsIncludePattern);

        // note: endpoints will be always != null
        if (endpoints != null) {
            List<String> sortedEndpoints = new ArrayList<String>(endpoints);

            // group same URIs together
            Collections.sort(sortedEndpoints);

            model.addAttribute("endpoints", sortedEndpoints);
        }

        return VIEW_NAME;
    }
}
