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

package org.openhubframework.openhub.core.common.version;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Class for printing version info when application starts.
 *
 * @author Petr Juza
 */
@Component
public class VersionPrinter {

    private static final Logger LOG = LoggerFactory.getLogger(VersionPrinter.class);

    @Autowired
    private VersionInfoSource versionInfoSource;

    /**
     * Application name, must correspond with {@code <name>} element from root pom.xml.
     */
    public static final String APPLICATION_NAME = "OpenHub Core";

    @PostConstruct
    public void printVersion() {
        VersionInfo filter = new VersionInfo(APPLICATION_NAME, null, null, null, null);
        final VersionInfo[] versions = versionInfoSource.getVersionInformation(filter);

        String versionStr = "N/A";
        if (versions.length > 0) {
            versionStr = versions[0].getFullVersion() + " (" + versions[0].getDate() + ")";
        }

        LOG.info("OpenHub version: " + versionStr);
    }
}
