/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.web.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;


/**
 * OpenHub specific CORS configuration which could be enabled/disabled via {@link #CORS_ENABLED} property.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 * @see CorsConfiguration
 */
@Configuration
@ConfigurationProperties(prefix = CorsProperties.PREFIX)
public class CorsProperties extends CorsConfiguration {

    /**
     * Representing <em>prefix</em> path for configuration properties.
     */
    public static final String PREFIX = "cors";

    /**
     * Representing property that holds state of CORS configuration.
     */
    public static final String CORS_ENABLED = PREFIX + ".enabled";

    /**
     * Wildcard representing <em>all</em> paths.
     */
    public static final String ALL_PATHS = "/**";

}
