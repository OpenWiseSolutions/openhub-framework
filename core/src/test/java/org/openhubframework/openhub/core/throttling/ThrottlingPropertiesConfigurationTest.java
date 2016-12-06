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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.openhubframework.openhub.spi.throttling.ThrottleProps;
import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Test suite for {@link ThrottlingPropertiesConfiguration}.
 *
 * @author Petr Juza
 */
public class ThrottlingPropertiesConfigurationTest {

    //TODO PJUZA correct unit tests for properties
//    @Test
//    public void testConf() {
//        // prepare properties
//        String prefix = ThrottlingPropertiesConfiguration.PROPERTY_PREFIX;
//        Properties props = new Properties();
//        props.put(ThrottlingPropertiesConfiguration.DEFAULT_LIMIT_PROP, "5");
//        props.put(ThrottlingPropertiesConfiguration.DEFAULT_INTERVAL_PROP, "15");
//
//        props.put(prefix + "crm.op1", "10");
//        props.put(prefix + "crm.op2", "10/70");
//        props.put(prefix + "billing.*", "50");
//        props.put(prefix + "*.sendSms", "100");
//        props.put(prefix + "*.sendSms", "100/6");
//
//        // create configuration
//        ThrottlingPropertiesConfiguration conf = new ThrottlingPropertiesConfiguration(props);
//
//        // verify
//        assertThrottleProp(conf, "crm", "op1", 10, 15);
//        assertThrottleProp(conf, "crm", "op2", 10, 70);
//        assertThrottleProp(conf, "crm", ThrottleScope.ANY_SERVICE, 10, 15);
//        assertThrottleProp(conf, "crm", "sendSms", 100, 6);
//
//        assertThrottleProp(conf, "billing", ThrottleScope.ANY_SERVICE, 50, 15);
//        assertThrottleProp(conf, "billing", "activateSubscriber", 50, 15);
//
//        assertThrottleProp(conf, "billing", "sendSmsWithParams", 50, 15);
//        assertThrottleProp(conf, ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms", 100, 6);
//
//        assertThrottleProp(conf, "erp", "createDeposit", 5, 15);
//    }

    private void assertThrottleProp(ThrottlingPropertiesConfiguration conf, String sourceSystem, String serviceName,
                                    int expLimit, int expInterval) {
        ThrottleScope throttleScope = new ThrottleScope(sourceSystem, serviceName);

        ThrottleProps throttleProps = conf.getThrottleProps(throttleScope);
        assertThat(throttleProps, notNullValue());

        assertThat("assert limit for " + throttleScope, throttleProps.getLimit(), is(expLimit));
        assertThat("assert interval for " + throttleScope, throttleProps.getInterval(), is(expInterval));
    }
}
