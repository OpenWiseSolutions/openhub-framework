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

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.NoDataFoundException;


/**
 * JPA implementation of {@link MessageDao} interface.
 *
 * @author Petr Juza
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
    public List<Message> findChildMessagesForParent(Message parentMessage) {
        Assert.notNull(parentMessage, "parentMessage must not be null");

        TypedQuery<Message> q = em.createQuery(
                "SELECT m FROM " + Message.class.getName() + " m WHERE m.parentMsgId = :parentMsgId", Message.class);
        q.setParameter("parentMsgId", parentMessage.getMsgId());

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
    public Message findPartlyFailedMessage(Duration interval) {
        // find message that was lastly processed before specified interval
        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + MsgStateEnum.PARTLY_FAILED + "'"
                + "     AND m.lastUpdateTimestamp < :lastTime"
                + " ORDER BY m.msgTimestamp";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("lastTime", Instant.now().minus(interval));
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
    public Message findPostponedMessage(Duration interval) {
        // find message that was lastly processed before specified interval
        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + MsgStateEnum.POSTPONED + "'"
                + "     AND m.lastUpdateTimestamp < :lastTime"
                + " ORDER BY m.msgTimestamp";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("lastTime", Instant.now().minus(interval));
        q.setMaxResults(1);
        List<Message> messages = q.getResultList();

        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Override
    public boolean updateMessageProcessingUnderLock(Message msg, Node processingNode) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.notNull(processingNode, "processingNode must not be null");

        Message dbMsg = findMessageWithStates(msg.getMsgId(), true, MsgStateEnum.IN_QUEUE);

        boolean result = false;
        if (dbMsg != null) {
            // change message's state to PROCESSING
            msg.setState(MsgStateEnum.PROCESSING);
            Instant currDate = Instant.now();
            msg.setStartProcessTimestamp(currDate);
            msg.setLastUpdateTimestamp(currDate);
            msg.setNodeId(processingNode.getNodeId());

            update(msg);
            result = true;
        }
        return result;
    }

    @Override
    public boolean updateMessageInQueueUnderLock(Message msg, Node processingNode) {
        Assert.notNull(msg, "msg must not be null");
        Assert.notNull(processingNode, "processingNode must not be null");

        Message dbMsg = findMessageWithStates(msg.getMsgId(), true, MsgStateEnum.NEW, MsgStateEnum.PARTLY_FAILED,
                MsgStateEnum.POSTPONED, MsgStateEnum.WAITING_FOR_RES);
        boolean result = false;
        if (dbMsg != null) {
            // change message's state to IN QUEUE
            msg.setState(MsgStateEnum.IN_QUEUE);
            Instant currDate = Instant.now();
            msg.setStartInQueueTimestamp(currDate);
            msg.setLastUpdateTimestamp(currDate);
            msg.setNodeId(processingNode.getNodeId());

            update(msg);
            result = true;
        }
        return result;
    }

    /**
     * Finds message by id and states under database lock.
     *
     * @param msgId  message id
     * @param lock   {@code true} - database lock, {@code false} - no lock for result
     * @param states states
     * @return found message, {@code NULL} - no message found
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private Message findMessageWithStates(Long msgId, boolean lock, @Nullable MsgStateEnum... states) {
        Assert.notNull(msgId, "msgId must not be null");

        List<MsgStateEnum> statesInList = states == null ? Collections.EMPTY_LIST : Arrays.asList(states);

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.msgId = :msgId";
        if (!CollectionUtils.isEmpty(statesInList)) {
            jSql += " AND m.state in (:states)";
        }

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("msgId", msgId);
        if (!CollectionUtils.isEmpty(statesInList)) {
            q.setParameter("states", statesInList);
        }
        if (lock) {
            // note: https://blogs.oracle.com/carolmcdonald/entry/jpa_2_0_concurrency_and
            q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }

        List<Message> result = q.getResultList();
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }

    @Override
    public List<Message> findProcessingMessages(Duration interval) {
        Assert.notNull(interval, "interval must not be null");

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING
                + "' OR m.state = '" + MsgStateEnum.NEW
                + "' OR m.state = '" + MsgStateEnum.IN_QUEUE + "')"
                + "     AND m.startProcessTimestamp < :startTime";

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("startTime", Instant.now().minus(interval));
        q.setMaxResults(MAX_MESSAGES_IN_ONE_QUERY);
        return q.getResultList();
    }

    @Override
    public int getCountMessages(MsgStateEnum state, @Nullable Duration interval) {
        Instant lastUpdateTime = null;

        String jSql = "SELECT COUNT(m) "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE m.state = '" + state.name() + "'";

        if (interval != null) {
            lastUpdateTime = Instant.now().minus(interval);
            jSql += " AND m.lastUpdateTimestamp >= :lastUpdateTime";
        }

        TypedQuery<Number> q = em.createQuery(jSql, Number.class);
        if (lastUpdateTime != null) {
            q.setParameter("lastUpdateTime", lastUpdateTime);
        }

        return q.getSingleResult().intValue();
    }

    @Override
    public int getCountProcessingMessagesForFunnel(String funnelValue, Duration idleInterval, String funnelCompId) {
        Assert.notNull(idleInterval, "idleInterval must not be null");

        String jSql = "SELECT COUNT(m) "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.IN_QUEUE + "'"
                + "         OR m.state = '" + MsgStateEnum.NEW + "'"
                + "         OR m.state = '" + MsgStateEnum.WAITING + "'"
                + "         OR m.state = '" + MsgStateEnum.WAITING_FOR_RES + "')"
                + "      AND m.funnelValue = '" + funnelValue + "'"
                + "      AND m.startProcessTimestamp >= :startTime";

        TypedQuery<Number> q = em.createQuery(jSql, Number.class);
        q.setParameter("startTime", Instant.now().minus(idleInterval));

        return q.getSingleResult().intValue();
    }

    @Override
    public List<Message> getMessagesForGuaranteedOrderForRoute(String funnelValue, boolean excludeFailedState) {
        //TODO (juza) limit select to specific number of items + add msgId DESC to sorting (parent vs. child)
        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.IN_QUEUE + "'"
                + "         OR m.state = '" + MsgStateEnum.NEW + "'"
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
    public List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, Duration idleInterval,
            boolean excludeFailedState, String funnelCompId) {
        Assert.notNull(idleInterval, "idleInterval must not be null");

        String jSql = "SELECT m "
                + "FROM " + Message.class.getName() + " m "
                + "WHERE (m.state = '" + MsgStateEnum.PROCESSING + "' "
                + "         OR m.state = '" + MsgStateEnum.IN_QUEUE + "'"
                + "         OR m.state = '" + MsgStateEnum.NEW + "'"
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

        //TODO (juza) limit select to specific number of items + add msgId DESC to sorting (parent vs. child)

        TypedQuery<Message> q = em.createQuery(jSql, Message.class);
        q.setParameter("startTime", Instant.now().minus(idleInterval));

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
