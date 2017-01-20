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

package org.openhubframework.openhub.core.common.asynch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.joda.time.Seconds;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.confirm.ConfirmationCallback;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.ValidationIntegrationException;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.common.asynch.confirm.ConfirmationPollExecutor;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link AsynchMessageRoute}.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
@Transactional
public class AsynchMessageRouteConfirmTest extends AbstractCoreDbTest {

    private static final String REQUEST_XML =
            "  <cus:setCustomerRequest xmlns=\"http://openhubframework.org/ws/Customer-v1\""
                    + "         xmlns:cus=\"http://openhubframework.org/ws/CustomerService-v1\">"
                    + "         <cus:customer>"
                    + "            <externalCustomerID>12</externalCustomerID>"
                    + "            <customerNo>23</customerNo>"
                    + "            <customerTypeID>2</customerTypeID>"
                    + "            <lastName>Juza</lastName>"
                    + "            <firstName>Petr</firstName>"
                    + "         </cus:customer>"
                    + "  </cus:setCustomerRequest>";

    @Produce(uri = AsynchMessageRoute.URI_SYNC_MSG)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message msg;

    private String subRouteUri;

    @Autowired
    private ConfirmationCallback confirmationCallback;

    @Autowired
    private ConfirmationPollExecutor confirmationExecutor;

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    @ConfigurableValue(key = "ohf.asynch.confirmation.intervalSec")
    private ConfigurationItem<Seconds> interval;


    @Configuration
    public static class TestContextConfig {

        @Bean(name = ConfirmationCallback.BEAN)
        @Primary
        public ConfirmationCallback mockConfirmationCallback() {
            return mock(ConfirmationCallback.class);
        }
    }

    @Before
    public void prepareData() throws Exception {
        // message
        Date currDate = new Date();

        msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload(REQUEST_XML);
        msg.setLastUpdateTimestamp(currDate);

        subRouteUri = "direct:" + msg.getService().getServiceName() + "_" + msg.getOperationName()
                + AbstractBasicRoute.OUT_ROUTE_SUFFIX;
        getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class)
                    .handled(true)
                    .to(AsynchConstants.URI_ERROR_FATAL);

                from(subRouteUri)
                        .routeId("TestRoute")
                        .to(mock);
            }
        });
    }

    @Test
    public void testConfirmationStatusOK() throws Exception {
        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK));

        // verify confirmation sent OK
        verify(confirmationCallback).confirm(msg);
        verifyNoMoreInteractions(confirmationCallback);
    }

    @Test
    public void testConfirmationStatusFailed() throws Exception {
        // simulate fatal failure:
        mock.whenAnyExchangeReceived(throwException(new ValidationIntegrationException(InternalErrorEnum.E102)));

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.FAILED));

        // verify confirmation sent OK
        verify(confirmationCallback).confirm(msg);
        verifyNoMoreInteractions(confirmationCallback);
    }

    @Test
    public void testConfirmationFailedRepeat() throws Exception {
        if (interval.getValue().getSeconds() > 0) {
            fail("This test requires interval to be set to 0, otherwise the polling won't happen fast enough");
        }

        // force confirmation callback to fail:
        doThrow(new IntegrationException(InternalErrorEnum.E100, "Simulated Failure ONE")) // fail once
                .doThrow(new IntegrationException(InternalErrorEnum.E100, "Simulated Failure TWO")) // fail twice
                .doNothing() // succeed on the 3rd time
                .when(confirmationCallback).confirm(msg);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);
        Thread.sleep(1000);
        confirmationExecutor.run(); // push failed confirms from DB to confirm queue
        Thread.sleep(1000);
        confirmationExecutor.run(); // push failed confirms from DB to confirm queue

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK)); // message should stay OK

        // verify 2 failures + 1 success and nothing else:
        verify(confirmationCallback, never()).confirm(null);
        verify(confirmationCallback, times(3)).confirm(eq(msg));
        verifyNoMoreInteractions(confirmationCallback);
    }
}
