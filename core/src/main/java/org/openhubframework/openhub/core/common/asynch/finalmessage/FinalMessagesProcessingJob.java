/*
 * Copyright 2018 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.finalmessage;

import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessagesProcessingService;
import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.api.common.quartz.SimpleTriggerPropertyUnit;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

/**
 * Definition of job to invoke final messages handling.
 *
 * @author Karel Kovarik
 * @since 2.1
 * @see FinalMessagesProcessingService
 */
public class FinalMessagesProcessingJob {
    private static final Logger LOG = LoggerFactory.getLogger(FinalMessagesProcessingJob.class);

    /**
     * Unique job name.
     */
    static final String JOB_NAME = "core_FinalMessageProcessing";

    @Autowired
    private FinalMessagesProcessingService finalMessagesProcessingService;

    @OpenHubQuartzJob(
            name = JOB_NAME,
            executeTypeInCluster = JobExecuteTypeInCluster.NOT_CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(
                    repeatIntervalProperty = CoreProps.ASYNCH_FINAL_MESSAGES_PROCESSING_INTERVAL_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS)
    )
    public final void processFinalMessages() {
        LOG.trace("Start of job to process messages in final states.");
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        finalMessagesProcessingService.processMessages();
        stopWatch.stop();
        LOG.trace("Job finished successfully in '{}' millis.", stopWatch.getTotalTimeMillis());
    }

}
