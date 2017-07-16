/*
 * Copyright 2012-2017 the original author or authors.
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

package org.openhubframework.openhub.web.common;

import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * Constants of web module related property names.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public final class WebProps {

    public static final String MESSAGES_LIMIT = OpenHubPropertyConstants.PREFIX + "admin.console.messages.limit";

    private WebProps() {
        // to prevent instantiation
    }
}
