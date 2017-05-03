package org.openhubframework.openhub.core.common.quartz.trigger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import org.openhubframework.openhub.api.common.quartz.JobExecuteTypeInCluster;
import org.openhubframework.openhub.api.common.quartz.QuartzCronTrigger;
import org.openhubframework.openhub.api.common.quartz.QuartzJob;
import org.openhubframework.openhub.api.common.quartz.QuartzSimpleTrigger;
import org.openhubframework.openhub.common.reflection.AnnotationMethodFilter;
import org.openhubframework.openhub.core.common.quartz.QuartzUtils;
import org.openhubframework.openhub.core.configuration.ConfigurationService;
import org.openhubframework.openhub.spi.quartz.TriggerFactory;

/**
 * Implementation {@link TriggerFactory} that create {@link Trigger}s from annotation {@link QuartzJob},
 * {@link QuartzCronTrigger} and {@link QuartzSimpleTrigger}.
 * <p>
 * This implementation implements {@link BeanPostProcessor} and search methods which have
 * {@link QuartzJob} annotation on every spring bean.
 * When method {@link #createTriggers(JobExecuteTypeInCluster)} is called, then from every found methods is
 * create {@link Trigger}s.
 * </p>
 *
 * @author Roman Havlicek
 * @see TriggerFactory
 * @see Trigger
 * @see QuartzJob
 * @see QuartzSimpleTrigger
 * @see QuartzCronTrigger
 * @since 2.0
 */
