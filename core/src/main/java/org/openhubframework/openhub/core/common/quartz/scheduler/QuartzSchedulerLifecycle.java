package org.openhubframework.openhub.core.common.quartz.scheduler;

import javax.annotation.Nullable;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.SchedulingException;
import org.springframework.util.Assert;

/**
 * Lifecycle for quartz {@link Scheduler}.
 * <p>
 * Scheduler is started after calling method {@link #start()} and after event {@link ApplicationReadyEvent}
 * is handled.
 * </p>
 *
 * @author Roman Havlicek
 * @see Scheduler
 * @see DefaultScheduler
 * @see ApplicationReadyEvent
 * @since 2.0
 */
public class QuartzSchedulerLifecycle implements SmartLifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzSchedulerLifecycle.class);

    /**
     * Scheduler for this lifecycle.
     */
    private final Scheduler scheduler;

    /**
     * {@code true} scheduler start automatically, {@code false} - scheduler start after someone call method
     * {@link #start()}.
     */
    private boolean autoStartup = true;

    /**
     * {@code true} method {@link #start()} was called, {@code false} - otherwise.
     */
    private boolean startCalled = false;

    /**
     * {@code true} event {@link ApplicationReadyEvent} was handled, {@code false} - otherwise.
     */
    private boolean applicationReadyEventHandled = false;

    /**
     * {@code true} - {@link Scheduler} was started, {@code false} - otherwise.
     */
    private boolean schedulerStarted = false;

    /**
     * Phase order.
     */
    private int phase = Integer.MAX_VALUE;

    /**
     * Delay for start {@link Scheduler} in seconds.
     */
    private Integer startupDelay;

    /**
     * New instance.
     * No delay for start {@link Scheduler}.
     *
     * @param scheduler scheduler
     */
    public QuartzSchedulerLifecycle(Scheduler scheduler) {
        this(scheduler, null);
    }

    /**
     * New instance.
     *
     * @param scheduler    scheduler
     * @param startupDelay delay for start {@link Scheduler} in second
     */
    public QuartzSchedulerLifecycle(Scheduler scheduler, @Nullable Integer startupDelay) {
        Assert.notNull(scheduler, "scheduler must not be null");

        this.scheduler = scheduler;
        this.startupDelay = startupDelay;
    }

    @Override
    public void stop(Runnable callback) {
        Assert.notNull(callback, "callback must not be null");

        if (schedulerStarted) {
            stop();
            callback.run();
        }
    }

    @Override
    public void start() {
        startCalled = true;
        startScheduler();
    }

    /**
     * Start scheduler after method {@link #start()} was called and {@link ApplicationReadyEvent} was handled.
     */
    private void startScheduler() {
        if (startCalled && applicationReadyEventHandled) {
            try {
                if (getStartupDelay() == null) {
                    LOG.info("Starting Quartz Scheduler '{}'.", scheduler.getSchedulerName());
                    scheduler.start();
                } else {
                    LOG.info("Starting Quartz Scheduler '{}' with delay {}s.", scheduler.getSchedulerName(),
                            getStartupDelay());
                    scheduler.startDelayed(getStartupDelay());
                }
                schedulerStarted = true;
            } catch (SchedulerException ex) {
                throw new SchedulingException("Could not start Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public void stop() {
        if (schedulerStarted) {
            try {
                this.scheduler.standby();
            } catch (SchedulerException ex) {
                throw new SchedulingException("Could not stop Quartz Scheduler", ex);
            }
        }
    }

    @Override
    public boolean isRunning() {
        try {
            return schedulerStarted && !scheduler.isInStandbyMode();
        } catch (SchedulerException ex) {
            return false;
        }
    }

    /**
     * Handled {@link ApplicationReadyEvent} and start scheduler (if method {@link #start()} was called).
     *
     * @param event event
     */
    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        Assert.notNull(event, "event must not be null");

        applicationReadyEventHandled = true;
        startScheduler();
    }

    //---------------------------------------------- SET / GET ---------------------------------------------------------

    /**
     * Set phase.
     *
     * @param phase phase
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    /**
     * Set automatically start {@link Scheduler}.
     *
     * @param autoStartup {@code true} scheduler start automatically,
     *                    {@code false} - scheduler start after someone call method {@link #start()}
     */
    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    /**
     * Gets delay of {@link Scheduler} start.
     *
     * @return delay, {@code NULL} - {@link Scheduler} start immediately
     */
    @Nullable
    public Integer getStartupDelay() {
        return startupDelay;
    }

    /**
     * Sets delay of {@link Scheduler} start.
     *
     * @param startupDelay delay, {@code NULL} - {@link Scheduler} start immediately
     */
    public void setStartupDelay(@Nullable Integer startupDelay) {
        this.startupDelay = startupDelay;
    }
}
