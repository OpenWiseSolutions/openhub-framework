package org.openhubframework.openhub.core.common.quartz.jobfactory;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.MethodInvoker;

import org.openhubframework.openhub.core.common.quartz.QuartzUtils;

/**
 * Job factory extends {@link SpringBeanJobFactory} of create and set {@link MethodInvoker} from {@link JobDetail}
 * (parameters {@link QuartzUtils#JOB_BEAN_METHOD_NAME_PARAMETER} and {@link QuartzUtils#JOB_BEAN_NAME_PARAMETER})
 * into {@link MethodInvokingJobDetailFactoryBean.MethodInvokingJob}.
 *
 * @author Roman Havlicek
 * @see QuartzUtils#JOB_BEAN_NAME_PARAMETER
 * @see QuartzUtils#JOB_BEAN_METHOD_NAME_PARAMETER
 * @see MethodInvoker
 * @see JobDetail
 * @see MethodInvokingJobDetailFactoryBean.MethodInvokingJob
 * @since 2.0
 */
public final class BeanMethodJobFactory extends SpringBeanJobFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BeanMethodJobFactory.class);

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);

        if (job instanceof MethodInvokingJobDetailFactoryBean.MethodInvokingJob) {
            JobDetail jobDetail = bundle.getJobDetail();
            String jobBeanName = (String) jobDetail.getJobDataMap().get(QuartzUtils.JOB_BEAN_NAME_PARAMETER);
            String jobMethodName = (String) jobDetail.getJobDataMap().get(
                    QuartzUtils.JOB_BEAN_METHOD_NAME_PARAMETER);

            if (!StringUtils.isBlank(jobBeanName) && !StringUtils.isBlank(jobMethodName)) {

                Object jobBean = applicationContext.getBean(jobBeanName);

                LOG.debug("Create {} for job {} with bean name {} and method name {}.",
                        MethodInvoker.class, jobDetail.getKey().getName(), jobBeanName, jobMethodName);

                MethodInvoker methodInvoker = new MethodInvoker();
                methodInvoker.setTargetObject(jobBean);
                methodInvoker.setTargetMethod(jobMethodName);
                methodInvoker.prepare();

                ((MethodInvokingJobDetailFactoryBean.MethodInvokingJob) job).setMethodInvoker(methodInvoker);
            }
        }
        return job;
    }
}
