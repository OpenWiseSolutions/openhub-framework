package org.openhubframework.openhub.spi.quartz;

import java.util.List;

import org.quartz.Trigger;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzCronTrigger;
import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;

/**
 * Factory class for creating new {@link Trigger}s instance.
 * <p>
 * Every trigger instance is able to running concurrent in all nodes in cluster at the same time
 * ({@link JobExecuteTypeInCluster#CONCURRENT}) or in exact node in
 * cluster ({@link JobExecuteTypeInCluster#NOT_CONCURRENT}).
 * This execution type can be set in job definition {@link QuartzJob#executeTypeInCluster()}.
 * </p>
 *
 * @author Roman Havlicek
 * @see Trigger
 * @see JobExecuteTypeInCluster
 * @see QuartzJob
 * @see QuartzSimpleTrigger
 * @see QuartzCronTrigger
 * @since 2.0
 */
public interface TriggerFactory {

    /**
     * Create new {@link Trigger}s instances for cluster execute type.
     *
     * @param jobExecuteTypeInCluster execute type for which {@link Trigger}s will be created
     * @return new {@link Trigger}s
     */
    List<Trigger> createTriggers(JobExecuteTypeInCluster jobExecuteTypeInCluster);
}
