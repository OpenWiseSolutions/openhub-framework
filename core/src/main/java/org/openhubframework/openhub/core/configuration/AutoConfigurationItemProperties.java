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

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.core.config.CacheNames;


/**
 * Auto-configuration of configuration item properties via parameters. This configuration registers 
 * {@link ConfigurationItemProducer} as producer for {@link ConfigurationItem} wrappers which holds service
 * to manage items on-the-fly.
 * <p>
 * Configuration exposes default implementation of contract service {@link ConfigurationService} to operate
 * with {@link ConfigurationItem}. It uses {@link Environment} as system parameter repository, but it is possible 
 * to override with custom implementation.
 *
 * @author Tomas Hanus
 * @since 2.0
 * @see ConversionService
 */
@AutoConfiguration
public class AutoConfigurationItemProperties {

    /**
     * Create {@link BeanPostProcessor} that catch fields with {@link ConfigurableValue} and process them with value.
     *
     * @return the processor.
     */
    @Bean
    public BeanPostProcessor configurationItemProducer() {
        return new ConfigurationItemProducer();
    }

    /**
     * Creates default {@link ConfigurationService} implementation that use {@link Environment} to operate
     * with configuration parameters.
     *
     * @param environment that represents
     * @return default {@link ConfigurationService} implementation as proxy of {@link Environment}
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationService configurationService(final Environment environment) {
        return new ConfigurationService() {

            @Override
            @Cacheable(CacheNames.CONFIG_PARAMS)
            public <T> T getValue(Class<T> clazz, String key) {
                return environment.getProperty(key, clazz);
            }

            @Override
            public <T> void setValue(Class<T> clazz, String key, Object newValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
