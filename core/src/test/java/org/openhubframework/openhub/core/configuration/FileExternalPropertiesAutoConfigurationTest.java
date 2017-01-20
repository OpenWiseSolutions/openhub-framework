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

package org.openhubframework.openhub.core.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.core.AbstractCoreDbTest;


/**
 * Test suites that validates {@link FileExternalPropertiesAutoConfiguration}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class FileExternalPropertiesAutoConfigurationTest extends AbstractCoreDbTest {

    // original value is in DB -> application-test.properties has value "orig"
    @ConfigurableValue(key = "ohf.test")
    private ConfigurationItem<String> mail;

    @Test
    public void testConfigurationItemValue() {
        assertThat(mail, notNullValue());
        assertThat(mail.getValue(), is("correct"));
    }
}
