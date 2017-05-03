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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.api.common.quartz.SimpleTriggerPropertyUnit;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;


/**
 * Job definition that starts checking alerts by scheduler.
 * <p>
 * Repeat interval for this job is load from configuration {@value CoreProps#ALERTS_REPEAT_TIME_SEC}.
 * Job running concurrent in all nodes in cluster.
 * </p>
 *
 * @author Petr Juza
 * @see AlertsCheckingService#checkAlerts()
 * @see CoreProps#ALERTS_REPEAT_TIME_SEC
 * @since 0.4
 */
@Profile(Profiles.PROD)
@Component
public class AlertsSchedulerJob {

    /**
     * Name for check alerts job.
     */
    private static final String JOB_NAME = "alerts";

    @Autowired
    private AlertsCheckingService alertsCheckingService;

    /**
     * Invoke job for checking alerts.
     */
    @OpenHubQuartzJob(name = JOB_NAME, executeTypeInCluster = JobExecuteTypeInCluster.NOT_CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ALERTS_REPEAT_TIME_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
    public final void invokeJob() {
        alertsCheckingService.checkAlerts();
    }
}
