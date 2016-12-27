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

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * Properties for OpenHub {@link DataSource}s (transactional and non-transactional).
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
@Component
@ConfigurationProperties(prefix = OpenHubPropertyConstants.SPRING_PREFIX)
public class OpenHubDatabaseProperties {

    /**
     * Full prefix for transaction based datasource properties.
     */
    // actually we use spring datasource configuration
    public static final String DATASOURCE_PREFIX = OpenHubPropertyConstants.SPRING_PREFIX + ".datasource";

    /**
     * Properties for standard transactional {@link DataSource}.
     */
    private OpenHubDataSourceProperties datasource;

    /**
     * Return properties for standard {@link DataSource}.
     *
     * @return datasource properties.
     */
    public OpenHubDataSourceProperties getDatasource() {
        return datasource;
    }

    /**
     * Set Properties for standard transactional {@link DataSource}.
     *
     * @param datasource properties to set.
     */
    public void setDatasource(OpenHubDataSourceProperties datasource) {
        this.datasource = datasource;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("datasource", datasource)
                .toString();
    }
}
