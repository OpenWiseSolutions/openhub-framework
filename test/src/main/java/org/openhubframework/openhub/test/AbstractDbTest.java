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

package org.openhubframework.openhub.test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import org.openhubframework.openhub.api.entity.*;
import org.openhubframework.openhub.common.Profiles;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;


/**
 * Extends {@link AbstractTest} and adds support for test with database.
 *
 * @author Petr Juza
 */
@ActiveProfiles(profiles = Profiles.H2)
public abstract class AbstractDbTest extends AbstractTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDbTest.class);

    /**
     * Default database unit name of Hibernate.
     */
    private static final String UNIT_NAME = "OpenHub";

    @PersistenceContext(unitName = UNIT_NAME)
    protected EntityManager em;

    @Autowired
    protected PlatformTransactionManager transactionManager;

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

    /**
     * Print current state of entities.
     */
    public void printEntities() {
        List<Message> messages = em.createQuery(
                "SELECT m FROM " + Message.class.getName() + " m", Message.class).getResultList();
        List<ExternalCall> externalCalls = em.createQuery(
                "SELECT c FROM " + ExternalCall.class.getName() + " c", ExternalCall.class).getResultList();
        List<Request> requests = em.createQuery(
                "SELECT r FROM " + Request.class.getName() + " r", Request.class).getResultList();
        List<Response> responses = em.createQuery(
                "SELECT r FROM " + Response.class.getName() + " r", Response.class).getResultList();

        LOG.info("Messages:\n{}", StringUtils.join(messages, "\n"));
        LOG.info("External Calls:\n{}", StringUtils.join(externalCalls, "\n"));
        LOG.info("Requests:\n{}", StringUtils.join(requests, "\n"));
        LOG.info("Responses:\n{}", StringUtils.join(responses, "\n"));
    }

    /**
     * Creates new message (without saving into database).
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

        Instant now = Instant.now();

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

    /**
     * Creates and saves new message.
     *
     * @param sourceSystem the source system
     * @param service the service
     * @param operationName the operation name
     * @param payload the payload
     * @return message
     */
    protected Message createAndSaveMessage(ExternalSystemExtEnum sourceSystem, ServiceExtEnum service,
            String operationName, String payload) {
        return createAndSaveMessages(1, sourceSystem, service, operationName, payload)[0];
    }

    /**
     * Creates and saves new messages.
     *
     * @param messageCount How many messages will be created
     * @param sourceSystem the source system
     * @param service the service
     * @param operationName the operation name
     * @param payload the payload
     * @return messages
     */
    protected Message[] createAndSaveMessages(final int messageCount, final ExternalSystemExtEnum sourceSystem,
            final ServiceExtEnum service, final String operationName, final String payload) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        return tx.execute(new TransactionCallback<Message[]>() {
            @Override
            public Message[] doInTransaction(TransactionStatus status) {
                Message[] messages = new Message[messageCount];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = createMessage(sourceSystem, service, operationName, payload);
                    messages[i].setMsgTimestamp(Instant.now().plusSeconds(i*5));
                    em.persist(messages[i]);
                }
                em.flush();
                return messages;
            }
        });
    }

    /**
     * Creates and saves new messages.
     *
     * @param messageCount How many messages will be created
     * @param messageCallback Callback handler that can adjust {@link Message}
     * @return messages
     */
    protected Message[] createAndSaveMessages(final int messageCount, final MessageCallback messageCallback) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        return tx.execute(new TransactionCallback<Message[]>() {
            @Override
            public Message[] doInTransaction(TransactionStatus status) {
                Message[] messages = new Message[messageCount];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = createMessage(ExternalSystemTestEnum.CRM, ServiceTestEnum.CUSTOMER,
                            "testOperation", "test payload");

                    try {
                        messageCallback.beforeInsert(messages[i], i+1);
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
     * Contract for defining callback handler that can adjust {@link Message} before inserting into database.
     */
    public interface MessageCallback {

        /**
         * Do whatever you want before the message will be inserted into database
         *
         * @param message The message
         * @param order The order of the message (if there is only one message, then always order is one)
         */
        void beforeInsert(Message message, int order) throws Exception;
    }
}
