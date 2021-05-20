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

package org.openhubframework.openhub.core.configuration;

import static org.openhubframework.openhub.api.configuration.CoreProps.PROPERTY_INCLUDE_PATTERN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * Configure and hook {@link PropertySource} from database table "configuration" that represents default application
 * configuration.
 * <p>
 * Use '{@value CoreProps#PROPERTY_INCLUDE_PATTERN}' property to define pattern for which property names should
 * be loaded from DB. If there is no defined then all property names which starts with
 * '{@value OpenHubPropertyConstants#PREFIX}' will be loaded from DB.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Component
public class DbExternalPropertiesAutoConfiguration
        implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(DbExternalPropertiesAutoConfiguration.class);

    /**
     * Order of this listener. Must be processed after standard {@link ConfigDataEnvironmentPostProcessor}
     * (processed application.properties).
     */
    public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 10;

    public static final String DB_CONF_PROPERTY_SOURCE_NAME = "dbConfiguration";

    @Value("${" + PROPERTY_INCLUDE_PATTERN + ":#{null}}")
    private String includePatternStr;

    @Autowired
    private DbConfigurationParamService paramService;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Constraints.notNull(event, "event must not be null");

        final PropertySource source = new DbPropertySource(DB_CONF_PROPERTY_SOURCE_NAME, paramService, includePatternStr);

        ConfigurableEnvironment env = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();
        env.getPropertySources().addLast(source);

        LOG.debug("External DB configuration '{}' was added.", DB_CONF_PROPERTY_SOURCE_NAME);
    }
}
