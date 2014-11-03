/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.core.alerts;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;


/**
 * Spring JDBC implementation of {@link AlertsDao}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Repository
public class AlertsJdbcDao implements AlertsDao {

    private JdbcTemplate template;

    @Autowired
    @Qualifier(value = "dataSource")
    public void setDataSource(DataSource dataSource) {
        Assert.notNull(dataSource, "dataSource must not be empty!");

        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public long runQuery(String sql) {
        Assert.hasText(sql, "the sql must not be empty");

        return template.queryForObject(sql, Long.class);
    }
}
