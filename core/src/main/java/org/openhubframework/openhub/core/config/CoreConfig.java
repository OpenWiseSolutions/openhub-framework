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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.openhubframework.openhub.api.entity.ErrorsCatalog;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.confcheck.ConfigurationChecker;


/**
 * OpenHub core configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
public class CoreConfig {

    /**
     * Configures {@link ConfigurationChecker}.
     */
    @Bean
    @Profile("!" + Profiles.TEST) // not init for tests
    @ConditionalOnMissingBean
    public ConfigurationChecker configurationChecker() {
        return new ConfigurationChecker();
    }

    /**
     * Defines OHF error codes catalogue.
     */
    @Bean
    public ErrorsCatalog coreErrorCatalog() {
        return new ErrorsCatalog(InternalErrorEnum.values());
    }
}
