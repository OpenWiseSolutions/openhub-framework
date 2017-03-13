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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openhubframework.openhub.api.configuration.CoreProps.PROPERTY_INCLUDE_PATTERN;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.configuration.DataTypeEnum;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.core.AbstractCoreDbTest;


/**
 * Test suites that validates {@link DbPropertySource}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@TestPropertySource(properties = {
        PROPERTY_INCLUDE_PATTERN + "=^sth\\..*$"
})
@Transactional
public class DbPropertySourceTest extends AbstractCoreDbTest {

    @Autowired
    private Environment env;

    @Autowired
    private DbConfigurationParamDao paramDao;

    @Before
    public void prepareConfParam() {
        DbConfigurationParam param = new DbConfigurationParam("sth.source.1", "misc", "oneDB",
                null, DataTypeEnum.STRING, true, null);
        paramDao.insert(param);

        param = new DbConfigurationParam("ohf.source.2", "misc", "twoDB",
                null, DataTypeEnum.STRING, true, null);
        paramDao.insert(param);
    }

    @Test
    public void testGetParam() {
        assertThat(env.getProperty("sth.source.1"), is("oneDB"));
        assertThat(env.getProperty("ohf.source.2"), nullValue());
    }
}
