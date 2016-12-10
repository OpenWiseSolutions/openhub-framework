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

package org.openhubframework.openhub.common.datasource;

import static org.openhubframework.openhub.common.datasource.OpenHubDataSourceProperties.JNDI_NAME;
import static org.openhubframework.openhub.common.datasource.OpenHubDataSourceProperties.URL;
import static org.openhubframework.openhub.common.datasource.OpenHubDatabaseProperties.DATASOURCE_PREFIX;
import static org.springframework.util.StringUtils.hasText;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.Assert;

import org.openhubframework.openhub.common.AutoConfiguration;


/**
 * Default {@link DataSource} OpenHub provider for domain database.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@AutoConfiguration
public class OpenHubDatabaseConfiguration extends AbstractDatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenHubDatabaseConfiguration.class);

    @Autowired
    private OpenHubDatabaseProperties databaseProperties;

    @Autowired
    private HikariConfig hikariConfig;

    /**
     * Creates {@link JdbcOperations} with OpenHub {@link DataSource} defined by {@link OpenHubDataSource} qualifier.
     *
     * @param dataSource as {@link OpenHubDataSource} type
     * @return OpenHub {@link DataSource datasource}
     */
    @Bean
    public JdbcOperations JdbcOperations(@OpenHubDataSource DataSource dataSource) {
        Assert.notNull(dataSource, "OpenHub Datasource must not be null");

        return new JdbcTemplate(dataSource);
    }

    /**
     * Creates {@link DataSource} that represents source to OpenHub database. This bean is created only when the specified bean classes
     * and/or names are not already contained. {@link DataSource} is fetched based upon {@literal JNDI} name if provided, otherwise
     * based upon URL connection string.
     *
     * @return {@link DataSource} as general OpenHub datasource
     * @see OpenHubDataSourceProperties
     */
    @ConditionalOnMissingBean(name = OpenHubDataSource.BEAN_NAME)
    // do not create if not specified in configuration (must use SPEL expression as ConditionalOnProperty has AND semantic)
    // '${openhub.datasource.jndi-name}' != '' or '${openhub.datasource.url}' != ''
    @ConditionalOnExpression("'${" + DATASOURCE_PREFIX + "." + JNDI_NAME + ":}' != '' or '${" + DATASOURCE_PREFIX + "." + URL + ":}' != ''")
    @Bean(destroyMethod = "", name = OpenHubDataSource.BEAN_NAME)
    @Primary // transactional datasource must be primary to be taken by spring boot always
    @Override
    public DataSource dataSource() {
        return createDataSource(databaseProperties.getDatasource(), OpenHubDataSource.BEAN_NAME);
    }

    private DataSource createDataSource(OpenHubDataSourceProperties properties, String beanName) {
        if (hasText(properties.getJndiName())) {
            logger.info("JNDI '{}' will be used to configure datasource", properties.getJndiName());

            DataSource dataSource = null;
            JndiTemplate jndi = new JndiTemplate();
            try {
                dataSource = (DataSource) jndi.lookup(properties.getJndiName());
                if (dataSource != null) {
                    excludeMBeanIfNecessary(dataSource, beanName);
                }
            } catch (NamingException e) {
                logger.error("NamingException for " + properties.getJndiName(), e);
            }

            return dataSource;

        } else {
            logger.info("JDBC URL '{}' will be used to configure datasource", properties.getUrl());

            hikariConfig.setDriverClassName(properties.getDriverClassName());
            hikariConfig.setJdbcUrl(properties.getUrl());
            hikariConfig.setUsername(properties.getUsername());
            hikariConfig.setPassword(properties.getPassword());

            final DataSource dataSource = new HikariDataSource(hikariConfig);

            excludeMBeanIfNecessary(dataSource, "dataSource");
            return dataSource;
        }
    }

    @Bean
    @ConfigurationProperties(OpenHubDatabaseProperties.DATASOURCE_PREFIX + ".hikari")
    // creates HikariCP config based upon properties
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }
}

