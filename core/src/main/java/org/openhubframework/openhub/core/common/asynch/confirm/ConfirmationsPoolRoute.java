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

package org.openhubframework.openhub.core.common.asynch.confirm;

import org.apache.camel.spring.SpringRouteBuilder;
import org.joda.time.Seconds;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.asynch.repair.RepairProcessingMsgRoute;


/**
 * Route definition that starts job process that pools failed confirmations for next processing.
 *
 * @author Petr Juza
 */
@CamelConfiguration(value = ConfirmationsPoolRoute.ROUTE_BEAN)
@Profile(Profiles.PROD)
public class ConfirmationsPoolRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "confirmationsPoolRouteBean";

    private static final String JOB_NAME = "confirmationPool";

    /**
     * How often to run process for pooling failed confirmations (in seconds).
     */
    @ConfigurableValue(key = "ohf.asynch.confirmation.repeatTimeSec")
    private ConfigurationItem<Seconds> repeatTime;

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (repeatTime.getValue().getSeconds() * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;

        from("quartz2://" + uri)
                .routeId("confirmationsPool" + AbstractBasicRoute.ROUTE_SUFFIX)

                .bean("jobStarterForConfirmationPooling", "start");
    }
}
