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

package org.openhubframework.openhub.core.throttling;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;
import org.openhubframework.openhub.spi.throttling.ThrottlingProcessor;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;


/**
 * Test suite for {@link ThrottleProcessorImpl}.
 *
 * @author Petr Juza
 */
public class ThrottleMsgProcessorTest extends AbstractCoreTest {

    private static final String THROTTLING_PROPS_NAME = "throttling-test";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Autowired
    private ThrottlingProcessor throttlingProcessor;

    @Autowired
    private ConfigurableEnvironment env;

    @Before
    public void prepareConfiguration() {
        // prepare properties
        String prefix = ThrottlingPropertiesConfiguration.PROPERTY_PREFIX;
        Properties props = new Properties();
        props.put(prefix + "*.sendSms", "2/10");
        props.put(prefix + "crm.createCustomer", "2/10");

        env.getPropertySources().addFirst(new PropertiesPropertySource(THROTTLING_PROPS_NAME, props));

        // configure
        ThrottlingPropertiesConfiguration conf = initThrottlingConf();

        setPrivateField(throttlingProcessor, "configuration", conf);
    }

    private ThrottlingPropertiesConfiguration initThrottlingConf() {
        ThrottlingPropertiesConfiguration conf = new ThrottlingPropertiesConfiguration();
        setPrivateField(conf, "env", env);
        conf.initProps();
        return conf;
    }

    @Test
    public void testSyncProcessor() throws Exception {
        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            ThrottleScope throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms");
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        producer.sendBody("something");
        producer.sendBody("something");

        try {
            producer.sendBody("something");
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }

    @Test
    public void testSyncProcessorWithDefaults() throws Exception {
        // create configuration
        env.getPropertySources().remove(THROTTLING_PROPS_NAME);
        ThrottlingPropertiesConfiguration confDefaults = initThrottlingConf();

        setPrivateField(throttlingProcessor, "configuration", confDefaults);

        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            ThrottleScope throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms");
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        for (int i = 0; i < AbstractThrottlingConfiguration.DEFAULT_LIMIT; i++) {
            producer.sendBody("something");
        }

        try {
            producer.sendBody("something");
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }

    @Test
    public void testAsyncProcessor() throws Exception {
        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Message msg = exchange.getIn().getBody(Message.class);

                            Assert.notNull(msg, "the msg must not be null");

                            ThrottleScope throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(),
                                    msg.getOperationName());
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        Message msg = new Message();
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setOperationName("createCustomer");

        producer.sendBody(msg);
        producer.sendBody(msg);

        try {
            producer.sendBody(msg);
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }
}
