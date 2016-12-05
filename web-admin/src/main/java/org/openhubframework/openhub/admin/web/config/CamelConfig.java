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

import org.apache.camel.component.quartz2.QuartzComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openhubframework.openhub.core.common.asynch.confirm.DelegateConfirmationCallback;


/**
 * Camel configuration.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@Configuration
public class CamelConfig {

    @Bean
    public DelegateConfirmationCallback confirmationCallback() {
        return new DelegateConfirmationCallback();
    }

    @Bean
    public QuartzComponent quartzComponent() {
        QuartzComponent quartz = new QuartzComponent();
        quartz.setStartDelayedSeconds(90);

        return quartz;
    }
}
