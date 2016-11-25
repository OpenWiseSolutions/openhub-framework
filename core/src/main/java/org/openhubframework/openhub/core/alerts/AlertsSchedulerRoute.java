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

import static org.openhubframework.openhub.api.configuration.CoreProps.ALERTS_REPEAT_TIME_SEC;

import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.core.common.asynch.repair.RepairProcessingMsgRoute;


/**
 * Route definition that starts checking alerts by scheduler.
 *
 * @author Petr Juza
 * @since 0.4
 */
@CamelConfiguration(value = AlertsSchedulerRoute.ROUTE_BEAN)
@Profile(Profiles.PROD)
public class AlertsSchedulerRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "alertsSchedulerRoute";

    private static final String JOB_NAME = "alerts";

    /**
     * How often to run checking of alerts (in seconds).
     */
    @ConfigurableValue(key = ALERTS_REPEAT_TIME_SEC)
    private ConfigurationItem<Seconds> repeatInterval;

    @Override
    public final void configure() throws Exception {
        int repeatSec = repeatInterval.getValue().getSeconds();

        if (repeatSec != -1) {
            String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                    + "?trigger.repeatInterval=" + (repeatSec * 1000)
                    + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;
            from("quartz2://" + uri)
                    .routeId("alerts" + AbstractBasicRoute.ROUTE_SUFFIX)

                    .bean(AlertsCheckingService.BEAN, "checkAlerts");
        }
    }
}
