package org.openhubframework.openhub.api.common.quartz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SimpleTrigger;

/**
 * Defined one {@link SimpleTrigger} in {@link QuartzJob}.
 * <p>
 * This type of trigger is used to fire a job repeated at a specified interval.
 * </p>
 * <p>
 * Repeat interval when will be method called can be defined direct in annotation {@link #repeatIntervalMillis()}.
 * or in property {@link #repeatIntervalProperty()}.
 * If repeat interval is saved in property, we can set time unit in which is repeat interval saved
 * ({@link #intervalPropertyUnit()}).
 * </p>
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see SimpleTrigger
 * @see QuartzCronTrigger
 * @see JobExecuteTypeInCluster
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzSimpleTrigger {

    /**
     * No repeat interval in {@link #repeatIntervalMillis()}.
     */
    static final long EMPTY_REPEAT_INTERVAL = -1;

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
     * Gets repeat interval.
     *
     * @return repeat interval
     */
    long repeatIntervalMillis() default EMPTY_REPEAT_INTERVAL;

    /**
     * Property name in which is repeat interval when will be method called.
     *
     * @return property name
     */
    String repeatIntervalProperty() default StringUtils.EMPTY;

    /**
     * Gets unit in which is saved repeat interval in property ({@link #repeatIntervalProperty()}).
     *
     * @return unit
     */
    SimpleTriggerPropertyUnit intervalPropertyUnit() default SimpleTriggerPropertyUnit.MILLIS;

    /**
     * Gets repeat count - {@value SimpleTrigger#REPEAT_INDEFINITELY} repeat indefinitely.
     *
     * @return repeat count
     */
    int repeatCount() default SimpleTrigger.REPEAT_INDEFINITELY;

    /**
     * Gets misfire instruction.
     *
     * @return misfire instruction
     */
    SimpleTriggerMisfireInstruction misfireInstruction() default SimpleTriggerMisfireInstruction.SMART_POLICY;
}
