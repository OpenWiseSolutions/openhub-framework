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

package org.openhubframework.openhub.spi.alerts;

import java.util.List;


/**
 * Alerts configuration contract.
 *
 * @author Petr Juza
 * @since 0.4
 */
public interface AlertsConfiguration {

    /**
     * Gets alert info.
     *
     * @param id the alert identifier
     * @return {@link AlertInfo}
     */
    AlertInfo getAlert(String id);

    /**
     * Gets list of all alerts.
     *
     * @param enabledOnly if {@code true} then only enabled alerts are returned
     * @return list of all alerts in order from configuration file
     */
    List<AlertInfo> getAlerts(boolean enabledOnly);

}
