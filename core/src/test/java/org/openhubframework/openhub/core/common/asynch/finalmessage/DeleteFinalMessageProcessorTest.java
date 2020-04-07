/*
 * Copyright 2018 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.finalmessage;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.api.entity.Response;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.reqres.RequestResponseService;
import org.openhubframework.openhub.spi.extcall.ExternalCallService;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Simple test suite for {@link DeleteFinalMessageProcessor}.
 *
 * @author Karel Kovarik
 */
@TestPropertySource(properties = {
        "ohf.asynch.finalMessages.processingEnabled=true",
        "ohf.asynch.finalMessages.ok.saveTimeInSec=14400",
        "ohf.asynch.finalMessages.failed.saveTimeInSec=28800",
        "ohf.asynch.finalMessages.cancel.saveTimeInSec=7200",
        "ohf.asynch.finalMessages.iterationMessageLimit=1000",
        "ohf.asynch.finalMessages.deleteProcessor.enabled=true"
})
public class DeleteFinalMessageProcessorTest extends AbstractCoreDbTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ExternalCallService externalCallService;

    @Autowired
    private RequestResponseService requestResponseService;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    // tested
    @Autowired
    @Qualifier(DeleteFinalMessageProcessor.QUALIFIER)
    private FinalMessageProcessor deleteFinalMessageProcessor;

    @Before
    public void setup() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Test
    public void test_delete_simpleOk() {
        Message message = createAndSaveMessage(
                ExternalSystemTestEnum.CRM,
                ServiceTestEnum.ACCOUNT,
                "testOperation",
                "payload");

        Long id = message.getId();
        assertThat(messageService.findMessageById(id), notNullValue());
        assertThat(countInTable(Message.class), is(1L));

        // invoke tested
        transactionTemplate.execute((TransactionStatus status) -> {
            deleteFinalMessageProcessor.processMessage(message);
            return null; // without result
        });

        assertThat(messageService.findMessageById(id), nullValue());
        assertThat(countInTable(Message.class), is(0L));
    }

    @Test
    public void test_delete_withAllEntities() {
        final Message message = createAndSaveMessage(
                ExternalSystemTestEnum.CRM,
                ServiceTestEnum.ACCOUNT,
                "testOperation",
                "payload");
        final Long msgId = message.getId();

        Request request = Request.createRequest("http://test.url", "join id", "request payload", message);
        requestResponseService.insertRequest(request);
        requestResponseService.insertResponse(Response.createResponse(request, "response payload", null, message));

        externalCallService.prepare("operationUri", "operationKey", message);

        // verify data is present
        Message loadedMessage = messageService.findMessageById(msgId);
        assertThat(loadedMessage, notNullValue());
        assertThat(countInTable(Message.class), is(1L));
        assertThat(countInTable(ExternalCall.class), is(1L));
        assertThat(countInTable(Request.class), is(1L));
        assertThat(countInTable(Response.class), is(1L));

        // invoke tested
        transactionTemplate.execute((TransactionStatus status) -> {
            deleteFinalMessageProcessor.processMessage(message);
            return null; // without result
        });

        assertThat(messageService.findMessageById(msgId), nullValue());
        assertThat(countInTable(Message.class), is(0L));
        assertThat(countInTable(ExternalCall.class), is(0L));
        assertThat(countInTable(Request.class), is(0L));
        assertThat(countInTable(Response.class), is(0L));
    }

    @Test
    public void test_delete_withoutResponse() {
        final Message message = createAndSaveMessage(
                ExternalSystemTestEnum.CRM,
                ServiceTestEnum.ACCOUNT,
                "testOperation",
                "payload");
        final Long msgId = message.getId();

        Request request = Request.createRequest("jms:test.queue", "join id", "request payload", message);
        requestResponseService.insertRequest(request);

        // verify data is present
        assertThat(messageService.findMessageById(msgId), notNullValue());
        assertThat(countInTable(Message.class), is(1L));
        assertThat(countInTable(Request.class), is(1L));

        // invoke tested
        transactionTemplate.execute((TransactionStatus status) -> {
            deleteFinalMessageProcessor.processMessage(message);
            return null; // without result
        });

        assertThat(messageService.findMessageById(msgId), nullValue());
        assertThat(countInTable(Message.class), is(0L));
        assertThat(countInTable(Request.class), is(0L));
    }

    private long countInTable(Class clazz) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(t) FROM " + clazz.getName() + " t ", Long.class);
        return query.getSingleResult();
    }
}
