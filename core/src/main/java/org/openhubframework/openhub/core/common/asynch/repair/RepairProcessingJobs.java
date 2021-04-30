package org.openhubframework.openhub.core.common.asynch.repair;

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_REPAIR_REPEAT_TIME_SEC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.api.common.quartz.*;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;

/**
 * Jobs for for repair processing.
 * <p>
 * Contains two jobs:
 * </p>
 * <ul>
 * <li>
 * {@value #EXT_CALL_REPAIR_JOB_NAME} - call method {@link RepairExternalCallService#repairProcessingExternalCalls()}
 * Repeat interval for this job is load from configuration {@value CoreProps#ASYNCH_REPAIR_REPEAT_TIME_SEC}.
 * Job running concurrent in all nodes in cluster.
 * </li>
 * <li>
 * {@value #MESSAGE_REPAIR_JOB_NAME} - call method {@link RepairMessageService#repairProcessingMessages()}
 * Repeat interval for this job is load from configuration {@value CoreProps#ASYNCH_REPAIR_REPEAT_TIME_SEC}.
 * Job running concurrent in all nodes in cluster.
 * </li>
 * </ul>
 *
 * @author Roman Havlicek
 * @see RepairExternalCallService#repairProcessingExternalCalls()
 * @see RepairMessageService#repairProcessingMessages()
 * @see CoreProps#ASYNCH_REPAIR_REPEAT_TIME_SEC
 * @since 2.0
 */
@Profile(Profiles.PROD)
@Component
public class RepairProcessingJobs {

    /**
     * Name for external call repair job.
     */
    private static final String EXT_CALL_REPAIR_JOB_NAME = "extCallRepair";

    /**
     * Name for message repair job.
     */
    private static final String MESSAGE_REPAIR_JOB_NAME = "messageRepair";

    @Autowired
    private RepairExternalCallService repairExternalCallService;

    @Autowired
    private RepairMessageService repairMessageService;

    /**
     * Repairs external calls hooked in the state {@link ExternalCallStateEnum#PROCESSING}.
     * After a specified time these external calls are changed to {@link ExternalCallStateEnum#FAILED} state
     * without increasing failed count.
     */
    @OpenHubQuartzJob(name = EXT_CALL_REPAIR_JOB_NAME, executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_REPAIR_REPEAT_TIME_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
    public final void repairExtCallsJob() {
        repairExternalCallService.repairProcessingExternalCalls();
    }

    /**
     * Repairs messages hooked in the state {@link MsgStateEnum#PROCESSING}.
     * These messages are after specified time changed to {@link MsgStateEnum#PARTLY_FAILED} state
     * without increasing failed count.
     */
    @OpenHubQuartzJob(name = MESSAGE_REPAIR_JOB_NAME, executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
            simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_REPAIR_REPEAT_TIME_SEC,
                    intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
    public final void repairMessagesJob() {
        repairMessageService.repairProcessingMessages();
    }
}
