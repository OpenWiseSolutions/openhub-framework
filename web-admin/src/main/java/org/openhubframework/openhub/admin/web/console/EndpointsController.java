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

import static org.openhubframework.openhub.api.configuration.CoreProps.ENDPOINTS_INCLUDE_PATTERN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.core.common.route.EndpointRegistry;


/**
 * Controller for displaying overview of endpoints.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Controller
public class EndpointsController {

    private static final String VIEW_NAME = "endpoints";

    @Autowired
    private EndpointRegistry endpointRegistry;

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    @ConfigurableValue(key = ENDPOINTS_INCLUDE_PATTERN)
    private ConfigurationItem<String> endpointsIncludePattern;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> endpoints = endpointRegistry.getEndpointURIs(endpointsIncludePattern.getValue());

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
