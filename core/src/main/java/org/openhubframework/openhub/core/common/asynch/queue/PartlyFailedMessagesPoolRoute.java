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

package org.openhubframework.openhub.core.common.asynch.queue;

import org.apache.camel.Handler;
import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.core.common.asynch.repair.RepairProcessingMsgRoute;
import org.openhubframework.openhub.core.common.asynch.stop.StopService;


/**
 * Route definition that starts job process that pools message queue (=database)
 * and takes {@link MsgStateEnum#PARTLY_FAILED} messages for further processing.
 *
 * @author Petr Juza
 */
@CamelConfiguration(value = PartlyFailedMessagesPoolRoute.ROUTE_BEAN)
@Profile("prod")
public class PartlyFailedMessagesPoolRoute extends SpringRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PartlyFailedMessagesPoolRoute.class);

    public static final String ROUTE_BEAN = "partlyFailedMsgPoolRouteBean";

    private static final String JOB_NAME = "partlyFailedPool";

    /**
     * How often to run process for polling partly failed messages (in seconds).
     */
    @Value("${asynch.partlyFailedRepeatTime}")
    private int partlyFailedRepeatTime;

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (partlyFailedRepeatTime * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;


        from("quartz2://" + uri)
                .routeId("partlyFailedMessageProcess" + AbstractBasicRoute.ROUTE_SUFFIX)

                // allow only if ESB not stopping
                .choice().when().method(ROUTE_BEAN, "isNotInStoppingMode")
                    .bean("jobStarterForMessagePooling", "start")
                .end();
    }

    /**
     * Checks if ESB goes down or not.
     *
     * @return {@code true} if ESB is in "stopping mode" otherwise {@code false}
     */
    @Handler
    public boolean isNotInStoppingMode() {
        StopService stopService = getApplicationContext().getBean(StopService.class);

        LOG.debug("ESB stopping mode is switched on: " + stopService.isStopping());

        return !stopService.isStopping();
    }
}
