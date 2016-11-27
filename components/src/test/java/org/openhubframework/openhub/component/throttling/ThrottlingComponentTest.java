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

package org.openhubframework.openhub.component.throttling;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.component.AbstractComponentsTest;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test suite for {@link ThrottlingComponent}.
 *
 * @author Petr Juza
 */
@ContextConfiguration(locations = {"classpath:/org/openhubframework/openhub/component/throttling/test_throttling_conf.xml"})
public class ThrottlingComponentTest extends AbstractComponentsTest {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Test
    public void testWrongUri_wrongRequestType() throws Exception {
        try {
            callComponent("throttling:wrongReqType");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("request type must have one of the following values: 'sync' or 'async'"));
        }
    }

    @Test
    public void testWrongUri_noOperationNameForSyncRequestType() throws Exception {
        try {
            callComponent("throttling:sync");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("operation name is mandatory for 'sync' request type"));
        }
    }

    @Test
    public void testSuccessfulCall_sync() throws Exception {
        callComponent("throttling:sync:sendSms");
    }

    @Test
    public void testSuccessfulCall_async() throws Exception {
        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("throttling:async");
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        Message msg = new Message();
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setOperationName("createCustomer");

        producer.sendBody(msg);
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
        producer.sendBody("someBody");
    }
}
