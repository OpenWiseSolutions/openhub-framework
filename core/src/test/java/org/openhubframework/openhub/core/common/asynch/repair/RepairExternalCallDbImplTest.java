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

package org.openhubframework.openhub.core.common.asynch.repair;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;


/**
 * Tests {@link RepairExternalCallDbImpl}
 */
@Transactional
@ContextConfiguration(loader = SpringockitoContextLoader.class)
public class RepairExternalCallDbImplTest extends AbstractCoreDbTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepairExternalCallDbImpl.class);

    @Autowired
    private ExternalCallDao externalCallDao;

    @Autowired
    private RepairExternalCallDbImpl externalCallService;

    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;


    @Test
    public void testRepairProcessingExternalCallsMany() throws Exception {
        ExternalCall[] externalCalls = createAndSaveExternalCalls(119);

        // 3 times to because MAX_MESSAGES_IN_ONE_QUERY=50
        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();

        for (ExternalCall externalCall : externalCalls) {
            LOG.info("Verifying external call {}", externalCall);
            ExternalCall found = externalCallDao.getExternalCall(
                    externalCall.getOperationName(), externalCall.getEntityId());
            assertThat(found, notNullValue());
            assertThat(found.getState(), is(ExternalCallStateEnum.FAILED));
        }
    }

    @Test
    public void testRepairProcessingExternalCallsOne() throws Exception {
        ExternalCall externalCall = createAndSaveExternalCalls(1)[0];

        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();

        ExternalCall found = externalCallDao.getExternalCall(
                externalCall.getOperationName(), externalCall.getEntityId());
        assertThat(found, notNullValue());
        assertThat(found.getState(), is(ExternalCallStateEnum.FAILED));
    }

    private ExternalCall[] createAndSaveExternalCalls(final int quantity) {
        TransactionTemplate tx = new TransactionTemplate(jpaTransactionManager);
        return tx.execute(new TransactionCallback<ExternalCall[]>() {
            @Override
            public ExternalCall[] doInTransaction(TransactionStatus status) {
                ExternalCall[] extCalls = new ExternalCall[quantity];
                for (int i = 0; i < extCalls.length; i++) {
                    Message message = createMessage(ExternalSystemTestEnum.CRM, ServiceTestEnum.CUSTOMER,
                            "someOperation", "some payload");

                    extCalls[i] = ExternalCall.createProcessingCall(
                            "direct:someOperation", UUID.randomUUID().toString(), message);
                    extCalls[i].setLastUpdateTimestamp(DateTime.now().minusHours(1).toDate());
                    em.persist(message);
                    em.persist(extCalls[i]);
                }
                em.flush();
                return extCalls;
            }
        });
    }
}
