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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.core.config.datasource.OpenHubDatabaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.core.common.dao.DbConst;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;


/**
 * Custom JPA configuration that sets persistence unit for OpenHub.
 *
 * @author Petr Juza
 * @since 2.0
 */
@AutoConfiguration
@AutoConfigureAfter(OpenHubDatabaseConfiguration.class)
public class JpaConfig {

    /**
     * Configuration properties instance.
     */
    @Autowired
    private JpaConfigurationProperties jpaConfigurationProperties;

    /**
     * Configures JPA entity manager.
     */
    @Bean
    @OpenHub
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
               @OpenHub DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(mergePackages(
                        jpaConfigurationProperties.getAdditionalPackages(),
                        Message.class,
                        DbConfigurationParam.class)
                )
                .persistenceUnit(DbConst.UNIT_NAME)
                .build();
    }

    /**
     * Primary transactionManager implementation, instance of JpaTransactionManager.
     * Can be enabled or disabled by property. By default, it is disabled.
     *
     * Should be enabled only if multiple datasources are used, and each of them should
     * have isolated transaction management. Otherwise it should be disabled, to leverage
     * spring boot autoconfiguration.
     *
     * @param builder the EntityManagerFactoryBuilder.
     * @param dataSource the OpenHub dataSource.
     * @return instance of transactionManager.
     * @since 2.1
     * @see JpaTransactionManager
     */
    @ConditionalOnProperty(name = JpaConfigurationProperties.TRANSACTION_MANAGER_ENABLED)
    @Primary
    @OpenHub
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder,
               @OpenHub DataSource dataSource) {
        return new JpaTransactionManager(entityManagerFactory(builder, dataSource).getObject());
    }

    /**
     * Util method to merge packages from list of packages & some provided classes.
     * Should remove all duplicites along the way.
     *
     * @param packageList list of packages.
     * @param basePackageClasses the classes, whose package should be included as wel..
     * @return array with string representation of packages.
     */
    private static String[] mergePackages(List<String> packageList, Class... basePackageClasses) {
        final Set<String> baseClassesList = new HashSet<>();
        // add all additional packages from list
        baseClassesList.addAll(packageList);
        // add all packages from classes
        baseClassesList.addAll(Arrays.stream(basePackageClasses)
                                     .map(ClassUtils::getPackageName)
                                     .collect(Collectors.toSet()));
        return baseClassesList.toArray(new String[0]);
    }

}
