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

package org.cleverbus.core.common.asynch.confirm;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nullable;
import javax.persistence.Query;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link ConfirmationPollExecutor}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ConfirmationPollExecutorTest extends AbstractCoreDbTest {

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private ConfirmationPollExecutor pollExecutor;

    @Autowired
    private ConfirmationService confirmationService;

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    @Value("${asynch.confirmation.interval}")
    private int interval;

    @Before
    public void prepareData() {
        if (interval > 0) {
            fail("This test requires interval to be set to 0, otherwise the polling won't happen fast enough");
        }

        setPrivateField(pollExecutor, "targetURI", "mock:test");
    }

    @Test
    public void testGetNextMessage_moreThreads() throws InterruptedException {
        // firstly commit messages to DB (we can commit because we have embedded DB for tests only)
        TransactionTemplate txTemplate = new TransactionTemplate(jpaTransactionManager);
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Message msg = insertNewMessage("1234_4567");
                confirmationService.insertFailedConfirmation(msg);

                msg = insertNewMessage("1234_4567_8");
                confirmationService.insertFailedConfirmation(msg);

                msg = insertNewMessage("1234_4567_9");
                confirmationService.insertFailedConfirmation(msg);
            }
        });

        // prepare threads
        int threads = 5;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    pollExecutor.run();
                } finally {
                    latch.countDown();
                }
            }
        };

        mock.expectedMessageCount(3);

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        mock.assertIsSatisfied();

        // verify messages
        ExternalCall extCall1 = findConfirmation("1234_4567");
        assertThat(extCall1, notNullValue());
        assertThat(extCall1.getState(), is(ExternalCallStateEnum.PROCESSING));

        ExternalCall extCall2 = findConfirmation("1234_4567_8");
        assertThat(extCall2, notNullValue());
        assertThat(extCall2.getState(), is(ExternalCallStateEnum.PROCESSING));

        ExternalCall extCall3 = findConfirmation("1234_4567_9");
        assertThat(extCall3, notNullValue());
        assertThat(extCall3.getState(), is(ExternalCallStateEnum.PROCESSING));


        // check confirmationService
        confirmationService.confirmationFailed(extCall1);
        extCall1 = findConfirmation("1234_4567");
        assertThat(extCall1.getState(), is(ExternalCallStateEnum.FAILED));

        confirmationService.confirmationComplete(extCall2);
        extCall2 = findConfirmation("1234_4567_8");
        assertThat(extCall2.getState(), is(ExternalCallStateEnum.OK));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private ExternalCall findConfirmation(String correlationId) {
        String jSql = "SELECT c "
                + "FROM ExternalCall c "
                + "WHERE c.entityId = ?1";

        Query q = em.createQuery(jSql);
        q.setParameter (1, correlationId);
        q.setMaxResults(1);
        List<ExternalCall> extCalls = (List<ExternalCall>) q.getResultList();

        if (extCalls.isEmpty()) {
            return null;
        } else {
            return extCalls.get(0);
        }
    }

    private Message insertNewMessage(String correlationId) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(MsgStateEnum.OK);

        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();

        return msg;
    }
}
