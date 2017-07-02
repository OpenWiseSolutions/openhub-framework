/*
 * Copyright 20016-2017 the original author or authors.
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

import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.LoggingErrorHandlerBuilder;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.camel.component.seda.PriorityBlockingQueueFactory;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.interceptor.DefaultTraceFormatter;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.camel.spi.Registry;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.core.common.asynch.confirm.DelegateConfirmationCallback;
import org.openhubframework.openhub.core.common.asynch.msg.MsgPriorityComparator;
import org.openhubframework.openhub.core.common.camel.ApplicationContextsRegistry;


/**
 * Camel configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
public class CamelConfig {

    private static final String DEFAULT_THREAD_PROFILE = "defaultThreadProfile";

    private static final int MAX_THREAD_POOL_SIZE = 30;

    @Bean
    public CamelContextConfiguration contextConfiguration(Registry registry) {
      return new CamelContextConfiguration() {

          @Override
          public void beforeApplicationStart(CamelContext camelContext) {
              // error handler
              LoggingErrorHandlerBuilder handlerBuilder = new LoggingErrorHandlerBuilder();
              handlerBuilder.logName("org.openhubframework.openhub.core");
              camelContext.setErrorHandlerBuilder(handlerBuilder);
              camelContext.setHandleFault(true);
              if (camelContext instanceof DefaultCamelContext){
                  ((SpringCamelContext) camelContext).setRegistry(registry);
              }

              // default thread profile (see DefaultExecutorServiceManager for defaults)
              ThreadPoolProfile threadPoolProfile = camelContext.getExecutorServiceManager()
                      .getDefaultThreadPoolProfile();
              threadPoolProfile.setId(DEFAULT_THREAD_PROFILE);
              threadPoolProfile.setMaxPoolSize(MAX_THREAD_POOL_SIZE);
          }

          @Override
          public void afterApplicationStart(CamelContext camelContext) {
              // nothing to set
          }
      };
    }

    /**
     * Configures {@link ApplicationContextsRegistry}.
     */
    @Bean
    public ApplicationContextsRegistry applicationContextsRegistry() {
        return new ApplicationContextsRegistry();
    }

    /**
     * Configures JPA component.
     */
    @Bean
    public JpaComponent jpaComponent(PlatformTransactionManager transactionManager, CamelContext camelContext,
            EntityManagerFactory entityManagerFactory) {
        JpaComponent jpaComponent = new JpaComponent();
        jpaComponent.setEntityManagerFactory(entityManagerFactory);
        jpaComponent.setTransactionManager(transactionManager);
        jpaComponent.setCamelContext(camelContext);
        return jpaComponent;
    }

    @Bean(name = AsynchConstants.PRIORITY_QUEUE_FACTORY)
    public PriorityBlockingQueueFactory priorityQueueFactory() {
        PriorityBlockingQueueFactory<Exchange> queueFactory = new PriorityBlockingQueueFactory<>();
        queueFactory.setComparator(new MsgPriorityComparator());
        return queueFactory;
    }

    /**
     * Configures Camel trace log.
     */
    @Bean
    public DefaultTraceFormatter traceFormatter() {
        DefaultTraceFormatter formatter = new DefaultTraceFormatter();
        formatter.setShowHeaders(false);
        return formatter;
    }

    /**
     * Configures Tracer for tracing routes.
     */
    @Bean
    public Tracer camelTracer() {
        return new Tracer();
    }


    // =================== production ================

    @Bean
    @Profile(Profiles.PROD)
    public DelegateConfirmationCallback confirmationCallback() {
        return new DelegateConfirmationCallback();
    }
}
