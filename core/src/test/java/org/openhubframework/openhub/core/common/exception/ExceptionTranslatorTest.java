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

package org.openhubframework.openhub.core.common.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.validation.IllegalDataException;
import org.openhubframework.openhub.test.data.ErrorTestEnum;


/**
 * Test suite for {@link ExceptionTranslator}.
 *
 * @author Petr Juza
 */
public class ExceptionTranslatorTest {

    @Test
    public void testComposeErrorMessage() {
        Exception ex = new IllegalDataException("wrong data", new IllegalArgumentException("wrong number format"));

        String errMsg = ExceptionTranslator.composeErrorMessage(ErrorTestEnum.E200, ex);

        assertThat(errMsg, is("E200: error in billing (IllegalArgumentException: wrong number format)"));

        // unspecified error code
        errMsg = ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E100, ex);

        assertThat(errMsg, is("E100: unspecified error (IllegalArgumentException: wrong number format)"));
    }
}
