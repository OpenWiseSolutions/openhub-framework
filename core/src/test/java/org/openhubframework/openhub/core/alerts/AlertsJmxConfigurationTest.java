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

package org.openhubframework.openhub.core.alerts;

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
 * Test suite for {@link AlertsJmxConfiguration}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class AlertsJmxConfigurationTest extends AbstractCoreTest {

    @Autowired
    private ConfigurableEnvironment env;

    private AlertsJmxConfiguration jmx;

    @Before
    public void prepareAlertsConf() {
        // prepare properties
        Properties props = new Properties();

        // add alert (min. version)
        String prefix = AlertsPropertiesConfiguration.ALERT_PROP_PREFIX + "1.";
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID");
        props.put(prefix + AlertsPropertiesConfiguration.LIMIT_PROP, "11");
        props.put(prefix + AlertsPropertiesConfiguration.SQL_PROP, "select COUNT()");

        // add alert (max. version)
        prefix = AlertsPropertiesConfiguration.ALERT_PROP_PREFIX + "66.";
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID2");
        props.put(prefix + AlertsPropertiesConfiguration.LIMIT_PROP, "11");
        props.put(prefix + AlertsPropertiesConfiguration.SQL_PROP, "select COUNT()");
        props.put(prefix + AlertsPropertiesConfiguration.ENABLED_PROP, "true");
        props.put(prefix + AlertsPropertiesConfiguration.MAIL_SBJ_PROP, "subject");
        props.put(prefix + AlertsPropertiesConfiguration.MAIL_BODY_PROP, "body");

        env.getPropertySources().addFirst(new PropertiesPropertySource("alerts-test", props));

        // configure alerts
        AlertsPropertiesConfiguration conf = new AlertsPropertiesConfiguration(env);
        conf.initProps();

        jmx = new AlertsJmxConfiguration(conf);
    }

    @Test
    public void testMBeanInfo() {
        MBeanInfo mBeanInfo = jmx.getMBeanInfo();

        assertThat(mBeanInfo, notNullValue());
        MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        assertThat(attributes.length, is(4));
        assertThat(attributes[0].getName(), is("ID2.enabled"));
        assertThat(attributes[1].getName(), is("ID2.limit"));
        assertThat(attributes[2].getName(), is("ID.enabled"));
        assertThat(attributes[3].getName(), is("ID.limit"));
    }

    @Test
    public void testGetAttribute() throws MBeanException, AttributeNotFoundException, ReflectionException {
        Object attribute = jmx.getAttribute("ID.enabled");
        assertThat(attribute, notNullValue());
        assertThat(attribute.toString(), is("true"));

        attribute = jmx.getAttribute("ID.limit");
        assertThat(attribute, notNullValue());
        assertThat(attribute.toString(), is("11"));

        try {
            jmx.getAttribute("something");
            fail("there is no attribute with specified key");
        } catch (AttributeNotFoundException ex) {
            // it's OK
        }
    }

    @Test
    public void testSetAttribute() throws Exception {
        Attribute attribute = new Attribute("ID.enabled", "false");
        jmx.setAttribute(attribute);
        assertThat(jmx.getAttribute("ID.enabled").toString(), is("false"));

        attribute = new Attribute("ID.limit", "8");
        jmx.setAttribute(attribute);
        assertThat(jmx.getAttribute("ID.limit").toString(), is("8"));

        try {
            attribute = new Attribute("something", "enable");
            jmx.setAttribute(attribute);
            fail("there is no attribute with specified key");
        } catch (AttributeNotFoundException ex) {
            // it's OK
        }
    }
}
