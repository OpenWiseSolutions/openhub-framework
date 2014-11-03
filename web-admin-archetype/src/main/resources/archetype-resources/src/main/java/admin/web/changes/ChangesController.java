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

package ${package}.admin.web.changes;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for displaying changes.txt (aka release notes).
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Controller
public class ChangesController {

    public static final String CHANGES_URI = "/changes";

    @RequestMapping(value = CHANGES_URI, method = RequestMethod.GET)
    @ResponseBody
    public String getChangesContent() throws IOException {
        ClassPathResource resource = new ClassPathResource("changes.txt");

        // add end of lines
        String resStr = "";
        List<String> lines = IOUtils.readLines(resource.getInputStream(), "utf-8");
        for (String line : lines) {
            resStr += line;
            resStr += "<br/>${symbol_escape}n";
        }

        return resStr;
    }
}
