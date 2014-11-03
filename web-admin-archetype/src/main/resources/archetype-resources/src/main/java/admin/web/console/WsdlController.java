#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.admin.web.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.cleverbus.core.common.contextcall.ContextCall;
import org.cleverbus.core.common.ws.WsdlRegistry;


/**
 * Controller for displaying overview of WSDLs.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Controller
public class WsdlController {

    public static final String VIEW_NAME = "wsdl";

    @Autowired
    private ContextCall contextCall;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> wsdls = contextCall.makeCall(WsdlRegistry.class, "getWsdls", Collection.class);

        // note: wsdls will be always != null
        if (wsdls != null) {
            List<String> sortedWsdls = new ArrayList<String>(wsdls);

            Collections.sort(sortedWsdls);

            model.addAttribute("wsdls", sortedWsdls);
        }

        return VIEW_NAME;
    }
}
