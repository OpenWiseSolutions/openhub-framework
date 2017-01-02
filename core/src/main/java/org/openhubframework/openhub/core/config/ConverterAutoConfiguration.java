/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.ConfigurableEnvironment;

import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.common.converter.Converters;


/**
 * Auto register all custom {@link Converter} defined in this library.
 *
 * @author Petr Juza
 * @since 2.0
 */
@AutoConfiguration
public class ConverterAutoConfiguration implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(ConverterAutoConfiguration.class);

    @Autowired
    private ConfigurableEnvironment env;

    @Override
    public void afterPropertiesSet() {
        LOG.debug("Registering OpenHub converters.");
        Converters.registerConverters(env.getConversionService());
    }
}