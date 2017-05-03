package org.openhubframework.openhub.api.common.quartz;

import org.quartz.SimpleTrigger;

/**
 * Contains all misfire instruction for simple trigger defined in annotation {@link QuartzSimpleTrigger}.
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see QuartzCronTrigger
 * @see QuartzSimpleTrigger
 * @see SimpleTrigger
 * @since 2.0
 */
public enum SimpleTriggerMisfireInstruction {

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_SMART_POLICY}.
     */
    SMART_POLICY(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY}.
     */
    IGNORE_MISFIRE_POLICY(SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_FIRE_NOW}.
     */
    FIRE_NOW(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT}.
     */
    RESCHEDULE_NEXT_WITH_EXISTING_COUNT(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT}.
     */
    RESCHEDULE_NEXT_WITH_REMAINING_COUNT(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT}.
     */
    RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT(
            SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT),

    /**
     * See documentation in {@link SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT}.
     */
    RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT(
            SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT);

    /**
     * Misfire instruction.
     */
    private final int misfireInstruction;

    /**
     * New instance.
     *
     * @param misfireInstruction misfire instruction
     */
    SimpleTriggerMisfireInstruction(int misfireInstruction) {
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
