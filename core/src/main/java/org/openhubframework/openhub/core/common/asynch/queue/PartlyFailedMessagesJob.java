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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.api.common.quartz.SimpleTriggerPropertyUnit;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * Route definition that starts job process that pools message queue (=database)
 * and takes {@link MsgStateEnum#PARTLY_FAILED} messages for further processing.
 * <p>
 * Repeat interval for this job is load from configuration {@value CoreProps#ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC}.
 * Job running concurrent in all nodes in cluster at the same time.
 * </p>
 *
 * @author Petr Juza
 * @see JobStarterForMessagePooling#start()
 * @see CoreProps#ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC
 */
@Profile(Profiles.PROD)
@Component
public class PartlyFailedMessagesJob {

    private static final String JOB_NAME = "partlyFailedPool";

    @Autowired
    private NodeService nodeService;

    @Autowired
    private JobStarterForMessagePooling jobStarterForMessagePooling;

    /**
     * Invoke job for process {@link MsgStateEnum#PARTLY_FAILED} messages
     *
     * @throws Exception
     */
    @OpenHubQuartzJob(name = JOB_NAME, executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
    public final void invokeJob() throws Exception {
        if (nodeService.getActualNode().isAbleToHandleExistingMessages()) {
            jobStarterForMessagePooling.start();
        }
    }
}
