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

package org.openhubframework.openhub.core.common.asynch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.asynch.repair.RepairMessageService;
import org.openhubframework.openhub.core.common.asynch.repair.RepairMessageServiceDbImpl;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;


/**
 * Test suite for {@link RepairMessageServiceDbImpl}.
 *
 * @author Petr Juza
 */
@Transactional
public class RepairProcessingMsgServiceDbTest extends AbstractCoreDbTest {

    @Autowired
    private RepairMessageService repairMsgService;

    @Autowired
    private MessageDao messageDao;

    private Message msg;

    @Before
    public void prepareData() {
        Instant currDate = Instant.now().minus(1, ChronoUnit.DAYS);

        msg = new Message();
        msg.setState(MsgStateEnum.NEW);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("xml");
        msg.setLastUpdateTimestamp(currDate);
    }

    @Test
    public void testRepairProcessingMessages() {
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setStartProcessTimestamp(msg.getMsgTimestamp());
        messageDao.insert(msg);

        em.flush();

        int msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        // call repairing
        repairMsgService.repairProcessingMessages();

        em.flush();

        // verify results
        msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        getJdbcTemplate().query("select * from message", new RowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
                // verify row values
                assertThat(rs.getLong("msg_id"), is(1L));
                assertThat((int)rs.getShort("failed_count"), is(1));
                assertThat(rs.getTimestamp("last_update_timestamp"), notNullValue());
                assertThat(MsgStateEnum.valueOf(rs.getString("state")), is(MsgStateEnum.PARTLY_FAILED));

                return new Message();
            }
        });
    }
}
