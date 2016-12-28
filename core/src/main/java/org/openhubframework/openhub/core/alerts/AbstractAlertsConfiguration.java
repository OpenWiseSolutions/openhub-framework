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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.openhubframework.openhub.spi.alerts.AlertInfo;
import org.openhubframework.openhub.spi.alerts.AlertsConfiguration;


/**
 * Parent class for alerts configuration.
 *
 * @author Petr Juza
 * @since 0.4
 */
public abstract class AbstractAlertsConfiguration implements AlertsConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAlertsConfiguration.class);

    private List<AlertInfo> alerts = new CopyOnWriteArrayList<AlertInfo>();

    protected final void addAlert(AlertInfo alertInfo) {
        Assert.notNull(alertInfo, "alertInfo must not be null");

        // check uniqueness
        if (alerts.contains(alertInfo)) {
            throw new IllegalStateException("Wrong alert's configuration - alert (id = '"
                    + alertInfo.getId() + "') already exist.");
        } else {
            LOG.debug("New alert info added: " + alertInfo);
            alerts.add(alertInfo);
        }
    }

    @Override
    public final AlertInfo getAlert(String id) {
        AlertInfo alert = findAlert(id);

        if (alert == null) {
            throw new IllegalArgumentException("There is no alert with id = '" + id + "'");
        }

        return alert;
    }

    @Nullable
    AlertInfo findAlert(String id) {
        for (AlertInfo alert : alerts) {
            if (alert.getId().equals(id)) {
                return alert;
            }
        }

        return null;
    }

    @Override
    public final List<AlertInfo> getAlerts(boolean enabledOnly) {
        if (!enabledOnly) {
            // return all
            return Collections.unmodifiableList(alerts);

        } else {
            // note: wouldn't be better to keep duplicated list of enabled alerts?
            List<AlertInfo> retAlerts = new ArrayList<AlertInfo>();
            for (AlertInfo alert : alerts) {
                if (alert.isEnabled()) {
                    retAlerts.add(alert);
                }
            }

            return retAlerts;
        }
    }
}
