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

package org.cleverbus.core.common.extension;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * CleverBus extensions loader where extensions are defined in properties.
 * Relevant properties are with '{@value #PROPERTY_PREFIX}' prefix.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class PropertiesExtensionConfigurationLoader extends AbstractExtensionConfigurationLoader {

    public static final String PROPERTY_PREFIX = "context.ext";

    private Properties properties;

    /**
     * Creates new extension loader with specified properties.
     *
     * @param properties the properties
     */
    public PropertiesExtensionConfigurationLoader(Properties properties) {
        Assert.notNull(properties, "the properties must not be null");

        this.properties = properties;
    }

    /**
     * Initializes extension configuration from properties.
     */
    @PostConstruct
    private void initExtensions() {
        // gets extension config locations
        Set<String> confLocations = new HashSet<String>();

        Enumeration<?> propNamesEnum = properties.propertyNames();
        while (propNamesEnum.hasMoreElements()) {
            String propName = (String) propNamesEnum.nextElement();

            if (propName.startsWith(PROPERTY_PREFIX)) {
                String configLoc = properties.getProperty(propName);

                if (StringUtils.isNotEmpty(configLoc)) {
                    confLocations.add(configLoc);
                }
            }
        }

        // loads extension configuration
        loadExtensions(confLocations.toArray(new String[]{}));
    }
}
