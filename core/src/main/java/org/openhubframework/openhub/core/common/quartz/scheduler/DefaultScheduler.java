/*
 * Copyright (c) 2016 BSC Praha, spol. s r.o.
 */

package org.openhubframework.openhub.core.common.quartz.scheduler;

import java.util.*;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Calendar;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzCronTrigger;
import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.core.common.quartz.trigger.AnnotationMethodTriggerFactory;
import org.openhubframework.openhub.spi.quartz.TriggerFactory;

/**
 * Implementation of Quartz {@link Scheduler} that create {@link Trigger}s from {@link TriggerFactory}.
 * <p>
 * Scheduler has two constructors:
 * <ul>
 * <li>
 * {@link DefaultScheduler#DefaultScheduler(String, JobExecuteTypeInCluster, Properties)} - create scheduler that store
 * information about triggers and jobs into memory.
 * </li>
 * <li>
 * {@link DefaultScheduler#DefaultScheduler(String, JobExecuteTypeInCluster, DataSource, Properties)} - create scheduler
 * that store information about triggers and jobs into {@link DataSource}.
 * </li>
 * </ul>
 * </p>
 *
 * @author Roman Havlicek
 * @see TriggerFactory
 * @see QuartzJob
 * @see QuartzSimpleTrigger
 * @see QuartzCronTrigger
 * @see QuartzSchedulerLifecycle
 * @see AnnotationMethodTriggerFactory
 * @since 2.0
 */
