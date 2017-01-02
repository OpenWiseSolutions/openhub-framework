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

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.*;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.exception.MultipleDataFoundException;


/**
 * DAO for {@link ExternalCall} entity.
 *
 * @author Petr Juza
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ExternalCallDaoJpaImpl implements ExternalCallDao {

    private static final int MAX_MESSAGES_IN_ONE_QUERY = 50;

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public void insert(ExternalCall externalCall) {
        em.persist(externalCall);
    }

    @Override
    public void update(ExternalCall externalCall) {
        em.merge(externalCall);
    }

    @Override
    @Nullable
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public ExternalCall getExternalCall(String operationName, String entityId) {
        Assert.notNull(operationName, "operationName (uri) must not be null");
        Assert.notNull(entityId, "entityId (operation key) must not be null");

        String jSql = "SELECT c FROM " + ExternalCall.class.getName() + " c " +
                    "WHERE c.operationName = ?1 AND c.entityId = ?2";
        TypedQuery<ExternalCall> q = em.createQuery(jSql, ExternalCall.class);
        q.setParameter(1, operationName);
        q.setParameter(2, entityId);

        List<ExternalCall> results = q.getResultList();
        if (results.isEmpty()) {
            return null;
        }

        if (results.size() > 1) {
            throw new MultipleDataFoundException(String.format(
                    "Multiple ExternalCall instances found for operationName/entityId: %s/%s",
                    operationName, entityId));
        }

        return results.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockExternalCall(ExternalCall extCall) throws PersistenceException {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() != ExternalCallStateEnum.PROCESSING,
                "the extCall must not be locked in a processing state");

        Assert.isTrue(em.contains(extCall), "the extCall must be attached");
        // note: https://blogs.oracle.com/carolmcdonald/entry/jpa_2_0_concurrency_and
        em.lock(extCall, LockModeType.OPTIMISTIC);
        extCall.setState(ExternalCallStateEnum.PROCESSING);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ExternalCall findConfirmation(Seconds interval) {
        // find confirmation that was lastly processed before specified interval
        LocalDateTime lastUpdateLimit = LocalDateTime.now().minus(interval);

        String jSql = "SELECT c "
                + "FROM " + ExternalCall.class.getName() + " c "
                + "WHERE c.operationName = :operationName"
                + "     AND c.state = :state"
                + "     AND c.lastUpdateTimestamp < :lastUpdateTimestamp"
                + " ORDER BY c.creationTimestamp";

        TypedQuery<ExternalCall> q = em.createQuery(jSql, ExternalCall.class);
        q.setParameter("operationName", ExternalCall.CONFIRM_OPERATION);
        q.setParameter("state", ExternalCallStateEnum.FAILED);
        q.setParameter("lastUpdateTimestamp", new Timestamp(lastUpdateLimit.toDate().getTime()));
        q.setMaxResults(1);
        List<ExternalCall> extCalls = q.getResultList();

        if (extCalls.isEmpty()) {
            return null;
        } else {
            return extCalls.get(0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExternalCall lockConfirmation(final ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() != ExternalCallStateEnum.PROCESSING,
                "the extCall must not be locked in a processing state");
        Assert.isTrue(em.contains(extCall), "the extCall must be attached");

        em.lock(extCall, LockModeType.PESSIMISTIC_WRITE);
        extCall.setState(ExternalCallStateEnum.PROCESSING);
        return extCall;
    }

    @Override
    public List<ExternalCall> findProcessingExternalCalls(Seconds interval) {
        LocalDateTime startProcessLimit = LocalDateTime.now().minus(interval);

        String jSql = "SELECT c "
                + "FROM " + ExternalCall.class.getName() + " c "
                + "WHERE c.state = '" + ExternalCallStateEnum.PROCESSING + "'"
                + "     AND c.lastUpdateTimestamp < :time";

        TypedQuery<ExternalCall> q = em.createQuery(jSql, ExternalCall.class);
        q.setParameter("time", new Timestamp(startProcessLimit.toDate().getTime()));
        q.setMaxResults(MAX_MESSAGES_IN_ONE_QUERY);
        return q.getResultList();
    }
}
