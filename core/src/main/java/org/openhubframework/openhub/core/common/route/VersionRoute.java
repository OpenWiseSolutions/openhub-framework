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

package org.openhubframework.openhub.core.common.route;

import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.common.version.VersionInfo;
import org.openhubframework.openhub.core.common.version.VersionInfoSource;
import org.openhubframework.openhub.core.common.version.VersionPrinter;


/**
 * Route definition for version service - listens to {@code /http/version} url.
 *
 * @author Petr Juza
 */
@CamelConfiguration
public class VersionRoute extends AbstractBasicRoute {

    private static final String ROUTE_ID_VERSION = "version" + IN_ROUTE_SUFFIX;

    @Autowired
    private VersionInfoSource versionInfoSource;

    @Override
    public void doConfigure() throws Exception {
        VersionInfo filter = new VersionInfo(VersionPrinter.APPLICATION_NAME, null, null, null, null);
        final VersionInfo[] versions = versionInfoSource.getVersionInformation(filter);

        String versionStr = "N/A";
        if (versions.length > 0) {
            versionStr = versions[0].getFullVersion() + " (" + versions[0].getDate() + ")";
        }

        from("servlet:///version?servletName=" + RouteConstants.CAMEL_SERVLET)
                .routeId(ROUTE_ID_VERSION)
                .transform(constant(versionStr));
    }
}
