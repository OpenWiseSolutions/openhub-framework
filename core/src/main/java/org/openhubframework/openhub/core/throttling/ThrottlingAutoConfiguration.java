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

package org.openhubframework.openhub.core.throttling;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.spi.throttling.ThrottleCounter;


/**
 * Configures throttling counter implementations.
 * There is property '{@value #COUNTER_IMPL_PROPERTY}' that defines which built-in implementation will be used.
 * If not defined then default {@link ThrottleCounterMemoryImpl} implementation is used.
 *
 * @author Petr Juza
 * @since 2.0
 */
@AutoConfiguration
@ConditionalOnMissingBean(ThrottleCounter.class)
@EnableConfigurationProperties(ThrottlingProperties.class)
public class ThrottlingAutoConfiguration {

    private static final String COUNTER_IMPL_PROPERTY = "ohf.throttling.counter.impl";

    private static final String IN_MEMORY_CLASS_NAME
            = "org.openhubframework.openhub.core.throttling.ThrottleCounterMemoryImpl";

    private static final String HAZELCAST_CLASS_NAME
            = "org.openhubframework.openhub.core.throttling.ThrottleCounterHazelcastImpl";

    @AutoConfiguration
    @ConditionalOnProperty(name = COUNTER_IMPL_PROPERTY, matchIfMissing = true, havingValue = IN_MEMORY_CLASS_NAME)
    public static class InMemoryConfiguration {

   		@Bean
   		public ThrottleCounterMemoryImpl inMemoryThrottlingCounter()  {
   		    return new ThrottleCounterMemoryImpl();
   		}
   	}

    @AutoConfiguration
    @ConditionalOnClass(HazelcastInstance.class)
    @AutoConfigureAfter({HazelcastAutoConfiguration.class, CacheAutoConfiguration.class})
    @ConditionalOnProperty(name = COUNTER_IMPL_PROPERTY, havingValue = HAZELCAST_CLASS_NAME)
   	public static class HazelcastConfiguration {

        @Bean
   		public ThrottleCounterHazelcastImpl hazelcastThrottlingCounter(HazelcastInstance hazelcast)  {
   		    return new ThrottleCounterHazelcastImpl(hazelcast);
   		}
   	}
}
