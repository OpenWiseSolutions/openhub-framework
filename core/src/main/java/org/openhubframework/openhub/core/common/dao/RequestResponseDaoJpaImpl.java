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

import static org.springframework.util.StringUtils.hasText;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.api.entity.Response;


/**
 * JPA implementation of {@link RequestResponseDao} interface.
 *
 * @author Petr Juza
 * @since 0.4
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class RequestResponseDaoJpaImpl implements RequestResponseDao {

    public static final int MAX_REQUESTS_IN_ONE_QUERY = 50;

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public void insertRequest(Request request) {
        em.persist(request);
    }

    @Override
    public void insertResponse(Response response) {
        em.persist(response);
    }

    @Nullable
    @Override
    public Request findLastRequest(String uri, String responseJoinId) {
        Assert.hasText(uri, "the uri must not be empty");
        Assert.hasText(responseJoinId, "the responseJoinId must not be empty");

        String jSql = "SELECT r " +
                "FROM " + Request.class.getName() + " r " +
                "WHERE r.responseJoinId = :responseJoinId AND r.uri = :uri " +
                "ORDER BY r.reqTimestamp";

        TypedQuery<Request> q = em.createQuery(jSql, Request.class);
        q.setParameter("responseJoinId", responseJoinId);
        q.setParameter("uri", uri);

        // we search by unique key - it's not possible to have more records
        List<Request> requests = q.getResultList();
        if (requests.isEmpty()) {
            return null;
        } else {
            return requests.get(0); // if find more items then return first one only
        }
    }

    @Override
    public List<Request> findByCriteria(Instant from, Instant to, String subUri, String subRequest) {
        Assert.notNull(from, "the from must not be null");
        Assert.notNull(to, "the to must not be null");

        String jSql = "SELECT r "
                + "         FROM " + Request.class.getName() + " r " +
                "           WHERE r.reqTimestamp >= :from " +
                "               AND r.reqTimestamp <= :to ";

        if (hasText(subUri)) {
            jSql += "           AND r.uri like :subUri)";
        }
        if (hasText(subRequest)) {
            jSql += "           AND r.request like :subRequest)";
        }

        jSql += "           ORDER BY r.reqTimestamp";

        TypedQuery<Request> q = em.createQuery(jSql, Request.class);
        q.setParameter("from", from);
        q.setParameter("to", to);
        if (hasText(subUri)) {
            q.setParameter("subUri", "%" + subUri + "%");
        }
        if (hasText(subRequest)) {
            q.setParameter("subRequest", "%" + subRequest + "%");
        }
        q.setMaxResults(MAX_REQUESTS_IN_ONE_QUERY);

        return q.getResultList();
    }
}
