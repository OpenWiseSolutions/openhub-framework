/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.fail;

import java.util.Properties;
import javax.management.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import org.openhubframework.openhub.core.AbstractCoreTest;


/**
 * Test suite for {@link JmxThrottlingConfiguration}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class JmxThrottlingConfigurationTest extends AbstractCoreTest {

    @Autowired
    private ConfigurableEnvironment env;

    private JmxThrottlingConfiguration jmx;

    @Before
    public void prepareThrottlingConf() {
        // prepare properties
        String prefix = ThrottlingPropertiesConfiguration.PROPERTY_PREFIX;
        Properties props = new Properties();
        props.put(ThrottlingPropertiesConfiguration.DEFAULT_LIMIT_PROP, "5");
        props.put(ThrottlingPropertiesConfiguration.DEFAULT_INTERVAL_PROP, "15");

        props.put(prefix + "crm.op1", "10");
        props.put(prefix + "crm.op2", "10/70");
        props.put(prefix + "billing.*", "50");

        env.getPropertySources().addFirst(new PropertiesPropertySource("throttling-test", props));

        // create configuration
        ThrottlingPropertiesConfiguration conf = new ThrottlingPropertiesConfiguration(env);
        conf.initProps();

        jmx = new JmxThrottlingConfiguration(conf);
    }

    @Test
    public void testMBeanInfo() {
        MBeanInfo mBeanInfo = jmx.getMBeanInfo();

        assertThat(mBeanInfo, notNullValue());
        MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        assertThat(attributes.length, is(4));
        assertThat(attributes[0].getName(), is("crm.op1"));
        assertThat(attributes[1].getName(), is("billing.*"));
        assertThat(attributes[2].getName(), is("crm.op2"));
        assertThat(attributes[3].getName(), is("*.*"));
    }

    @Test
    public void testGetAttribute() throws MBeanException, AttributeNotFoundException, ReflectionException {
        Object attribute = jmx.getAttribute("crm.op2");

        assertThat(attribute, notNullValue());
        assertThat(attribute.toString(), is("10/70"));

        try {
            jmx.getAttribute("something");
            fail("there is no attribute with specified key");
        } catch (AttributeNotFoundException ex) {
            // it's OK
        }
    }

    @Test
    public void testSetAttribute() throws Exception {
        Attribute attribute = new Attribute("crm.op2", "20/40");
        jmx.setAttribute(attribute);

        assertThat(jmx.getAttribute("crm.op2").toString(), is("20/40"));

        try {
            attribute = new Attribute("something", "20/40");
            jmx.setAttribute(attribute);
            fail("there is no attribute with specified key");
        } catch (AttributeNotFoundException ex) {
            // it's OK
        }
    }
}
