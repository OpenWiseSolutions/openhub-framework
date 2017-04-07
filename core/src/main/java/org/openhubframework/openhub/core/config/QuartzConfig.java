package org.openhubframework.openhub.core.config;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzCronTrigger;
import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.common.Tools;
import org.openhubframework.openhub.core.common.quartz.jobfactory.BeanMethodJobFactory;
import org.openhubframework.openhub.core.common.quartz.scheduler.DefaultScheduler;
import org.openhubframework.openhub.core.common.quartz.scheduler.QuartzSchedulerLifecycle;
import org.openhubframework.openhub.core.common.quartz.trigger.AnnotationMethodTriggerFactory;

/**
 * Custom configuration for quartz jobs.
 *
 * @author Roman Havlicek
 * @see Scheduler
 * @see QuartzJob
 * @see QuartzCronTrigger
 * @see QuartzSimpleTrigger
 * @see AnnotationMethodTriggerFactory
 * @see DefaultScheduler
 * @since 2.0
 */
@Profile(Profiles.PROD)
@Configuration
public class QuartzConfig {

    /**
     * Default startup delay for all {@link Scheduler}s in seconed.
     */
    private static final int STARTUP_DELAY = 30;

    /**
     * Quartz property name prefix.
     */
    private static final String QUARTZ_PROPERTY_PREFIX = "org.quartz";

    /**
     * Create job factory.
     *
     * @return job factory
     */
    @Bean
    public JobFactory jobFactory() {
        return new BeanMethodJobFactory();
    }

    /**
     * Lifecycle for scheduler that store information into database.
     *
     * @param scheduler scheduler
     * @return lifecycle for scheduler
     * @see #notConcurrentClusterJobScheduler(DataSource, ConfigurableEnvironment)
     */
    @Bean
    public QuartzSchedulerLifecycle notConcurrentSchedulerLifecycle(
            @Qualifier("notConcurrentClusterJobScheduler") Scheduler scheduler) {
        Assert.notNull(scheduler, "scheduler must not be null");

        return new QuartzSchedulerLifecycle(scheduler, STARTUP_DELAY);
    }

    /**
     * Lifecycle for scheduler that store information into memory.
     *
     * @param scheduler scheduler
     * @return lifecycle for scheduler
     * @see #concurrentClusterJobScheduler()
     */
    @Bean
    public QuartzSchedulerLifecycle concurrentSchedulerLifecycle(
            @Qualifier("concurrentClusterJobScheduler") Scheduler scheduler) {
        Assert.notNull(scheduler, "scheduler must not be null");

        return new QuartzSchedulerLifecycle(scheduler, STARTUP_DELAY);
    }

    /**
     * {@link Scheduler} that store information about jobs and triggers into memory.
     * This scheduler run jobs in all nodes in cluster at the same time.
     *
     * @return new scheduler
     */
    @Bean
    public Scheduler concurrentClusterJobScheduler() {
        return new DefaultScheduler("MEMORY_SCHEDULER", JobExecuteTypeInCluster.CONCURRENT, null);
    }

    /**
     * {@link Scheduler} that store information about jobs and triggers into database.
     * This scheduler run jobs only in one node in cluster at the same time.
     *
     * @param dataSource       data source
     * @param env environment
     * @return new scheduler
     */
    @Bean
    public Scheduler notConcurrentClusterJobScheduler(DataSource dataSource, ConfigurableEnvironment env) {
        Assert.notNull(dataSource, "dataSource must not be null");
        Assert.notNull(env, "env must not be null");

        return new DefaultScheduler("DATABASE_CLUSTER_SCHEDULER", JobExecuteTypeInCluster.NOT_CONCURRENT, dataSource,
                Tools.getPropertiesWithPrefix(env, QUARTZ_PROPERTY_PREFIX));
    }

    /**
     * Create {@link AnnotationMethodTriggerFactory}.
     *
     * @return trigger factory
     */
    @Bean
    public BeanPostProcessor simpleMethodTriggerFactory() {
        return new AnnotationMethodTriggerFactory();
    }
}
