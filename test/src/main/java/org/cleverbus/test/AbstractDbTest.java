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

package org.cleverbus.test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.entity.Request;
import org.cleverbus.api.entity.Response;
import org.cleverbus.api.entity.ServiceExtEnum;
import org.cleverbus.common.log.Log;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Extends {@link AbstractTest} and adds support for test with database.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ContextConfiguration(locations = {"classpath:/META-INF/test_persistence.xml"})
@TransactionConfiguration(transactionManager = "jpaTxManager")
public abstract class AbstractDbTest extends AbstractTest {

    /**
     * Default database unit name of Hibernate.
     */
    public static final String UNIT_NAME = "CleverBus";

    @PersistenceContext(unitName = UNIT_NAME)
    protected EntityManager em;

    @Autowired
    @Qualifier(value = "jpaTxManager")
    protected JpaTransactionManager jpaTransactionManager;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    /**
     * Gets {@link JdbcTemplate} for test DB.
     *
     * @return JdbcTemplate
     */
    protected JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }

        return jdbcTemplate;
    }

    public void printEntities() {
        List<Message> messages = em.createQuery(
                "SELECT m FROM " + Message.class.getName() + " m", Message.class).getResultList();
        List<ExternalCall> externalCalls = em.createQuery(
                "SELECT c FROM " + ExternalCall.class.getName() + " c", ExternalCall.class).getResultList();
        List<Request> requests = em.createQuery(
                "SELECT r FROM " + Request.class.getName() + " r", Request.class).getResultList();
        List<Response> responses = em.createQuery(
                "SELECT r FROM " + Response.class.getName() + " r", Response.class).getResultList();

        Log.info("Messages:\n{}", StringUtils.join(messages, "\n"));
        Log.info("External Calls:\n{}", StringUtils.join(externalCalls, "\n"));
        Log.info("Requests:\n{}", StringUtils.join(requests, "\n"));
        Log.info("Responses:\n{}", StringUtils.join(responses, "\n"));
    }

    /**
     * Creates new message.
     *
     * @param sourceSystem the source system
     * @param service the service
     * @param operationName the operation name
     * @param payload the payload
     * @return message
     */
    protected Message createMessage(ExternalSystemExtEnum sourceSystem, ServiceExtEnum service, String operationName,
            String payload) {

        Message msg = new Message();

        Date now = new Date();

        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(now);
        msg.setReceiveTimestamp(now);
        msg.setSourceSystem(sourceSystem);
        msg.setCorrelationId(UUID.randomUUID().toString());

        msg.setService(service);
        msg.setOperationName(operationName);
        msg.setPayload(payload);
        msg.setLastUpdateTimestamp(now);

        return msg;
    }

    protected Message createAndSaveMessage(ExternalSystemExtEnum sourceSystem,
                                           final ServiceExtEnum service,
                                           final String operationName,
                                           final String payload) {
        return createAndSaveMessages(1, sourceSystem, service, operationName, payload)[0];
    }

    protected Message[] createAndSaveMessages(final int messageCount,
                                              final ExternalSystemExtEnum sourceSystem,
                                              final ServiceExtEnum service,
                                              final String operationName,
                                              final String payload) {
        TransactionTemplate tx = new TransactionTemplate(jpaTransactionManager);
        return tx.execute(new TransactionCallback<Message[]>() {
            @Override
            public Message[] doInTransaction(TransactionStatus status) {
                Message[] messages = new Message[messageCount];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = createMessage(sourceSystem, service, operationName, payload);
                    messages[i].setMsgTimestamp(DateTime.now().plusSeconds(i*5).toDate());
                    em.persist(messages[i]);
                }
                em.flush();
                return messages;
            }
        });
    }

    protected Message[] createAndSaveMessages(final int messageCount, final MessageProcessor initializer) {
        TransactionTemplate tx = new TransactionTemplate(jpaTransactionManager);
        return tx.execute(new TransactionCallback<Message[]>() {
            @Override
            public Message[] doInTransaction(TransactionStatus status) {
                Message[] messages = new Message[messageCount];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = createMessage(ExternalSystemTestEnum.CRM, ServiceTestEnum.CUSTOMER,
                            "testOperation", "test payload");

                    try {
                        initializer.process(messages[i]);
                    } catch (Exception exc) {
                        throw new RuntimeException(exc);
                    }
                    em.persist(messages[i]);
                }
                em.flush();
                return messages;
            }
        });
    }

    /**
     * Interface for defining processors that can affect {@link Message} in one way or another.
     * Used for initializing the message before it's finally sent to its destination (async OUT route).
     */
    public static interface MessageProcessor {
        void process(Message message) throws Exception;
    }
}