public class AnnotationMethodTriggerFactory implements TriggerFactory, BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationMethodTriggerFactory.class);

    /**
     * Map in which is saved bean name with methods which have {@link QuartzJob} annotation.
     */
    private final Map<String, List<Method>> beanNameTriggerMethods = new HashMap<>();

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public List<Trigger> createTriggers(JobExecuteTypeInCluster jobExecuteTypeInCluster) {
        Assert.notNull(jobExecuteTypeInCluster, "jobExecuteTypeInCluster must not be null");

        List<Trigger> result = new ArrayList<>();
        for (Map.Entry<String, List<Method>> triggerMethods : beanNameTriggerMethods.entrySet()) {
            for (Method triggerMethod : triggerMethods.getValue()) {
                result.addAll(createTriggersFromMethod(triggerMethods.getKey(), triggerMethod, jobExecuteTypeInCluster));
            }
        }

        LOG.debug("For execute type {} found {} triggers.", jobExecuteTypeInCluster, result.size());
        return result;
    }

    /**
     * Create {@link Trigger}s from one {@link Method} on bean.
     *
     * @param beanName                bean name
     * @param method                  method which has {@link QuartzJob} annotation
     * @param jobExecuteTypeInCluster execute type in cluster
     * @return new {@link Trigger}s
     */
    protected List<Trigger> createTriggersFromMethod(String beanName, Method method,
                                                     JobExecuteTypeInCluster jobExecuteTypeInCluster) {
        Assert.hasText(beanName, "beanName must not be empty");
        Assert.notNull(method, "method must not be null");
        Assert.notNull(jobExecuteTypeInCluster, "jobExecuteTypeInCluster must not be null");

        List<Trigger> result = new ArrayList<>();

        QuartzJob quartzJob = AnnotatedElementUtils.findMergedAnnotation(method, QuartzJob.class);
        if (quartzJob == null) {
            throw new IllegalStateException("Annotation '" + QuartzJob.class.getSimpleName()
                    + "' not found on method '" + method.getName() + "' in bean '" + beanName + "'.");
        }

        if (quartzJob.executeTypeInCluster().equals(jobExecuteTypeInCluster)) {
            //if on annotation is no trigger definition throw exception
            if (quartzJob.simpleTriggers().length == 0 && quartzJob.cronTriggers().length == 0) {
                throw new IllegalStateException("No trigger found on method '" + method.getName()
                        + "' in bean '" + beanName + "'.");
            }

            //create job detail
            JobDetail jobDetail = createJobDetail(beanName, method, quartzJob);

            int triggerOrder = 0;
            //create simple triggers
            for (QuartzSimpleTrigger simpleTrigger : quartzJob.simpleTriggers()) {
                Trigger trigger = createSimpleTrigger(simpleTrigger, jobDetail, ++triggerOrder);
                if (trigger != null) {
                    result.add(trigger);
                }
            }

            //create cron triggers
            for (QuartzCronTrigger cronTrigger : quartzJob.cronTriggers()) {
                Trigger trigger = createCronTrigger(cronTrigger, jobDetail, ++triggerOrder);
                if (trigger != null) {
                    result.add(trigger);
                }
            }
        }

        LOG.debug("For execute type {} and method {} on bean {} found {} triggers.", jobExecuteTypeInCluster,
                method.getName(), beanName, result.size());
        return result;
    }

    /**
     * Create {@link Trigger} from {@link QuartzSimpleTrigger} definition.
     *
     * @param simpleTrigger trigger definition
     * @param jobDetail     job
     * @param triggerOrder  order of trigger for the same job
     * @return new {@link Trigger}, {@code NULL} - trigger can not be create
     */
    @Nullable
    private Trigger createSimpleTrigger(QuartzSimpleTrigger simpleTrigger, JobDetail jobDetail, int triggerOrder) {
        Assert.notNull(simpleTrigger, "simpleTrigger must not be null");
        Assert.notNull(jobDetail, "jobDetail must not be null");

        String triggerName = StringUtils.isBlank(simpleTrigger.name())
                ? QuartzUtils.createTriggerName(jobDetail, triggerOrder) : simpleTrigger.name();
        try {
            long repeatInterval = getRepeatInterval(simpleTrigger);
            if (repeatInterval > 0) {
                SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
                factoryBean.setName(triggerName);
                if (!StringUtils.isBlank(simpleTrigger.group())) {
                    factoryBean.setGroup(simpleTrigger.group());
                }
                factoryBean.setJobDetail(jobDetail);
                factoryBean.setStartDelay(0L);
                factoryBean.setRepeatInterval(repeatInterval);
                factoryBean.setRepeatCount(simpleTrigger.repeatCount());
                factoryBean.setMisfireInstruction(simpleTrigger.misfireInstruction().getMisfireInstruction());

                factoryBean.afterPropertiesSet();

                LOG.debug("Create simple trigger {} for job {} with repeat interval {} in millis.", triggerName,
                        jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName(), repeatInterval);
                return factoryBean.getObject();
            } else {
                LOG.warn("Simple trigger {} for job {} has no repeat interval.", triggerName,
                        jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName());
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error in creating trigger with name '" + triggerName
                    + "' for job '" + jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName()
                    + "'. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Create {@link Trigger} from {@link QuartzCronTrigger} definition.
     *
     * @param cronTrigger  trigger definition
     * @param jobDetail    job
     * @param triggerOrder order of trigger for the same job
     * @return new {@link Trigger}, {@code NULL} - trigger can not be create
     */
    @Nullable
    private Trigger createCronTrigger(QuartzCronTrigger cronTrigger, JobDetail jobDetail, int triggerOrder) {
        Assert.notNull(cronTrigger, "cronTrigger must not be null");
        Assert.notNull(jobDetail, "jobDetail must not be null");

        String triggerName = StringUtils.isBlank(cronTrigger.name())
                ? QuartzUtils.createTriggerName(jobDetail, triggerOrder) : cronTrigger.name();
        try {
            String cronExpression = getCronExpression(cronTrigger);

            CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
            factoryBean.setName(triggerName);
            if (!StringUtils.isBlank(cronTrigger.group())) {
                factoryBean.setGroup(cronTrigger.group());
            }
            factoryBean.setJobDetail(jobDetail);
            factoryBean.setCronExpression(cronExpression);
            factoryBean.setMisfireInstruction(cronTrigger.misfireInstruction().getMisfireInstruction());

            factoryBean.afterPropertiesSet();

            LOG.debug("Create cron trigger {} for job {} with expression {}.", triggerName,
                    jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName(), cronExpression);
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new IllegalStateException("Error in creating cron trigger with name '" + triggerName
                    + "' for job '" + jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName()
                    + "'. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Gets cron expression from {@link QuartzCronTrigger}.
     * Cron is defined direct on {@link QuartzCronTrigger#cronExpression()} or in property
     * {@link QuartzCronTrigger#cronExpressionProperty()}.
     *
     * @param cronTrigger trigger definition
     * @return cron expression
     */
    private String getCronExpression(QuartzCronTrigger cronTrigger) {
        Assert.notNull(cronTrigger, "cronTrigger must not be null");

        if (!StringUtils.isBlank(cronTrigger.cronExpression())
                && !StringUtils.isBlank(cronTrigger.cronExpressionProperty())) {
            throw new IllegalArgumentException("Value cronExpression and cronExpressionProperty is set in '"
                    + QuartzSimpleTrigger.class.getSimpleName() + "'. Only one attribute must be set.");
        }

        String result = null;
        if (!StringUtils.isBlank(cronTrigger.cronExpressionProperty())) {
            result = configurationService.getValue(String.class, cronTrigger.cronExpressionProperty());
            if (StringUtils.isBlank(result)) {
                throw new IllegalStateException("Value in property '" + cronTrigger.cronExpressionProperty()
                        + "' is empty.");
            }
        } else if (!StringUtils.isBlank(cronTrigger.cronExpression())) {
            result = cronTrigger.cronExpression();
        }

        if (!CronExpression.isValidExpression(result)) {
            throw new IllegalArgumentException("Cron expression '" + result + "' is not valid.");
        }
        return result;
    }

    /**
     * Gets repeat interval for trigger defind in {@link QuartzSimpleTrigger}.
     * Repeat interval is defined direct on {@link QuartzSimpleTrigger#repeatIntervalMillis()} or in property
     * {@link QuartzSimpleTrigger#repeatIntervalProperty()}.
     *
     * @param simpleTrigger trigger definition
     * @return repeat interval
     */
    private long getRepeatInterval(QuartzSimpleTrigger simpleTrigger) {
        Assert.notNull(simpleTrigger, "jobTrigger must not be null");

        if (simpleTrigger.repeatIntervalMillis() != QuartzSimpleTrigger.EMPTY_REPEAT_INTERVAL
                && !StringUtils.isBlank(simpleTrigger.repeatIntervalProperty())) {
            throw new IllegalArgumentException("Value repeatInterval and repeatIntervalProperty is set in '"
                    + QuartzSimpleTrigger.class.getSimpleName() + "'. Only one attribute must be set.");
        }

        Long result = null;
        if (!StringUtils.isBlank(simpleTrigger.repeatIntervalProperty())) {
            result = configurationService.getValue(Long.class, simpleTrigger.repeatIntervalProperty());
            if (result == null) {
                throw new IllegalStateException("Value in property '" + simpleTrigger.repeatIntervalProperty()
                        + "' is empty.");
            }
            //convert interval into millis
            result = simpleTrigger.intervalPropertyUnit().getIntervalInMillis(result);
        } else if (simpleTrigger.repeatIntervalMillis() != QuartzSimpleTrigger.EMPTY_REPEAT_INTERVAL) {
            result = simpleTrigger.repeatIntervalMillis();
        }

        if (result == null) {
            throw new IllegalArgumentException("No repeat interval found.");
        }
        return result;
    }

    /**
     * Create {@link JobDetail} from {@link QuartzJob}.
     *
     * @param beanName  bean name for job
     * @param method    method where is job defined base on {@link QuartzJob} annotation
     * @param quartzJob job definition
     * @return new job
     */
    private JobDetail createJobDetail(String beanName, Method method, QuartzJob quartzJob) {
        Assert.hasText(beanName, "beanName must not be empty");
        Assert.notNull(method, "method must not be null");
        Assert.notNull(quartzJob, "quartzJob must not be null");

        Class<?> jobClass = (quartzJob.stateful() ? MethodInvokingJobDetailFactoryBean.StatefulMethodInvokingJob.class
                : MethodInvokingJobDetailFactoryBean.MethodInvokingJob.class);

        JobDetailImpl result = new JobDetailImpl();
        result.setName(quartzJob.name());
        result.setGroup(quartzJob.group());
        result.setDescription(quartzJob.description());
        result.setJobClass((Class) jobClass);
        result.setDurability(true);
        result.getJobDataMap().put(QuartzUtils.JOB_BEAN_NAME_PARAMETER, beanName);
        result.getJobDataMap().put(QuartzUtils.JOB_BEAN_METHOD_NAME_PARAMETER, method.getName());

        LOG.debug("Create job detail for method {} on bean {}.", method.getName(), beanName);
        return result;
    }

    //--------------------------------------------- BeanPostProcessor --------------------------------------------------

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Assert.notNull(bean, "bean must not be null");
        Assert.hasText(beanName, "beanName must not be empty");

        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                List<Method> triggerMethods = beanNameTriggerMethods.get(beanName);
                if (triggerMethods == null) {
                    triggerMethods = new ArrayList<>();
                    beanNameTriggerMethods.put(beanName, triggerMethods);
                }
                triggerMethods.add(method);

                LOG.debug("Found method {} on bean {} with quartz job definition.", method.getName(), beanName);
            }
        }, new AnnotationMethodFilter(QuartzJob.class));

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
