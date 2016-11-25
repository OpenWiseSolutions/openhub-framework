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

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.spi.alerts.AlertInfo;


/**
 * Test suite for {@link EmailAlertListenerSupport}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class EmailAlertListenerSupportTest extends AbstractCoreTest {

    @Autowired
    private EmailAlertListenerSupport alertListener;

    @MockBean
    private EmailService emailServiceMock;

    @Test
    public void testOnAlert() {
        // prepare data
        String subject = "Alert: notification about WAITING messages";
        String body = "There are %d message(s) in WAITING_FOR_RESPONSE state for more then %d seconds.";
        AlertInfo alert = new AlertInfo("ID", 1, "sql", true, subject, body);
        int actualCount = 3;

        // action
        alertListener.onAlert(alert, actualCount);

        // verify
        verify(emailServiceMock, only()).sendEmailToAdmins(subject, alertListener.formatBody(alert, actualCount));
    }
}
