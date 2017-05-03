package org.openhubframework.openhub.api.common.quartz;

/**
 * Contains all possibility of running jobs in cluster mode.
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see QuartzCronTrigger
 * @see QuartzSimpleTrigger
 * @since 2.0
 */
public enum JobExecuteTypeInCluster {

    /**
     * Jobs are executed only on one node in cluster.
     */
    NOT_CONCURRENT,

    /**
     * Jobs are executed in all nodes in cluster at the same time.
     */
    CONCURRENT
}
