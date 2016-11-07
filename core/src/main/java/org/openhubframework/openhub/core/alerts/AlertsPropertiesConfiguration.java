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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhubframework.openhub.spi.alerts.AlertInfo;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Alerts configuration via {@link Properties}.
 *
 * @author Petr Juza
 * @since 0.4
 */
public class AlertsPropertiesConfiguration extends AbstractAlertsConfiguration {

    public static final String ALERT_PROP_PREFIX = "alerts.";

    public static final String ID_PROP = "id";

    public static final String LIMIT_PROP = "limit";

    public static final String SQL_PROP = "sql";

    public static final String ENABLED_PROP = "enabled";

    public static final String MAIL_SBJ_PROP = "mail.subject";

    public static final String MAIL_BODY_PROP = "mail.body";

    private Properties properties;

    /**
     * Creates new configuration with specified properties.
     *
     * @param properties the properties
     */
    public AlertsPropertiesConfiguration(Properties properties) {
        Assert.notNull(properties, "the properties must not be null");

        this.properties = properties;

        initProps();
    }

    /**
     * Initializes configuration from properties.
     */
    private void initProps() {
        // example of alert configuration
//        alerts.900.id=WAITING_MSG_ALERT
//        alerts.900.limit=0
//        alerts.900.sql=SELECT COUNT(*) FROM message WHERE state = 'WAITING_FOR_RES'
//        alerts.900.enabled=true
//        alerts.900.mail.subject=There are %d message(s) in WAITING_FOR_RESPONSE state for more then %d seconds.
//        alerts.900.mail.body=Alert: notification about WAITING messages


        // get relevant properties for alerts
        List<String> propNames = new ArrayList<String>();

        Enumeration<?> propNamesEnum = properties.propertyNames();
        while (propNamesEnum.hasMoreElements()) {
            String propName = (String) propNamesEnum.nextElement();

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

            String id = properties.getProperty(propPrefix + ID_PROP);

            // check if id is unique
            if (ids.contains(id)) {
                throw new IllegalStateException("Wrong alert's configuration - id '" + id + "' is not unique.");
            } else {
                ids.add(id);
            }

            String limit = properties.getProperty(propPrefix + LIMIT_PROP);
            String sql = properties.getProperty(propPrefix + SQL_PROP);

            // check if sql contains count()
            if (!StringUtils.containsIgnoreCase(sql, "count(")) {
                throw new IllegalStateException("Wrong alert's configuration - SQL clause for id '" + id
                        + "' doesn't contain count().");
            }

            String enabled = properties.getProperty(propPrefix + ENABLED_PROP);
            enabled = enabled == null ? "true" : enabled;

            String subject = properties.getProperty(propPrefix + MAIL_SBJ_PROP);
            String body = properties.getProperty(propPrefix + MAIL_BODY_PROP);


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
