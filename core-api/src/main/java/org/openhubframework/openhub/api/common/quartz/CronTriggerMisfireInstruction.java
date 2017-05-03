package org.openhubframework.openhub.api.common.quartz;

import org.quartz.CronTrigger;

/**
 * Contains all misfire instruction for cron trigger defined in annotation {@link QuartzCronTrigger}.
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see QuartzCronTrigger
 * @see QuartzSimpleTrigger
 * @see CronTrigger
 * @since 2.0
 */
public enum CronTriggerMisfireInstruction {

    /**
     * See documentation in {@link CronTrigger#MISFIRE_INSTRUCTION_SMART_POLICY}.
     */
    SMART_POLICY(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY),

    /**
     * See documentation in {@link CronTrigger#MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY}.
     */
    IGNORE_MISFIRE_POLICY(CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY),

    /**
     * See documentation in {@link CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING}.
     */
    DO_NOTHING(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING),

    /**
     * See documentation in {@link CronTrigger#MISFIRE_INSTRUCTION_FIRE_ONCE_NOW}.
     */
    FIRE_ONCE_NOW(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);

    /**
     * Misfire instruction.
     */
    private final int misfireInstruction;

    /**
     * New instance.
     *
     * @param misfireInstruction misfire instruction
     */
    CronTriggerMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

    //----------------------------------------------------- SET / GET --------------------------------------------------

    /**
     * Gets misfire instruction.
     *
     * @return misfire instruction
     */
    public int getMisfireInstruction() {
        return misfireInstruction;
    }
}
