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

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.spi.alerts.AlertInfo;
import org.openhubframework.openhub.spi.alerts.AlertListener;
import org.openhubframework.openhub.spi.alerts.AlertsConfiguration;


/**
 * Default implementation of {@link AlertsCheckingService} interface.
 *
 * @author Petr Juza
 * @since 0.4
 */
public abstract class AbstractAlertsCheckingService implements AlertsCheckingService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAlertsCheckingService.class);

    @Autowired
    private AlertsConfiguration alertsConfig;

    @Autowired
    private Collection<AlertListener> listeners;

    @Override
    public final void checkAlerts() {
        LOG.debug("Alerts checking starts ...");

        if (listeners.isEmpty()) {
            LOG.debug("There is no listeners => not reason for alerts checking.");

            return;
        }

        List<AlertInfo> alerts = alertsConfig.getAlerts(true);

        for (AlertInfo alert : alerts) {
            long count = getCount(alert);

            if (count > alert.getLimit()) {
                LOG.debug("Actual count=" + count + " exceeded limit (" + alert.getLimit()
                        + ") of alert (" + alert.toHumanString() + ")");

                // notify all listeners
                for (AlertListener listener : listeners) {
                    try {
                        if (listener.supports(alert)) {
                            listener.onAlert(alert, count);
                        }
                    } catch (Exception ex) {
                        LOG.error("Listener (" + listener.getClass().getName() + ") for alert (" + alert
                                + ") ends with exception.", ex);
                    }
                }
            }
        }

        LOG.debug("Alerts checking ends.");
    }

    /**
     * Gets actual count of specified alert.
     *
     * @param alert the alert info
     * @return count of items from the alert query
     */
    protected abstract long getCount(AlertInfo alert);
}
