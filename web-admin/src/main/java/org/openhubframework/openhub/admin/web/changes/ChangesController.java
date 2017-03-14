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

package org.openhubframework.openhub.admin.web.changes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for displaying changes.MD (aka release notes/change log).
 *
 * @author Petr Juza
 * @author Tomas Hanus
 * @see ChangelogProvider
 */
@Controller
@Configuration
public class ChangesController {

    public static final String CHANGES_URI = "/changes";
    private static final String CHANGE_LOG_PATH = "changes.MD";
    
    @Autowired
    private ChangelogProvider changelogProvider;

    @RequestMapping(value = CHANGES_URI, method = RequestMethod.GET)
    @ResponseBody
    public String getChangesContent() throws IOException {
        // add end of lines
        String resStr = "";
        List<String> lines = IOUtils.readLines(changelogProvider.getChangelog().getInputStream(), StandardCharsets.UTF_8);
        for (String line : lines) {
            resStr += line;
            resStr += "<br/>\n";
        }

        return resStr;
    }

    /**
     * Registers default implementation of {@link ChangelogProvider}.
     * 
     * @return default changelog provider
     */
    @Bean
    @ConditionalOnMissingBean
    public ChangelogProvider defaultChangelogProvider() {
        return new ChangelogProvider() {
            @Override
            public Resource getChangelog() {
                return new ClassPathResource(CHANGE_LOG_PATH);
            }
        };
    }

    /**
     * Provider of {@link Resource} as change log of platform.
     */
    public interface ChangelogProvider {

        /**
         * Returns resource that represents changelog.
         * 
         * @return changelog resource
         */
        Resource getChangelog();
        
    }
}
