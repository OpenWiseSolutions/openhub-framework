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

package org.openhubframework.openhub.core.configuration;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.core.common.dao.DbConst;


/**
 * JPA implementation of {@link DbConfigurationParamDao} interface.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class DbConfigurationParamDaoJpaImpl implements DbConfigurationParamDao {

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public void insert(DbConfigurationParam parameter) {
        Constraints.notNull(parameter, "parameter must not be null");
        em.persist(parameter);
    }

    @Override
    public void update(DbConfigurationParam parameter) {
        Constraints.notNull(parameter, "parameter must not be null");
        em.merge(parameter);
    }

    @Override
    public Optional<DbConfigurationParam> findParameter(String code) {
        Constraints.notNull(code, "the code must not be null");

        return Optional.ofNullable(em.find(DbConfigurationParam.class, code));
    }

    @Override
    public List<DbConfigurationParam> findAllParameters() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<DbConfigurationParam> cq = cb.createQuery(DbConfigurationParam.class);
        Root<DbConfigurationParam> from = cq.from(DbConfigurationParam.class);
        cq.select(from).orderBy(cb.asc(from.get("categoryCode")), cb.asc(from.get("code")));

        TypedQuery<DbConfigurationParam> q = em.createQuery(cq);
        return q.getResultList();
    }
}
