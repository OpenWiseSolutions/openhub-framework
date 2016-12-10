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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;


/**
 * OpenHub {@link javax.sql.DataSource} properties holder.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
public class OpenHubDataSourceProperties extends DataSourceProperties {

    /**
     * Name of URL property.
     */
    public static final String URL = "url";

    /**
     * Name of the jndiName property.
     */
    public static final String JNDI_NAME = "jndi-name";

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("url", getUrl())
            .append("driverClassName", getDriverClassName())
            .append("username", getUsername())
            .append("password", "*****")
            .append("jndiName", getJndiName())
            .toString();
    }
}
