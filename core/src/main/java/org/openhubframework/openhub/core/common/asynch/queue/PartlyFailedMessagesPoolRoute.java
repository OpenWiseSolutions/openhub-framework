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

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC;

import org.apache.camel.Handler;
import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.common.time.Seconds;
import org.openhubframework.openhub.core.common.asynch.repair.RepairProcessingMsgRoute;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * Route definition that starts job process that pools message queue (=database)
 * and takes {@link MsgStateEnum#PARTLY_FAILED} messages for further processing.
 *
 * @author Petr Juza
 */
@CamelConfiguration(value = PartlyFailedMessagesPoolRoute.ROUTE_BEAN)
@Profile(Profiles.PROD)
public class PartlyFailedMessagesPoolRoute extends SpringRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PartlyFailedMessagesPoolRoute.class);

    public static final String ROUTE_BEAN = "partlyFailedMsgPoolRouteBean";

    private static final String JOB_NAME = "partlyFailedPool";

    static final String ROUTE_ID = "partlyFailedMessageProcess" + AbstractBasicRoute.ROUTE_SUFFIX;

    /**
     * How often to run process for polling partly failed messages (in seconds).
     */
    @ConfigurableValue(key = ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC)
    private ConfigurationItem<Seconds> partlyFailedRepeatTime;

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (partlyFailedRepeatTime.getValue().getSeconds() * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;


        from("quartz2://" + uri)
                .routeId(ROUTE_ID)

                // allow only if ESB not stopping
                .choice().when().method(ROUTE_BEAN, "isAbleToHandleExistingMessages")
                    .bean("jobStarterForMessagePooling", "start")
                .end();
    }

    /**
     * Checks if actual node handles existing message.
     *
     * @return {@code true} is handles existing message, {@code false} - otherwise
     */
    @Handler
    public boolean isAbleToHandleExistingMessages() {
        NodeService nodeService = getApplicationContext().getBean(NodeService.class);

        boolean result = nodeService.getActualNode().isAbleToHandleExistingMessages();

        if (result) {
            LOG.debug("Node is able to handle existing message.");
        } else {
            LOG.debug("Node is not able to handle message. Node is stopped.");
        }
        return result;
    }
}
