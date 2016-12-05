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

package org.openhubframework.openhub.admin.web.config;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.LoggingErrorHandlerBuilder;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.camel.component.quartz2.QuartzComponent;
import org.apache.camel.component.seda.PriorityBlockingQueueFactory;
import org.apache.camel.processor.interceptor.DefaultTraceFormatter;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import org.openhubframework.openhub.core.common.asynch.confirm.DelegateConfirmationCallback;
import org.openhubframework.openhub.core.common.asynch.msg.MsgPriorityComparator;


/**
 * Camel configuration.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@Configuration
public class CamelConfig {

    @Bean
    public CamelContextConfiguration contextConfiguration() {
      return new CamelContextConfiguration() {

          @Override
          public void beforeApplicationStart(CamelContext camelContext) {
              // error handler
              LoggingErrorHandlerBuilder handlerBuilder = new LoggingErrorHandlerBuilder();
              handlerBuilder.logName("org.openhubframework.openhub.core");
              camelContext.setErrorHandlerBuilder(handlerBuilder);

              // default thread profile (see DefaultExecutorServiceManager for defaults)
              ThreadPoolProfile threadPoolProfile = camelContext.getExecutorServiceManager()
                      .getDefaultThreadPoolProfile();
              threadPoolProfile.setId("defaultThreadProfile");
              threadPoolProfile.setMaxPoolSize(30);
          }

          @Override
          public void afterApplicationStart(CamelContext camelContext) {
              // nothing to set
          }
      };
    }

    @Bean
    public JpaComponent jpaComponent(PlatformTransactionManager transactionManager, CamelContext camelContext,
            EntityManagerFactory entityManagerFactory) {
        JpaComponent jpaComponent = new JpaComponent();
        jpaComponent.setEntityManagerFactory(entityManagerFactory);
        jpaComponent.setTransactionManager(transactionManager);
        jpaComponent.setCamelContext(camelContext);

        return jpaComponent;
    }

    @Bean(name = "priorityQueueFactory")
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

    @Bean
    public Tracer camelTracer() {
        return new Tracer();
    }

    @Bean
    @Profile("prod")
    public DelegateConfirmationCallback confirmationCallback() {
        return new DelegateConfirmationCallback();
    }

    @Bean
    @Profile("prod")
    public QuartzComponent quartzComponent() {
        QuartzComponent quartz = new QuartzComponent();
        quartz.setStartDelayedSeconds(90);

        return quartz;
    }
}
