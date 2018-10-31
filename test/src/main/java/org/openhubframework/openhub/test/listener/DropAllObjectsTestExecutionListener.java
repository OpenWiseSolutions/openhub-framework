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

package org.openhubframework.openhub.test.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Test execution listener to drop all objects in database (jdbcTemplate).
 *
 * @author Karel Kovarik
 * @since 2.0.1
 * @see org.springframework.test.context.TestExecutionListener
 */
public class DropAllObjectsTestExecutionListener extends AbstractTestExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(DropAllObjectsTestExecutionListener.class);

    private static final String QUERY = "DROP ALL OBJECTS";

    protected String sqlQuery() {
        return QUERY;
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        final JdbcTemplate jdbcTemplate;
        try {
            jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);
        } catch (BeansException e ) {
            // fail silently with logging only
            LOG.warn("Could not get JdbcTemplate bean from spring context:", e);
            return;
        }

        jdbcTemplate.execute(sqlQuery());
    }
}
