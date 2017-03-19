/*
 * Copyright 2016 the original author or authors.
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

package org.openhubframework.openhub.core.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;


/**
 * Test suites that validates capability injection of {@link ConfigurationItem}.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
//TODO (thanus, 18/12/2016, TASK: OHRJIRA-9) it is necessary to create internal test configurations for OHF tests
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoConfigurationItemProperties.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "ohf.test.key=5"
})
public class AutoConfigurationItemPropertiesTest {

    @ConfigurableValue("ohf.test.key")
    private ConfigurationItem<Long> tested;

    @Test
    public void testGetValue() {
        // test
        final Long value = tested.getValue();
        assertThat(value, is(5L));
    }
}