package org.openhubframework.openhub.api.common.quartz;

import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

/**
 * Contains all time unit in which can be saved repeat interval for {@link QuartzSimpleTrigger} in property
 * ({@link QuartzSimpleTrigger#repeatIntervalProperty()}).
 * <p>
 * Unit can be set in {@link QuartzSimpleTrigger#intervalPropertyUnit()}.
 * </p>
 *
 * @author Roman Havlicek
 * @see QuartzJob
 * @see QuartzSimpleTrigger#intervalPropertyUnit()
 * @since 2.0
 */
public enum SimpleTriggerPropertyUnit {

    /**
     * Interval in property {@link QuartzSimpleTrigger#repeatIntervalProperty()} is saved in millis.
     */
    MILLIS(new MillisConverter() {
        @Override
        public long convertIntervalToMillis(long interval) {
            return interval;
        }
    }),

    /**
     * Interval in property {@link QuartzSimpleTrigger#repeatIntervalProperty()} is saved in seconds.
     */
    SECONDS(new MillisConverter() {
        @Override
        public long convertIntervalToMillis(long interval) {
            return TimeUnit.SECONDS.toMillis(interval);
        }
    }),

    /**
     * Interval in property {@link QuartzSimpleTrigger#repeatIntervalProperty()} is saved in minutes.
     */
    MINUTES(new MillisConverter() {
        @Override
        public long convertIntervalToMillis(long interval) {
            return TimeUnit.MINUTES.toMillis(interval);
        }
    });

    /**
     * Converter time interval into millis.
     */
    private final MillisConverter millisConverter;

    /**
     * New instance.
     *
     * @param millisConverter converter time unit into millis
     */
    SimpleTriggerPropertyUnit(MillisConverter millisConverter) {
        Assert.notNull(millisConverter, "millisConverter must not be null");

        this.millisConverter = millisConverter;
    }

    /**
     * Gets time unit interval in millis.
     *
     * @param interval unit repeat interval (in millis, or seconds...)
     * @return repeat interval in millis
     */
    public long getIntervalInMillis(long interval) {
        return millisConverter.convertIntervalToMillis(interval);
    }

    //---------------------------------------------- PRIVATE CLASS -----------------------------------------------------

    /**
     * Converter for convert repeat interval in millis, seconds or minutes into millis.
     */
    private interface MillisConverter {

        /**
         * Convert time repeat interval into millis.
         *
         * @param interval time repeat interval in millis, seconds or minutes...
         * @return interval in millis
         */
        long convertIntervalToMillis(long interval);
    }
}
