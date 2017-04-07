package org.openhubframework.openhub.api.common.quartz;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;

/**
 * Defined one quartz {@link Job} with simple trigger ({@link QuartzSimpleTrigger}) or cron trigger
 * {@link QuartzCronTrigger}.
 * <p>
 * Annotation can be added on method and this method will be called when trigger will be executed.
 * </p>
 * <p>
 * Every job must have at least one {@link QuartzCronTrigger} or {@link QuartzSimpleTrigger}. And one job can have
 * one or more {@link QuartzCronTrigger}s and {@link QuartzSimpleTrigger}.
 * </p>
 * <p>
 * In cluster mode must be set if job will be execute in all nodes ({@link JobExecuteTypeInCluster#CONCURRENT}) or
 * only in one node ({@link JobExecuteTypeInCluster#NOT_CONCURRENT}) at the same time.
 * </p>
 * <p>
 * Example:
 * <pre>
 * {@code @QuartzJob}(name = "jobName", group = "groupName", executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
 *      simpleTriggers = @QuartzSimpleTrigger(repeatIntervalMillis = 1000))
 * public final void invokeJob() throws Exception {
 *
 * }
 * </pre>
 * This example show that will be create job with group <i>groupName</i> and name <i>jobName</i> and
 * method <i>invokeJob</i> will be called every 1000 millis in all nodes in cluster.
 * </p>
 *
 * @author Roman Havlicek
 * @see QuartzSimpleTrigger
 * @see QuartzCronTrigger
 * @see JobExecuteTypeInCluster
 * @see CronTriggerMisfireInstruction
 * @see SimpleTriggerMisfireInstruction
 * @see SimpleTriggerPropertyUnit
 * @since 2.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QuartzJob {

    /**
     * Job group name.
     *
     * @return group name
     */
    String group();

    /**
     * Job name.
     *
     * @return name
     */
    String name();

    /**
     * Job description.
     *
     * @return description
     */
    String description() default StringUtils.EMPTY;

    /**
     * Uses a Quartz {@link DisallowConcurrentExecution} instead of the default job.
     *
     * @return {@code true} uses {@link DisallowConcurrentExecution}, {@code false} - otherwise
     */
    boolean stateful() default false;

    /**
     * Gets execute type in cluster mode.
     *
     * @return execute type in cluster mode
     */
    JobExecuteTypeInCluster executeTypeInCluster();

    /**
     * All cron triggers definition for this job.
     *
     * @return cron triggers
     */
    QuartzCronTrigger[] cronTriggers() default {};

    /**
     * All simple triggers definition for this job.
     *
     * @return simple triggers
     */
    QuartzSimpleTrigger[] simpleTriggers() default {};
}
