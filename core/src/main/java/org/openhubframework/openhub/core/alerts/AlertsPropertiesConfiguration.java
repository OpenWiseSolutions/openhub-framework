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

package org.openhubframework.openhub.core.alerts;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.common.Tools;
import org.openhubframework.openhub.spi.alerts.AlertInfo;


/**
 * Alerts configuration via {@link Properties}.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Service
public class AlertsPropertiesConfiguration extends AbstractAlertsConfiguration {

    public static final String ALERT_PROP_PREFIX = "alerts.";

    public static final String ID_PROP = "id";

    public static final String LIMIT_PROP = "limit";

    public static final String SQL_PROP = "sql";

    public static final String ENABLED_PROP = "enabled";

    public static final String MAIL_SBJ_PROP = "mail.subject";

    public static final String MAIL_BODY_PROP = "mail.body";

    private ConfigurableEnvironment env;

    /**
     * Creates new configuration with properties from {@link Environment environment}.
     */
    @Autowired
    public AlertsPropertiesConfiguration(ConfigurableEnvironment env) {
        this.env = env;
    }

    /**
     * Initializes configuration from properties.
     */
    @PostConstruct
    void initProps() {
        Assert.notNull(env, "env must not be null");

        // example of alert configuration
//        alerts.900.id=WAITING_MSG_ALERT
//        alerts.900.limit=0
//        alerts.900.sql=SELECT COUNT(*) FROM message WHERE state = 'WAITING_FOR_RES'
//        alerts.900.enabled=true
//        alerts.900.mail.subject=There are %d message(s) in WAITING_FOR_RESPONSE state for more then %d seconds.
//        alerts.900.mail.body=Alert: notification about WAITING messages


        // get relevant properties for alerts
        List<String> propNames = new ArrayList<String>();

        for (String propName : Tools.getAllKnownPropertyNames(env)) {
            if (propName.startsWith(ALERT_PROP_PREFIX)) {
                propNames.add(propName);
            }
        }

        // get alert IDs
        Set<String> orders = new HashSet<String>();

        Pattern pattern = Pattern.compile("alerts\\.(\\d+)\\.id");
        for (String propName : propNames) {
            Matcher matcher = pattern.matcher(propName);

            if (matcher.matches()) {
                String order = StringUtils.substringBetween(propName, ALERT_PROP_PREFIX, ".");

                if (orders.contains(order)) {
                    throw new IllegalStateException("Wrong alert's configuration - alert order '"
                            + order + "' was already used.");
                } else {
                    orders.add(order);
                }
            }
        }


        // get property values
        Set<String> ids = new HashSet<String>();
        for (String order : orders) {
            String propPrefix = ALERT_PROP_PREFIX + order + ".";

            String id = env.getProperty(propPrefix + ID_PROP);

            // check if id is unique
            if (ids.contains(id)) {
                throw new IllegalStateException("Wrong alert's configuration - id '" + id + "' is not unique.");
            } else {
                ids.add(id);
            }

            String limit = env.getProperty(propPrefix + LIMIT_PROP);
            String sql = env.getProperty(propPrefix + SQL_PROP);

            // check if sql contains count()
            if (!StringUtils.containsIgnoreCase(sql, "count(")) {
                throw new IllegalStateException("Wrong alert's configuration - SQL clause for id '" + id
                        + "' doesn't contain count().");
            }

            String enabled = env.getProperty(propPrefix + ENABLED_PROP);
            enabled = enabled == null ? "true" : enabled;

            String subject = env.getProperty(propPrefix + MAIL_SBJ_PROP);
            String body = env.getProperty(propPrefix + MAIL_BODY_PROP);


            // add new alert
            try {
                AlertInfo alertInfo = new AlertInfo(id, Long.valueOf(limit), sql, BooleanUtils.toBoolean(enabled),
                        subject, body);

                addAlert(alertInfo);
            } catch (Exception ex) {
                throw new IllegalStateException("Wrong alert's configuration - conversion error", ex);
            }
        }
    }
}
