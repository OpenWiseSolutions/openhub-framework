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

package ${package}.web.console;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for displaying admin console directory of actions
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
@Controller
public class ConsoleController {

    public static final String JAVAMELODY_DISABLED = "javamelody.disabled";

    public static final String VIEW_NAME = "console";

    @RequestMapping("/console")
    public String showConsole(@ModelAttribute("model") ModelMap model) {

        // this variable is set only if monitoring is switched on
        final String state = System.getProperty(JAVAMELODY_DISABLED);

        final Boolean monitoring = StringUtils.isEmpty(state) || Boolean.valueOf(state).equals(Boolean.TRUE);
        model.addAttribute("javamelody", monitoring);

        return VIEW_NAME;
    }
}
