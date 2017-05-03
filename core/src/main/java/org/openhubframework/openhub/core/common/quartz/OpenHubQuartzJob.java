package org.openhubframework.openhub.core.common.quartz;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzCronTrigger;
import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;

/**
 * Defined one quartz {@link Job} with default group name {@value #OPEN_HUB_JOB_GROUP_NAME}.
 * For base use see in {@link QuartzJob}.
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see QuartzCronTrigger
 * @see QuartzSimpleTrigger
 * @since 2.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@QuartzJob(name = StringUtils.EMPTY, group = OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME,
        executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT)
public @interface OpenHubQuartzJob {

    /**
     * Default group name.
     */
    static final String OPEN_HUB_JOB_GROUP_NAME = "esbOpenHub";

    /**
     * Job name.
     *
     * @return name
     * @see QuartzJob#name()
     */
    String name();

    /**
     * Job description.
     *
     * @return description
     * @see QuartzJob#description()
     */
    String description() default StringUtils.EMPTY;

    /**
     * Uses a Quartz {@link DisallowConcurrentExecution} instead of the default job.
     *
     * @return {@code true} uses {@link DisallowConcurrentExecution}, {@code false} - otherwise
     * @see QuartzJob#stateful()
     */
    boolean stateful() default false;

    /**
     * Gets execute type in cluster mode.
     *
     * @return execute type in cluster mode
     * @see QuartzJob#executeTypeInCluster()
     */
    JobExecuteTypeInCluster executeTypeInCluster();

    /**
     * All cron triggers definition for this job.
     *
     * @return cron triggers
     * @see QuartzJob#cronTriggers()
     */
    QuartzCronTrigger[] cronTriggers() default {};

    /**
     * All simple triggers definition for this job.
     *
     * @return simple triggers
     * @see QuartzJob#simpleTriggers()
     */
    QuartzSimpleTrigger[] simpleTriggers() default {};
}