public class DefaultScheduler implements Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultScheduler.class);

    /**
     * Scheduler name.
     */
    private final String schedulerName;

    /**
     * Execute type in cluster mode.
     */
    private final JobExecuteTypeInCluster jobExecuteType;

    /**
     * Data source in which is saved information about triggers and jobs.
     */
    private final DataSource dataSource;

    /**
     * Quartz configuration.
     */
    private final Properties quartzConfProperties;

    /**
     * Actual scheduler.
     */
    private Scheduler scheduler;

    /**
     * Job factory.
     */
    @Autowired
    private JobFactory jobFactory;

    /**
     * All trigger factories.
     */
    @Autowired(required = false)
    private List<TriggerFactory> triggerFactories;

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * Task executor.
     */
    @Autowired(required = false)
    private SchedulingTaskExecutor taskExecutor;

    /**
     * New instance that save information about jobs and triggers into memory.ø
     *
     * @param schedulerName        name of scheduler
     * @param jobExecuteType       execute type jobs in cluster
     *                             ({@link TriggerFactory#createTriggers(JobExecuteTypeInCluster)})
     * @param quartzConfProperties quartz configuration
     */
    public DefaultScheduler(String schedulerName, JobExecuteTypeInCluster jobExecuteType,
                            @Nullable Properties quartzConfProperties) {
        Assert.hasText(schedulerName, "schedulerName must not be empty");
        Assert.notNull(jobExecuteType, "jobExecuteType must not be null");

        this.schedulerName = schedulerName;
        this.jobExecuteType = jobExecuteType;
        this.dataSource = null;
        this.quartzConfProperties = quartzConfProperties;
    }

    /**
     * New instance that save information about jobs and triggers into data source.
     *
     * @param schedulerName        name of scheduler
     * @param jobExecuteType       execute type jobs in cluster
     *                             ({@link TriggerFactory#createTriggers(JobExecuteTypeInCluster)})
     * @param dataSource           data source in which will be save information about jobs and triggers
     * @param quartzConfProperties quartz configuration
     */
    public DefaultScheduler(String schedulerName, JobExecuteTypeInCluster jobExecuteType,
                            DataSource dataSource, @Nullable Properties quartzConfProperties) {
        Assert.hasText(schedulerName, "schedulerName must not be empty");
        Assert.notNull(jobExecuteType, "jobExecuteType must not be null");
        Assert.notNull(dataSource, "dataSource must not be null");

        this.schedulerName = schedulerName;
        this.jobExecuteType = jobExecuteType;
        this.dataSource = dataSource;
        this.quartzConfProperties = quartzConfProperties;
    }

    /**
     * Create new instance of {@link Scheduler}.
     *
     * @return scheduler
     */
    protected synchronized Scheduler createScheduler() {
        try {
            LOG.info("Create scheduler {}.", schedulerName);

            SchedulerFactoryBean result = new SchedulerFactoryBean();
            result.setJobFactory(jobFactory);
            if (dataSource != null) {
                result.setDataSource(dataSource);
            }
            if (quartzConfProperties != null) {
                result.setQuartzProperties(quartzConfProperties);
            }
            if (taskExecutor != null) {
                result.setTaskExecutor(taskExecutor);
            }
            result.setAutoStartup(false);
            result.setSchedulerName(schedulerName);
            result.setWaitForJobsToCompleteOnShutdown(true);
            result.setOverwriteExistingJobs(true);

            //create triggers
            List<Trigger> triggers = new LinkedList<>();
            if (!CollectionUtils.isEmpty(triggerFactories)) {
                for (TriggerFactory triggerFactory : triggerFactories) {
                    triggers.addAll(triggerFactory.createTriggers(getJobExecuteType()));
                }
            }
            if (!triggers.isEmpty()) {
                result.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
            }

            // autowire all and initialize the bean
            ctx.getAutowireCapableBeanFactory().autowireBean(result);
            ctx.getAutowireCapableBeanFactory().initializeBean(result, schedulerName + "_FACTORY");

            return result.getObject();
        } catch (Exception e) {
            throw new IllegalStateException("Error in creating scheduler. Error: " + e.getMessage());
        }
    }

    /**
     * Gets new or existing instance of {@link Scheduler}.
     *
     * @return scheduler
     */
    protected Scheduler getScheduler() {
        if (scheduler == null) {
            scheduler = createScheduler();
        }
        return scheduler;
    }

    @Override
    public String getSchedulerName() throws SchedulerException {
        return getScheduler().getSchedulerName();
    }

    @Override
    public String getSchedulerInstanceId() throws SchedulerException {
        return getScheduler().getSchedulerInstanceId();
    }

    @Override
    public SchedulerContext getContext() throws SchedulerException {
        return getScheduler().getContext();
    }

    @Override
    public void start() throws SchedulerException {
        getScheduler().start();
    }

    @Override
    public void startDelayed(int seconds) throws SchedulerException {
        getScheduler().startDelayed(seconds);
    }

    @Override
    public boolean isStarted() throws SchedulerException {
        return getScheduler().isStarted();
    }

    @Override
    public void standby() throws SchedulerException {
        getScheduler().standby();
    }

    @Override
    public boolean isInStandbyMode() throws SchedulerException {
        return getScheduler().isInStandbyMode();
    }

    @Override
    public void shutdown() throws SchedulerException {
        getScheduler().shutdown();
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) throws SchedulerException {
        getScheduler().shutdown(waitForJobsToComplete);
    }

    @Override
    public boolean isShutdown() throws SchedulerException {
        return getScheduler().isShutdown();
    }

    @Override
    public SchedulerMetaData getMetaData() throws SchedulerException {
        return getScheduler().getMetaData();
    }

    @Override
    public List<JobExecutionContext> getCurrentlyExecutingJobs() throws SchedulerException {
        return getScheduler().getCurrentlyExecutingJobs();
    }

    @Override
    public void setJobFactory(JobFactory factory) throws SchedulerException {
        getScheduler().setJobFactory(factory);
    }

    @Override
    public ListenerManager getListenerManager() throws SchedulerException {
        return getScheduler().getListenerManager();
    }

    @Override
    public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return getScheduler().scheduleJob(jobDetail, trigger);
    }

    @Override
    public Date scheduleJob(Trigger trigger) throws SchedulerException {
        return getScheduler().scheduleJob(trigger);
    }

    @Override
    public void scheduleJobs(Map<JobDetail, Set<? extends Trigger>> triggersAndJobs, boolean replace) throws SchedulerException {
        getScheduler().scheduleJobs(triggersAndJobs, replace);
    }

    @Override
    public void scheduleJob(JobDetail jobDetail, Set<? extends Trigger> triggersForJob, boolean replace) throws SchedulerException {
        getScheduler().scheduleJob(jobDetail, triggersForJob, replace);
    }

    @Override
    public boolean unscheduleJob(TriggerKey triggerKey) throws SchedulerException {
        return getScheduler().unscheduleJob(triggerKey);
    }

    @Override
    public boolean unscheduleJobs(List<TriggerKey> triggerKeys) throws SchedulerException {
        return getScheduler().unscheduleJobs(triggerKeys);
    }

    @Override
    public Date rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
        return getScheduler().rescheduleJob(triggerKey, newTrigger);
    }

    @Override
    public void addJob(JobDetail jobDetail, boolean replace) throws SchedulerException {
        getScheduler().addJob(jobDetail, replace);
    }

    @Override
    public void addJob(JobDetail jobDetail, boolean replace, boolean storeNonDurableWhileAwaitingScheduling) throws SchedulerException {
        getScheduler().addJob(jobDetail, replace, storeNonDurableWhileAwaitingScheduling);
    }

    @Override
    public boolean deleteJob(JobKey jobKey) throws SchedulerException {
        return getScheduler().deleteJob(jobKey);
    }

    @Override
    public boolean deleteJobs(List<JobKey> jobKeys) throws SchedulerException {
        return getScheduler().deleteJobs(jobKeys);
    }

    @Override
    public void triggerJob(JobKey jobKey) throws SchedulerException {
        getScheduler().triggerJob(jobKey);
    }

    @Override
    public void triggerJob(JobKey jobKey, JobDataMap data) throws SchedulerException {
        getScheduler().triggerJob(jobKey, data);
    }

    @Override
    public void pauseJob(JobKey jobKey) throws SchedulerException {
        getScheduler().pauseJob(jobKey);
    }

    @Override
    public void pauseJobs(GroupMatcher<JobKey> matcher) throws SchedulerException {
        getScheduler().pauseJobs(matcher);
    }

    @Override
    public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
        getScheduler().pauseTrigger(triggerKey);
    }

    @Override
    public void pauseTriggers(GroupMatcher<TriggerKey> matcher) throws SchedulerException {
        getScheduler().pauseTriggers(matcher);
    }

    @Override
    public void resumeJob(JobKey jobKey) throws SchedulerException {
        getScheduler().resumeJob(jobKey);
    }

    @Override
    public void resumeJobs(GroupMatcher<JobKey> matcher) throws SchedulerException {
        getScheduler().resumeJobs(matcher);
    }

    @Override
    public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
        getScheduler().resumeTrigger(triggerKey);
    }

    @Override
    public void resumeTriggers(GroupMatcher<TriggerKey> matcher) throws SchedulerException {
        getScheduler().resumeTriggers(matcher);
    }

    @Override
    public void pauseAll() throws SchedulerException {
        getScheduler().pauseAll();
    }

    @Override
    public void resumeAll() throws SchedulerException {
        getScheduler().resumeAll();
    }

    @Override
    public List<String> getJobGroupNames() throws SchedulerException {
        return getScheduler().getJobGroupNames();
    }

    @Override
    public Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher) throws SchedulerException {
        return getScheduler().getJobKeys(matcher);
    }

    @Override
    public List<? extends Trigger> getTriggersOfJob(JobKey jobKey) throws SchedulerException {
        return getScheduler().getTriggersOfJob(jobKey);
    }

    @Override
    public List<String> getTriggerGroupNames() throws SchedulerException {
        return getScheduler().getTriggerGroupNames();
    }

    @Override
    public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher) throws SchedulerException {
        return getScheduler().getTriggerKeys(matcher);
    }

    @Override
    public Set<String> getPausedTriggerGroups() throws SchedulerException {
        return getScheduler().getPausedTriggerGroups();
    }

    @Override
    public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
        return getScheduler().getJobDetail(jobKey);
    }

    @Override
    public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
        return getScheduler().getTrigger(triggerKey);
    }

    @Override
    public TriggerState getTriggerState(TriggerKey triggerKey) throws SchedulerException {
        return getScheduler().getTriggerState(triggerKey);
    }

    @Override
    public void resetTriggerFromErrorState(final TriggerKey triggerKey) throws SchedulerException {
        getScheduler().resetTriggerFromErrorState(triggerKey);
    }

    @Override
    public void addCalendar(String calName, Calendar calendar, boolean replace, boolean updateTriggers) throws SchedulerException {
        getScheduler().addCalendar(calName, calendar, replace, updateTriggers);
    }

    @Override
    public boolean deleteCalendar(String calName) throws SchedulerException {
        return getScheduler().deleteCalendar(calName);
    }

    @Override
    public Calendar getCalendar(String calName) throws SchedulerException {
        return getScheduler().getCalendar(calName);
    }

    @Override
    public List<String> getCalendarNames() throws SchedulerException {
        return getScheduler().getCalendarNames();
    }

    @Override
    public boolean interrupt(JobKey jobKey) throws UnableToInterruptJobException {
        return getScheduler().interrupt(jobKey);
    }

    @Override
    public boolean interrupt(String fireInstanceId) throws UnableToInterruptJobException {
        return getScheduler().interrupt(fireInstanceId);
    }

    @Override
    public boolean checkExists(JobKey jobKey) throws SchedulerException {
        return getScheduler().checkExists(jobKey);
    }

    @Override
    public boolean checkExists(TriggerKey triggerKey) throws SchedulerException {
        return getScheduler().checkExists(triggerKey);
    }

    @Override
    public void clear() throws SchedulerException {
        getScheduler().clear();
    }

    /**
     * Gets cluster execute type.
     *
     * @return cluster execute type
     */
    public JobExecuteTypeInCluster getJobExecuteType() {
        return jobExecuteType;
    }

}
