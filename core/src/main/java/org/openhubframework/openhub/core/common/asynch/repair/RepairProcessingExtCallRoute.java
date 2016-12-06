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

package org.openhubframework.openhub.core.common.asynch.repair;

import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.Profiles;


/**
 * Route definition that repairs external calls hooked in the state {@link ExternalCallStateEnum#PROCESSING}.
 * After a specified time these external calls are changed to {@link ExternalCallStateEnum#FAILED} state
 * without increasing failed count.
 */
@CamelConfiguration(value = RepairProcessingExtCallRoute.ROUTE_BEAN)
@Profile(Profiles.PROD)
public class RepairProcessingExtCallRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "repairProcessingExtCallRoute";

    public static final String JOB_GROUP_NAME = "esbOpenHub";

    private static final String JOB_NAME = "extCallRepair";

    /**
     * How often to run repair process (in seconds).
     */
    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;

    @Override
    public final void configure() throws Exception {
        // repair processing messages
        String uri = JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (repeatInterval * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;

        from("quartz2://" + uri)
                .routeId("repairExternalCallProcess" + AbstractBasicRoute.ROUTE_SUFFIX)

                .bean(RepairExternalCallService.BEAN, "repairProcessingExternalCalls");
    }
}
