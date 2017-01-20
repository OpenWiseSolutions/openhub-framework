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

import javax.sql.DataSource;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.core.common.dao.DbConst;


/**
 * Custom JPA configuration that sets persistence unit for OpenHub.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
public class JpaConfig {

    /**
     * Configures JPA entity manager.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
            DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Message.class, DbConfigurationParam.class)
                .persistenceUnit(DbConst.UNIT_NAME)
                .build();
    }
}
