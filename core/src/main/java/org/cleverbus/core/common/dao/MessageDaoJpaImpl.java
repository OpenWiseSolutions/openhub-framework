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

package org.cleverbus.core.common.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.exception.NoDataFoundException;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * JPA implementation of {@link MessageDao} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Repository
public class MessageDaoJpaImpl implements MessageDao {

    public static final int MAX_MESSAGES_IN_ONE_QUERY = 50;

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void insert(Message msg) {
        em.persist(msg);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Message msg) {
        em.merge(msg);
    }

    @Override
    @Nullable
    public Message findMessage(Long msgId) {
        Assert.notNull(msgId, "the msgId must not be null");

        return em.find(Message.class, msgId);
    }

    @Override
    @Nullable
    public Message findEagerMessage(Long msgId) {
        Assert.notNull(msgId, "the msgId must not be null");

        TypedQuery<Message> q = em.createQuery(
                "SELECT m FROM " + Message.class.getName() + " m "
                        + " left join fetch m.externalCalls  "
                        + " left join fetch m.requests  "
                + "WHERE m.msgId = :msgId", Message.class);
        q.setParameter("msgId", msgId);

        return q.getSingleResult();
    }

    @Override
    public Message getMessage(Long msgId) {
        Assert.notNull(msgId, "the msgId must not be null");

        Message msg = em.find(Message.class, msgId);
        if (msg == null) {
            throw new NoDataFoundException("no message with id: " + msgId);
        }

        return msg;
    }

    @Override
    public List<Message> findChildMessages(Message msg) {
        TypedQuery<Message> q = em.createQuery(
                "SELECT m FROM " + Message.class.getName() + " m WHERE m.parentMsgId = :parentMsgId", Message.class);
        q.setParameter("parentMsgId", msg.getParentMsgId());

        return q.getResultList();
    }

