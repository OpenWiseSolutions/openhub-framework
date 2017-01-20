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

package org.openhubframework.openhub.core.throttling;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.common.Tools;
import org.openhubframework.openhub.spi.throttling.ThrottleProps;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Throttling configuration via {@link Properties}.
 *
 * @author Petr Juza
 */
@Service
public class ThrottlingPropertiesConfiguration extends AbstractThrottlingConfiguration {

    static final String PROPERTY_PREFIX = "throttling.";
    static final String DEFAULT_INTERVAL_PROP = PROPERTY_PREFIX + "defaultInterval";
    static final String DEFAULT_LIMIT_PROP = PROPERTY_PREFIX + "defaultLimit";

    @Autowired
    private Environment env;

    /**
     * Creates new configuration with properties from {@link Environment environment}.
     *
     * @param env the environment
     */
    @Autowired
    public ThrottlingPropertiesConfiguration(Environment env) {
        this.env = env;

        boolean throttlingDisabled = env.getProperty("ohf.disable.throttling", Boolean.class, false);

        LOG.debug("throttlingDisabled set to: " + throttlingDisabled);

        this.setThrottlingDisabled(throttlingDisabled);
    }

    /**
     * Initializes configuration from properties.
     */
    @PostConstruct
    void initProps() {
        Assert.notNull(env, "env must not be null");

        // extracts relevant property values
        int defaultInterval = DEFAULT_INTERVAL;
        int defaultLimit = DEFAULT_LIMIT;

        List<String> propNames = new ArrayList<String>();

        for (String propName : Tools.getAllKnownPropertyNames((ConfigurableEnvironment)env)) {
            if (propName.startsWith(PROPERTY_PREFIX)) {
                switch (propName) {
                    case DEFAULT_INTERVAL_PROP:
                        defaultInterval = Integer.valueOf(env.getProperty(DEFAULT_INTERVAL_PROP));
                        break;
                    case DEFAULT_LIMIT_PROP:
                        defaultLimit = Integer.valueOf(env.getProperty(DEFAULT_LIMIT_PROP));
                        break;
                    default:
                        propNames.add(propName);
                        break;
                }
            }
        }

        // add default throttle scope
        addProperty(ThrottleScope.ANY_SOURCE_SYSTEM, ThrottleScope.ANY_SERVICE, defaultInterval, defaultLimit);

        // create throttle scopes for relevant properties
        for (String propName : propNames) {
            // validate property name, possible values:
            //  throttling.crm.setActivityExt
            //  throttling.*.setActivityExt
            //  throttling.crm.*

            String[] nameParts = StringUtils.split(propName, ThrottleScope.THROTTLE_SEPARATOR);
            if (nameParts.length != 3) {
                throw new IllegalStateException("throttling property name must have exactly 3 parts, "
                        + "e.g. 'throttling.crm.setActivityExt'");
            }

            String propValue = env.getProperty(propName);

            // format: limit [/interval]
            String[] valueParts = StringUtils.split(propValue, ThrottleProps.PROP_VALUE_SEPARATOR);
            int limit = Integer.valueOf(valueParts[0]);
            int interval = defaultInterval;
            if (valueParts.length > 1) {
                interval = Integer.valueOf(valueParts[1]);
            }

            addProperty(nameParts[1], nameParts[2], interval, limit);
        }
    }
}
