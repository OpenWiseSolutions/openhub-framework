/*
 * Copyright 2002-2021 the original author or authors.
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

package org.openhubframework.openhub.common.properties;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;


/**
 * Configure and hook {@link PropertySource} from {@link #OH_PROPERTIES_LOCATION classpath:/openhub.properties}
 * external location that will override all application properties.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public class OpenHubExternalPropertiesAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHubExternalPropertiesAutoConfiguration.class);

    /**
     * Order of this listener. Must be processed after standard ConfigFileApplicationListener (processed application.properties)
     */
    private static final int ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 10;

    private static final String OH_PROPERTIES_LOCATION = "/openhub.properties";
    private static final String OH_PROPERTY_SOURCE_NAME = "openhub";

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ClassPathResource resource = new ClassPathResource(OH_PROPERTIES_LOCATION);
        if (resource.exists()) {
            LOG.info("Loading external property '{}{}'.", CLASSPATH_URL_PREFIX, OH_PROPERTIES_LOCATION);
            ResourcePropertySource propertySource;
            try {
                propertySource = new ResourcePropertySource(OH_PROPERTY_SOURCE_NAME, resource);
                event.getEnvironment().getPropertySources().addAfter(RandomValuePropertySource.RANDOM_PROPERTY_SOURCE_NAME, propertySource);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse external property " + CLASSPATH_URL_PREFIX + OH_PROPERTIES_LOCATION, e);
            }
        }
        LOG.debug("External property '{}{}' was not found.", CLASSPATH_URL_PREFIX, OH_PROPERTIES_LOCATION);
    }
}
