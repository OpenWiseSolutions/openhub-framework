package org.openhubframework.openhub.core.common.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.core.common.quartz.jobfactory.BeanMethodJobFactory;
import org.openhubframework.openhub.core.common.quartz.trigger.AnnotationMethodTriggerFactory;
import org.openhubframework.openhub.core.config.QuartzConfig;

/**
 * Contains common constants and methods for quartz.
 *
 * @author Roman Havlicek
 * @see QuartzConfig
 * @since 2.0
 */
public final class QuartzUtils {

    /**
     * Parameter name for {@link JobDetail#getJobDataMap()} in which is saved bean name that contains
     * {@link QuartzJob} annotation definition.
     *
     * @see BeanMethodJobFactory
     * @see AnnotationMethodTriggerFactory
     */
    public static final String JOB_BEAN_NAME_PARAMETER = "jobBeanName";

    /**
     * Parameter name for {@link JobDetail#getJobDataMap()} in which is saved method name that contains
     * {@link QuartzJob} annotaion definition.
     *
     * @see BeanMethodJobFactory
     * @see AnnotationMethodTriggerFactory
     */
    public static final String JOB_BEAN_METHOD_NAME_PARAMETER = "jobBeanMethodName";

    /**
     * Suffix for trigger name.
     *
     * @see #createTriggerName(JobDetail, int)
     */
    public static final String TRIGGER_NAME_SUFFIX = "_TRIGGER";

    /**
     * No instance.
     */
    private QuartzUtils() {
    }

    /**
     * Create {@link Trigger} name from {@link JobDetail}.
     *
     * @param jobDetail    job detail
     * @param triggerOrder trigger order for same job
     * @return trigger name
     * @see #TRIGGER_NAME_SUFFIX
     */
    public static String createTriggerName(JobDetail jobDetail, int triggerOrder) {
        Assert.notNull(jobDetail, "jobDetail must not be null");

        return jobDetail.getKey().getGroup() + "_" + jobDetail.getKey().getName()
                + (triggerOrder > 1 ? "_" + triggerOrder : "") + TRIGGER_NAME_SUFFIX;
    }
}
