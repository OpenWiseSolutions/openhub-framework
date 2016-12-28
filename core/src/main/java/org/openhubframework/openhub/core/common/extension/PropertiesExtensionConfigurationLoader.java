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

package org.openhubframework.openhub.core.common.extension;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.common.Tools;


/**
 * OpenHub extensions loader where extensions are defined in properties.
 * Relevant properties are with '{@value #PROPERTY_PREFIX}' prefix.
 *
 * @author Petr Juza
 */
@Service
@DependsOn("camelContext")
public class PropertiesExtensionConfigurationLoader extends AbstractExtensionConfigurationLoader {

    public static final String PROPERTY_PREFIX = "context.ext";

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Creates new extension loader with properties from {@link Environment environment}.
     */
    public PropertiesExtensionConfigurationLoader() {
    }

    /**
     * Initializes extension configuration from properties.
     */
    @PostConstruct
    private void initExtensions() {
        Assert.notNull(env, "env must not be null");

        // gets extension config locations
        Set<String> confLocations = new HashSet<String>();

        for (String propName : Tools.getAllKnownPropertyNames(env)) {
            if (propName.startsWith(PROPERTY_PREFIX)) {
                String configLoc = env.getProperty(propName);

                if (StringUtils.isNotEmpty(configLoc)) {
                    confLocations.add(configLoc);
                }
            }
        }

        // loads extension configuration
        loadExtensions(confLocations.toArray(new String[confLocations.size()]));
    }
}