    @Override
    @Nullable
    public Message findByCorrelationId(String correlationId, @Nullable ExternalSystemExtEnum sourceSystem) {
        Assert.notNull(correlationId, "the correlationId must not be null");

        String jSql = "SELECT m " +
                "FROM " + Message.class.getName() + " m " +
                "WHERE m.correlationId = :correlationId";

        if (sourceSystem != null) {
            jSql += " AND m.sourceSystemInternal = :sourceSystem";
        }

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("correlationId", correlationId);

        if (sourceSystem != null) {
            q.setParameter("sourceSystem", sourceSystem.getSystemName());
        }

        // we search by unique key - it's not possible to have more records
        List<Message> messages = q.getResultList();
        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0); // if find more items then return first one only
        }
    }

    @Override
    @Nullable
    public Message findPartlyFailedMessage(int interval) {
        // find message that was lastly processed before specified interval
        Date lastUpdateLimit = DateUtils.addSeconds(new Date(), -interval);

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + MsgStateEnum.PARTLY_FAILED + "'"
                + "     AND m.lastUpdateTimestamp < :lastTime"
                + " ORDER BY m.msgTimestamp";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("lastTime", new Timestamp(lastUpdateLimit.getTime()));
        q.setMaxResults(1);
        List<Message> messages = q.getResultList();

        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Override
    @Nullable
    public Message findPostponedMessage(int interval) {
        // find message that was lastly processed before specified interval
        Date lastUpdateLimit = DateUtils.addSeconds(new Date(), -interval);

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + MsgStateEnum.POSTPONED + "'"
                + "     AND m.lastUpdateTimestamp < :lastTime"
                + " ORDER BY m.msgTimestamp";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("lastTime", new Timestamp(lastUpdateLimit.getTime()));
        q.setMaxResults(1);
        List<Message> messages = q.getResultList();

        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Override
    public Boolean updateMessageForLock(final Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        // acquire pessimistic lock firstly
        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.msgId = :msgId"
                + "     AND (m.state = '" + MsgStateEnum.PARTLY_FAILED + "' "
                + "     OR m.state = '" + MsgStateEnum.POSTPONED + "')";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("msgId", msg.getMsgId());
        // note: https://blogs.oracle.com/carolmcdonald/entry/jpa_2_0_concurrency_and
        q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        Message dbMsg = q.getSingleResult();

        if (dbMsg != null) {
            // change message's state to PROCESSING
            msg.setState(MsgStateEnum.PROCESSING);
            Date currDate = new Date();
            msg.setStartProcessTimestamp(currDate);
            msg.setLastUpdateTimestamp(currDate);

            update(msg);
        }

        return true;
    }

    @Override
    public List<Message> findProcessingMessages(int interval) {
        final Date startProcessLimit = DateUtils.addSeconds(new Date(), -interval);

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + MsgStateEnum.PROCESSING + "'"
                + "     AND m.startProcessTimestamp < :startTime";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("startTime", new Timestamp(startProcessLimit.getTime()));
        q.setMaxResults(MAX_MESSAGES_IN_ONE_QUERY);
        return q.getResultList();
    }

    @Override
    public int getCountMessages(MsgStateEnum state, Integer interval) {
        Date lastUpdateTime = null;

        String jSql = "SELECT COUNT(m) "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + state.name() + "'";

        if (interval != null) {
            lastUpdateTime = DateUtils.addSeconds(new Date(), -interval);
            jSql += " AND m.lastUpdateTimestamp >= :lastUpdateTime";
        }

        TypedQuery<Number> q = em.createQuery(jSql, Number.class);
        if (lastUpdateTime != null) {
            q.setParameter("lastUpdateTime", new Timestamp(lastUpdateTime.getTime()));
        }

        return q.getSingleResult().intValue();
    }

    @Override
    public int getCountProcessingMessagesForFunnel(String funnelValue, int idleInterval, String funnelCompId) {
        String jSql = "SELECT COUNT(m) "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.WAITING + "'"
                + "         OR m.state = '" + MsgStateEnum.WAITING_FOR_RES + "')"
                + "      AND m.funnelValue = '" + funnelValue + "'"
                + "      AND m.startProcessTimestamp >= :startTime";

        TypedQuery<Number> q = em.createQuery(jSql, Number.class);
        q.setParameter("startTime", new Timestamp(DateUtils.addSeconds(new Date(), -idleInterval).getTime()));

        return q.getSingleResult().intValue();
    }

    @Override
    public List<Message> getMessagesForGuaranteedOrderForRoute(String funnelValue, boolean excludeFailedState) {
        //TODO (juza) omezit select na nejaky pocet + tridit jeste pres msgId DESC (parent vs. child)
        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.WAITING + "'"
                + "         OR m.state = '" + MsgStateEnum.PARTLY_FAILED + "'"
                + "         OR m.state = '" + MsgStateEnum.POSTPONED + "'";

        if (!excludeFailedState) {
            jSql += "         OR m.state = '" + MsgStateEnum.FAILED + "'";
        }

        jSql += "         OR m.state = '" + MsgStateEnum.WAITING_FOR_RES + "')"
                + "      AND m.funnelValue = '" + funnelValue + "'"
                + "      AND m.guaranteedOrder is true"
                + " ORDER BY m.msgTimestamp";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);

        return q.getResultList();
    }

    @Override
    public List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, int idleInterval,
            boolean excludeFailedState, String funnelCompId) {

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.WAITING + "'"
                + "         OR m.state = '" + MsgStateEnum.PARTLY_FAILED + "'"
                + "         OR m.state = '" + MsgStateEnum.POSTPONED + "'";

        if (!excludeFailedState) {
            jSql += "         OR m.state = '" + MsgStateEnum.FAILED + "'";
        }

        jSql += "         OR m.state = '" + MsgStateEnum.WAITING_FOR_RES + "')"
                + "      AND m.funnelValue = '" + funnelValue + "'"
                + "      AND m.funnelComponentId = '" + funnelCompId + "'"
                + "      AND m.startProcessTimestamp >= :startTime"
                + " ORDER BY m.msgTimestamp";

        //TODO (juza) omezit select na nejaky pocet + tridit jeste pres msgId (parent vs. child)

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("startTime", new Timestamp(DateUtils.addSeconds(new Date(), -idleInterval).getTime()));

        return q.getResultList();
    }

    @Override
    public List<Message> findMessagesByContent(String substring) {
        Assert.hasText("the substring must not be empty", substring);

        String jSql = "SELECT m "
                + "     FROM " + Message.class.getName() + " m "
                + " WHERE (m.payload like :substring) ORDER BY m.msgId DESC";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("substring", "%" + substring + "%");
        q.setMaxResults(MAX_MESSAGES_IN_ONE_QUERY);

        return q.getResultList();
    }
}
