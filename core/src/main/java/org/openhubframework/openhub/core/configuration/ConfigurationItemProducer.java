/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.configuration;

import static org.springframework.util.StringUtils.hasText;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;


/**
 * Factory hook that allows to inject {@link ConfigurationItem} marked by {@link ConfigurableValue}
 * into bean instances, e.g. checking for marker interfaces or wrapping them with proxies.
 * By default {@link ConfigurationItemImpl} is provided as configuration item.
 *
 * @author Tomas Hanus
 * @see #resolveKey(Field)
 * @see #createConfigurationItem(Class, String, ConfigurationService)
 * @since 2.0
 */
public class ConfigurationItemProducer implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationItemProducer.class);

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Produces configuration item as {@link ConfigurationItemImpl} based upon field.
     *
     * @param beanName the name of bean that contains {@link ConfigurationItemImpl}
     * @param field    that holds {@link ConfigurationItem}
     * @return configuration item as {@link ConfigurationItemImpl}
     */
    private <T extends Serializable> ConfigurationItem<T> produceConfigurationItem(String beanName, Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();

        Type[] typeArgs = type.getActualTypeArguments();
        Class<T> configItemTypeClass = (Class<T>) typeArgs[0];

        final String effectiveKey = resolveKey(field);

        logger.debug("Configuration item with key '{}' registered for bean '{}' ", effectiveKey, beanName);

        return createConfigurationItem(configItemTypeClass, effectiveKey, configurationService);
    }

    /**
     * Creates {@link ConfigurationItem} based upon provided parameters.
     *
     * @param clazz as type of parameter
     * @param effectiveKey as item ID
     * @param service as contract to process items
     * @return the {@link ConfigurationItem} implementation
     */
    protected <T extends Serializable> ConfigurationItem<T> createConfigurationItem(
            final Class<T> clazz,
            final String effectiveKey,
            final ConfigurationService service) {

        return new ConfigurationItemImpl<>(clazz, effectiveKey, service);
    }

    /**
     * Resolves effective key of configuration item from {@link ConfigurableValue} annotation.
     * It {@link ConfigurableValue#key()} is not provided, effective parameter name is build
     * from the name of class of field and field name.
     *
     * @param field that is marked with {@link ConfigurableValue}
     * @return effective key name to lookup
     */
    protected String resolveKey(Field field) {
        final ConfigurableValue annotation = AnnotationUtils.findAnnotation(field, ConfigurableValue.class);
        //TODO (thanus, 18/12/2016, TASK: OHFJIRA-9) clever implementation how key of configuration item will be generated
        String effectiveKey = annotation.key();
        if (!hasText(effectiveKey)) {
            effectiveKey = field.getDeclaringClass().getCanonicalName() + "." + field.getName();
            logger.warn("Effective key name is not recognized, generated name will be used: {}", effectiveKey);
        }

        return effectiveKey;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(),
                new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        // field must be accessible
                        field.setAccessible(true);
                        field.set(bean, produceConfigurationItem(beanName, field));
                        logger.debug("Bean '{}' holds configuration item for key '{}' which is pre-configured",
                                beanName, field.getName());
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(final Field field) {
                        return AnnotationUtils.findAnnotation(field, ConfigurableValue.class) != null;
                    }
                });

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
}
