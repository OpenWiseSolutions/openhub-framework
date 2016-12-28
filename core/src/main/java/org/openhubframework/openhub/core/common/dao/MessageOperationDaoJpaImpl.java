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

package org.openhubframework.openhub.core.common.dao;

import java.util.Arrays;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;


/**
 * JPA implementation of {@link MessageOperationDao} interface.
 *
 * @author Petr Juza
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MessageOperationDaoJpaImpl implements MessageOperationDao {

    private static final Logger LOG = LoggerFactory.getLogger(MessageOperationDaoJpaImpl.class);

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public boolean setPartlyFailedState(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        // change state to PARTLY_FAILED
        String jSql = "UPDATE " + Message.class.getName()
                + " SET state = ?1, lastUpdateTimestamp = ?2"
                + " WHERE msgId = ?3 AND state IN (?4)";

        Query q = em.createQuery(jSql);
        q.setParameter(1, MsgStateEnum.PARTLY_FAILED);
        q.setParameter(2, new Date());
        q.setParameter(3, msg.getMsgId());
        q.setParameter(4, Arrays.asList(MsgStateEnum.CANCEL, MsgStateEnum.FAILED));

        return q.executeUpdate() > 0;
    }

    @Override
    public void removeExtCalls(Message msg, boolean totalRestart) {
        Assert.notNull(msg, "the msg must not be null");

        String jSql = "DELETE "
                + "FROM " + ExternalCall.class.getName() + " c "
                + "WHERE c.msgId = ?1 ";

        if (!totalRestart) {
            // delete only confirmations
            jSql += "AND c.operationName = '" + ExternalCall.CONFIRM_OPERATION + "'";
        }

        Query q = em.createQuery(jSql);
        q.setParameter (1, msg.getMsgId());
        int updatedCount = q.executeUpdate();

        LOG.debug(updatedCount + " external calls were deleted for message with msgID=" + msg.getMsgId());
    }

    @Override
    public boolean setCancelState(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        String jSql = "UPDATE " + Message.class.getName()
                + " SET state = ?1 "
                + " WHERE msgId = ?2 AND state IN (?3)";

        Query q = em.createQuery(jSql);
        q.setParameter(1, MsgStateEnum.CANCEL);
        q.setParameter(2, msg.getMsgId());
        q.setParameter(3, Arrays.asList(MsgStateEnum.NEW, MsgStateEnum.PARTLY_FAILED, MsgStateEnum.POSTPONED));

        return q.executeUpdate() > 0;
    }
}
