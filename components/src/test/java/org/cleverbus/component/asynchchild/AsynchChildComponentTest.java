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

package org.cleverbus.component.asynchchild;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.BindingTypeEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.component.AbstractComponentsDbTest;
import org.cleverbus.test.EntityTypeTestEnum;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test suite for {@link AsynchChildComponent}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Transactional
public class AsynchChildComponentTest extends AbstractComponentsDbTest {

    private static final String MSG_BODY = "some body";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message msg;

    @Before
    public void prepareMessage() throws Exception {
        Date currDate = new Date();

        msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("payload");
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);

        em.persist(msg);
        em.flush();
    }

    @Test
    public void testWrongUri_noService() throws Exception {
        try {
            callComponent("asynch-child:createCustomer?bindingType=HARD&correlationId=566&sourceSystem=CRM");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("Service name can't be empty for asynch-child component"));
        }
    }

    @Test
    public void testWrongUri_noOperationName() throws Exception {
        try {
            callComponent("asynch-child:service");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("Service name can't be empty for asynch-child component"));
        }
    }

    @Test
    public void testWrongUri_wrongBindingType() throws Exception {
        try {
            callComponent("asynch-child:customer:createCustomer?bindingType=MEDIUM");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    endsWith("org.cleverbus.api.entity.BindingTypeEnum.MEDIUM"));
        }
    }

    @Test
    public void testCreateChild() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        callComponent("asynch-child:customer:createCustomer?bindingType=HARD&correlationId=566&sourceSystem=CRM"
                + "&objectId=111&funnelValue=val");

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), is(msg.getMsgId()));
        assertThat(asynchMsg.getParentBindingType(), is(BindingTypeEnum.HARD));
        assertThat(asynchMsg.getCorrelationId(), is("566"));
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is("CRM"));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));
        assertThat(asynchMsg.getObjectId(), is("111"));
        assertThat(asynchMsg.getFunnelValue(), is("val"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testCreateChild_softBinding() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        callComponent("asynch-child:customer:createCustomer?bindingType=SOFT&correlationId=566");

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), is(msg.getMsgId()));
        assertThat(asynchMsg.getParentBindingType(), is(BindingTypeEnum.SOFT));
        assertThat(asynchMsg.getCorrelationId(), is("566"));
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is("CRM"));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testCreateChildFromSyncRoute() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("asynch-child:customer:createCustomer");
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        producer.sendBody(MSG_BODY);

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), nullValue());
        assertThat(asynchMsg.getParentBindingType(), nullValue());
        assertThat(asynchMsg.getCorrelationId(), notNullValue());
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is(AsynchChildProducer.DEFAULT_EXTERNAL_SYSTEM));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    private void createAsynchRoute() throws Exception {
        RouteBuilder asynchRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from(AsynchConstants.URI_ASYNC_MSG)
                        .to("mock:test");
            }
        };

        mock.setExpectedMessageCount(1);

        getCamelContext().addRoutes(asynchRoute);
    }

    private void callComponent(final String uri) throws Exception {
        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to(uri);
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);
    }
}
