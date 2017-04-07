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

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_CONFIRMATION_REPEAT_TIME_SEC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.api.common.quartz.*;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;


/**
 * Job that starts job process that pools failed confirmations for next processing.
 * <p>
 * Repeat interval for this job is load from configuration {@value CoreProps#ASYNCH_CONFIRMATION_REPEAT_TIME_SEC}.
 * Job running concurrent in all nodes in cluster at the same time.
 * </p>
 *
 * @author Petr Juza
 */
@Profile(Profiles.PROD)
@Component
public class ConfirmationsPoolJob {

    /**
     * Name for job.
     */
    private static final String JOB_NAME = "confirmationPool";

    @Autowired
    private JobStarterForConfirmationPooling jobStarterForConfirmationPooling;

    /**
     * Invoke job for process failed confirmations.
     *
     * @throws Exception error during executing job
     */
    @OpenHubQuartzJob(name = JOB_NAME, executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_CONFIRMATION_REPEAT_TIME_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
    public final void invokeJob() throws Exception {
        jobStarterForConfirmationPooling.start();
    }
}
