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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.model.CallbackResponse;
import org.openhubframework.openhub.api.asynch.model.ConfirmationTypes;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.exception.StoppingException;
import org.openhubframework.openhub.api.exception.ThrottlingExceededException;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link AsynchInMessageRoute}.
 *
 * @author Petr Juza
 */
@ActiveRoutes(classes = AsynchInMessageRoute.class)
public class AsynchInMessageRouteTest extends AbstractCoreDbTest {

    private static final String FUNNEL_VALUE = "774724557";

    @Produce(uri = AsynchConstants.URI_ASYNCH_IN_MSG)
    private ProducerTemplate producer;

    @Produce(uri = AsynchInMessageRoute.URI_GUARANTEED_ORDER_ROUTE)
    private ProducerTemplate guaranteedProducer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Before
    public void prepareData() {
        getHeaders().put(AsynchConstants.SERVICE_HEADER, ServiceTestEnum.CUSTOMER);
        getHeaders().put(AsynchConstants.OPERATION_HEADER, "setCustomer");
        getHeaders().put(AsynchConstants.OBJECT_ID_HEADER, "567");
    }

    @Test
    public void testResponseOK() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        mock.assertIsSatisfied();

        // verify response
        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getBody(), instanceOf(CallbackResponse.class));
        CallbackResponse callbackResponse = (CallbackResponse) exchange.getIn().getBody();
        assertThat(callbackResponse.getStatus(), is(ConfirmationTypes.OK));

        // verify DB
        int msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        final List<Message> messages = getJdbcTemplate().query("select * from message", new RowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
                TraceIdentifier traceIdentifier = getTraceHeader().getTraceIdentifier();

                // verify row values
                assertThat(rs.getLong("msg_id"), notNullValue());
                assertThat(rs.getString("correlation_id"), is(traceIdentifier.getCorrelationID()));
                assertThat((int)rs.getShort("failed_count"), is(0));
                assertThat(rs.getString("failed_desc"), nullValue());
                assertThat(rs.getString("failed_error_code"), nullValue());
                assertThat(rs.getTimestamp("last_update_timestamp"), notNullValue());
                assertThat(rs.getTimestamp("start_process_timestamp"), notNullValue());
                assertThat(rs.getTimestamp("msg_timestamp").compareTo(traceIdentifier.getTimestamp().toDate()), is(0));
                assertThat(rs.getString("object_id"), is(getHeaders().get(AsynchConstants.OBJECT_ID_HEADER)));
                assertThat(rs.getString("operation_name"), is(getHeaders().get(AsynchConstants.OPERATION_HEADER)));
                assertThat(rs.getString("payload"), is("bodyContent"));
                assertThat(rs.getTimestamp("receive_timestamp"), notNullValue());
                assertThat(rs.getString("service"), is(ServiceTestEnum.CUSTOMER.getServiceName()));
                assertThat(rs.getString("source_system"), is(ExternalSystemTestEnum.CRM.getSystemName()));
                assertThat(MsgStateEnum.valueOf(rs.getString("state")), is(MsgStateEnum.PROCESSING));
                assertThat(rs.getString("funnel_value"), nullValue());
                assertThat(rs.getString("parent_binding_type"), nullValue());
                assertThat(rs.getString("funnel_component_id"), nullValue());
                assertThat(rs.getLong("parent_msg_id"), is(0L));
                assertThat(rs.getBoolean("guaranteed_order"), is(false));
                assertThat(rs.getBoolean("exclude_failed_state"), is(false));

                return new Message();
            }
        });

        assertThat(messages.size(), is(1));
    }

    @Test
    public void testResponseFAIL_noServiceHeader() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        // clear mandatory header => FAIL response
        getHeaders().remove(AsynchConstants.SERVICE_HEADER);
        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        assertErrorResponse("PredicateValidationException");
    }

    @Test
    public void testResponseFAIL_duplicateMsg() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        // add message with the same msgID (that's why we use JDBC) => FAIL response
        String sql = "INSERT INTO message "
                + " (correlation_id, failed_count, failed_desc, failed_error_code, last_update_timestamp, msg_timestamp,"
                + "     object_id, operation_name, payload, receive_timestamp, service, source_system, state, msg_id,"
                + "     guaranteed_order, exclude_failed_state)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, getTraceHeader().getTraceIdentifier().getCorrelationID(), 0, "", "", null,
                new Date(), "", "opName", "payload", new Date(), ServiceTestEnum.CUSTOMER.toString(),
                ExternalSystemTestEnum.CRM.toString(), MsgStateEnum.NEW.toString(), 1, false, false);

        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        assertErrorResponse("Unique index or primary key violation");
    }

    @Test
    public void testResponseFAIL_throttling() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("throttleProcess").replace().throwException(new ThrottlingExceededException("error"));

                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(0);

        try {
            producer.sendBodyAndHeaders("bodyContent", getHeaders());

            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(ThrottlingExceededException.class));
        }

        mock.assertIsSatisfied();
    }

    @Test
    public void testResponseFAIL_stopping() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("stopChecking").replace().throwException(new StoppingException("stop"));

                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(0);

        try {
            producer.sendBodyAndHeaders("bodyContent", getHeaders());

            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(StoppingException.class));
        }

        mock.assertIsSatisfied();
    }

    private void assertErrorResponse(String addInfo) throws InterruptedException {
        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        CallbackResponse callbackResponse = exchange
                .getProperty(AsynchConstants.ERR_CALLBACK_RES_PROP, CallbackResponse.class);
        assertThat(callbackResponse.getStatus(), is(ConfirmationTypes.FAIL));
        assertThat(callbackResponse.getAdditionalInfo(), containsString(addInfo));
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT), notNullValue());
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_GUARANTEED_ORDER)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("toAsyncRoute").replace().to("mock:test");
                    }
                });

        // prepare message - only one message
        Message msg = insertNewMessage("id1", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);

        mock.expectedMessageCount(1);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - second message with different funnel value
        msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, "some value", true);

        mock.expectedMessageCount(2);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - third message that is not guaranteed
        msg = insertNewMessage("id3", MsgStateEnum.PROCESSING, FUNNEL_VALUE, false);

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);

        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, FUNNEL_VALUE, true);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));

        // action
        guaranteedProducer.sendBody(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    private Message insertNewMessage(String correlationId, MsgStateEnum state, @Nullable String funnelValue,
            boolean guaranteedOrder) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(state);

        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);
        msg.setFunnelValue(funnelValue);
        msg.setGuaranteedOrder(guaranteedOrder);

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();

        return msg;
    }
}
