package org.openhubframework.openhub.core.common.quartz.trigger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openhubframework.openhub.api.common.quartz.*;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.core.common.quartz.OpenHubQuartzJob;

/**
 * Test suite for {@link AnnotationMethodTriggerFactory}.
 *
 * @author Roman Havlicek
 * @see AnnotationMethodTriggerFactory
 * @since 2.0
 */
public class AnnotationMethodTriggerFactoryTest extends AbstractCoreTest {

    @Autowired
    private AnnotationMethodTriggerFactory annotationMethodTriggerFactory;

    /**
     * Test for method {@link AnnotationMethodTriggerFactory#createTriggers(JobExecuteTypeInCluster)} for
     * {@link JobExecuteTypeInCluster#CONCURRENT} triggers.
     *
     * @throws Exception all errors
     */
    @Test
    public void testCreateConcurrentTriggers() throws Exception {
        List<Trigger> triggers = annotationMethodTriggerFactory.createTriggers(JobExecuteTypeInCluster.CONCURRENT);
        assertThat(triggers.size(), is(5));

        Collections.sort(triggers, (o1, o2) -> o1.getKey().getName().compareTo(o2.getKey().getName()));

        assertThat(triggers.get(0), instanceOf(CronTrigger.class));
        assertThat(triggers.get(1), instanceOf(SimpleTrigger.class));
        assertThat(triggers.get(2), instanceOf(CronTrigger.class));
        assertThat(triggers.get(3), instanceOf(SimpleTrigger.class));
        assertThat(triggers.get(4), instanceOf(SimpleTrigger.class));

        CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
        assertThat(cronTrigger.getKey().getName(), is("FirstTriggerForJob"));
        assertThat(cronTrigger.getKey().getGroup(), is("MoreTriggerGroup"));
        assertThat(cronTrigger.getJobKey().getName(), is("MoreTriggerJob"));
        assertThat(cronTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(cronTrigger.getCronExpression(), is("0 00 23 ? * *"));
        assertThat(cronTrigger.getMisfireInstruction(), is(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY));

        SimpleTrigger simpleTrigger = (SimpleTrigger) triggers.get(1);
        assertThat(simpleTrigger.getKey().getName(), is("FourthTriggerForJob"));
        assertThat(simpleTrigger.getKey().getGroup(), is("MoreTriggerGroup"));
        assertThat(simpleTrigger.getJobKey().getName(), is("MoreTriggerJob"));
        assertThat(simpleTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(simpleTrigger.getRepeatInterval(), is(60000L));
        assertThat(simpleTrigger.getRepeatCount(), is(SimpleTrigger.REPEAT_INDEFINITELY));
        assertThat(simpleTrigger.getMisfireInstruction(), is(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW));

        cronTrigger = (CronTrigger) triggers.get(2);
        assertThat(cronTrigger.getKey().getName(), is("SecondTriggerForJob"));
        assertThat(cronTrigger.getKey().getGroup(), is("MoreTriggerGroup"));
        assertThat(cronTrigger.getJobKey().getName(), is("MoreTriggerJob"));
        assertThat(cronTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(cronTrigger.getCronExpression(), is("0 00 10 ? * *"));
        assertThat(cronTrigger.getMisfireInstruction(), is(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW));

        simpleTrigger = (SimpleTrigger) triggers.get(3);
        assertThat(simpleTrigger.getKey().getName(), is("ThirdTriggerForJob"));
        assertThat(simpleTrigger.getKey().getGroup(), is("MoreTriggerGroup"));
        assertThat(simpleTrigger.getJobKey().getName(), is("MoreTriggerJob"));
        assertThat(simpleTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(simpleTrigger.getRepeatInterval(), is(10000L));
        assertThat(simpleTrigger.getRepeatCount(), is(20));
        assertThat(simpleTrigger.getMisfireInstruction(), is(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY));

        simpleTrigger = (SimpleTrigger) triggers.get(4);
        assertThat(simpleTrigger.getKey().getName(), is("esbOpenHub_AsyncPartlyFailedJob_TRIGGER"));
        assertThat(simpleTrigger.getKey().getGroup(), is("DEFAULT"));
        assertThat(simpleTrigger.getJobKey().getName(), is("AsyncPartlyFailedJob"));
        assertThat(simpleTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(simpleTrigger.getRepeatInterval(), is(60000L));
        assertThat(simpleTrigger.getRepeatCount(), is(SimpleTrigger.REPEAT_INDEFINITELY));
        assertThat(simpleTrigger.getMisfireInstruction(), is(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY));
    }

    /**
     * Test for method {@link AnnotationMethodTriggerFactory#createTriggers(JobExecuteTypeInCluster)} for
     * {@link JobExecuteTypeInCluster#NOT_CONCURRENT} triggers.
     *
     * @throws Exception all errors
     */
    @Test
    public void testCreateNotConcurrentTriggers() throws Exception {
        List<Trigger> triggers = annotationMethodTriggerFactory.createTriggers(JobExecuteTypeInCluster.NOT_CONCURRENT);
        assertThat(triggers.size(), is(2));

        Collections.sort(triggers, (o1, o2) -> o1.getKey().getName().compareTo(o2.getKey().getName()));

        assertThat(triggers.get(0), instanceOf(SimpleTrigger.class));
        assertThat(triggers.get(1), instanceOf(CronTrigger.class));

        SimpleTrigger simpleTrigger = (SimpleTrigger) triggers.get(0);
        assertThat(simpleTrigger.getKey().getName(), is("esbOpenHub_AsyncPostponedJob_TRIGGER"));
        assertThat(simpleTrigger.getKey().getGroup(), is("DEFAULT"));
        assertThat(simpleTrigger.getJobKey().getName(), is("AsyncPostponedJob"));
        assertThat(simpleTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(simpleTrigger.getRepeatInterval(), is(30000L));
        assertThat(simpleTrigger.getRepeatCount(), is(SimpleTrigger.REPEAT_INDEFINITELY));
        assertThat(simpleTrigger.getMisfireInstruction(), is(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY));

        CronTrigger cronTrigger = (CronTrigger) triggers.get(1);
        assertThat(cronTrigger.getKey().getName(), is("esbOpenHub_AsyncRepairJob_TRIGGER"));
        assertThat(cronTrigger.getKey().getGroup(), is("DEFAULT"));
        assertThat(cronTrigger.getJobKey().getName(), is("AsyncRepairJob"));
        assertThat(cronTrigger.getJobKey().getGroup(), is(OpenHubQuartzJob.OPEN_HUB_JOB_GROUP_NAME));
        assertThat(cronTrigger.getCronExpression(), is("0 00 23 ? * *"));
        assertThat(cronTrigger.getMisfireInstruction(), is(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY));
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public BeanPostProcessor simpleMethodTriggerFactory() {
            return new AnnotationMethodTriggerFactory();
        }

        @Bean
        public TestFirstJob firstJob() {
            return new TestFirstJob();
        }

        @Bean
        public TestSecondJob secondJob() {
            return new TestSecondJob();
        }
    }

    //--------------------------------------------------- PRIVATE CLASS ------------------------------------------------

    private static class TestFirstJob {

        @OpenHubQuartzJob(name = "AsyncPartlyFailedJob", executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
                simpleTriggers = @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC,
                        intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS))
        public void invokePartlyFailedJob() {

        }

        @OpenHubQuartzJob(name = "AsyncPostponedJob", executeTypeInCluster = JobExecuteTypeInCluster.NOT_CONCURRENT,
                simpleTriggers = @QuartzSimpleTrigger(repeatIntervalMillis = 30000))
        public void invokePostponedJob() {

        }

        @OpenHubQuartzJob(name = "AsyncRepairJob", executeTypeInCluster = JobExecuteTypeInCluster.NOT_CONCURRENT,
                cronTriggers = @QuartzCronTrigger(cronExpression = "0 00 23 ? * *"))
        public void invokeRepairJob() {

        }
    }

    private static class TestSecondJob {

        @OpenHubQuartzJob(name = "MoreTriggerJob", executeTypeInCluster = JobExecuteTypeInCluster.CONCURRENT,
                cronTriggers = {
                        @QuartzCronTrigger(cronExpression = "0 00 23 ? * *",
                                name = "FirstTriggerForJob",
                                group = "MoreTriggerGroup"),
                        @QuartzCronTrigger(cronExpression = "0 00 10 ? * *",
                                misfireInstruction = CronTriggerMisfireInstruction.FIRE_ONCE_NOW,
                                name = "SecondTriggerForJob",
                                group = "MoreTriggerGroup")},
                simpleTriggers = {
                        @QuartzSimpleTrigger(repeatIntervalMillis = 10000,
                                repeatCount = 20,
                                name = "ThirdTriggerForJob",
                                group = "MoreTriggerGroup"),
                        @QuartzSimpleTrigger(repeatIntervalProperty = ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC,
                                intervalPropertyUnit = SimpleTriggerPropertyUnit.SECONDS,
                                misfireInstruction = SimpleTriggerMisfireInstruction.FIRE_NOW,
                                name = "FourthTriggerForJob",
                                group = "MoreTriggerGroup")
                })
        public void invokeJob() {

        }
    }
}