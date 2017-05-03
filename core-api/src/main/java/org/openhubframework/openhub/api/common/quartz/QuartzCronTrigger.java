package org.openhubframework.openhub.api.common.quartz;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;

/**
 * Defined one {@link CronTrigger} in {@link QuartzJob}.
 * <p>
 * This type of trigger is used to fire a job at given moments in time, defined with Unix 'cron-like'
 * schedule definitions.
 * </p>
 * <p>
 * Cron expression when will be method called can be defined direct in annotation {@link #cronExpression()}
 * or in property {@link #cronExpressionProperty()}.
 * </p>
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see CronTrigger
 * @see QuartzSimpleTrigger
 * @see JobExecuteTypeInCluster
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzCronTrigger {

    /**
     * Gets group of trigger.
     * If group is empty, default group will be used.
     *
     * @return group
     */
    String group() default StringUtils.EMPTY;

    /**
     * Gets name of trigger.
     * If name is empty, default name will be used.
     *
     * @return name of trigger
     */
    String name() default StringUtils.EMPTY;

    /**
     * Cron expression when will be method called.
     *
     * @return cron expression
     */
    String cronExpression() default StringUtils.EMPTY;

    /**
     * Property name in which is saved cron expression when will be method called.
     *
     * @return property name
     */
    String cronExpressionProperty() default StringUtils.EMPTY;

    /**
     * Gets misfire instruction.
     *
     * @return misfire instruction type
     */
    CronTriggerMisfireInstruction misfireInstruction() default CronTriggerMisfireInstruction.SMART_POLICY;

}
