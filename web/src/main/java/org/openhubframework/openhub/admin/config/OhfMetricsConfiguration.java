/*
 * Copyright 2002-2021 the original author or authors.
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

package org.openhubframework.openhub.admin.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.admin.web.metrics.OhfMetricsEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto configuration for OpenHub custom endpoints.
 *
 * @author Jiri Hankovec
 * @since 2.3
 */
@AutoConfiguration
@AutoConfigureAfter(MetricsEndpointAutoConfiguration.class)
public class OhfMetricsConfiguration {

    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public OhfMetricsEndpoint ohfMetricsEndpoint(MeterRegistry meterRegistry) {
        return new OhfMetricsEndpoint(meterRegistry);
    }
}
