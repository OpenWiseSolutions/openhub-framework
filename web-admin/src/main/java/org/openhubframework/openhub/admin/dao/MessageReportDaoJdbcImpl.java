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

package org.openhubframework.openhub.admin.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import org.openhubframework.openhub.admin.dao.dto.MessageReportDto;


/**
 * JDBC implementation of {@link MessageReportDao}.
 *
 * @author Viliam Elischer
 */
@Repository
public class MessageReportDaoJdbcImpl implements MessageReportDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "dataSource")
    public void setDataSource(DataSource dataSource) {
        Assert.notNull(dataSource, "the dataSource must not be null");

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<MessageReportDto> getMessageStateSummary(Instant startDate, Instant endDate) {

        String sql = "SELECT service, operation_name, source_system, state, COUNT(*) as state_count " +
                "FROM message m " +
                "     WHERE receive_timestamp >= ? AND receive_timestamp <= ? " +
                "GROUP BY service, operation_name, source_system, state " +
                "ORDER BY service, operation_name, source_system, state;";

        List<MessageReportDto> raw = jdbcTemplate.query(sql, new RowMapper<MessageReportDto>() {

            public MessageReportDto mapRow(ResultSet rs, int i) throws SQLException {
                MessageReportDto mdto = new MessageReportDto();
                mdto.setServiceName(rs.getString("service"));
                mdto.setOperationName(rs.getString("operation_name"));
                mdto.setSourceSystem(rs.getString("source_system"));
                mdto.setState(rs.getString("state"));
                mdto.setStateCount(rs.getInt("state_count"));
                return mdto;
            }
        }, startDate, endDate);

        return raw;
    }
}
