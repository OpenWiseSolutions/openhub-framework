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

package org.openhubframework.openhub.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.core.common.asynch.notification.DummyEmailServiceImpl;


/**
 * Configuration for tests in core module.
 *
 * @author Petr Juza
 * @since 2.0
 */
@PropertySource(value = {"classpath:/config/application-test-default.properties"})
public class CoreTestConfig {

    @Bean
    @Primary
    public EmailService emailTestService() {
        return new DummyEmailServiceImpl();
    }
}
