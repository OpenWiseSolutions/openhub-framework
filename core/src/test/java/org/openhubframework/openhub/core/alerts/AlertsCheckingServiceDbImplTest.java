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

package org.openhubframework.openhub.core.alerts;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.openhubframework.openhub.spi.alerts.AlertInfo;
import org.openhubframework.openhub.spi.alerts.AlertListener;
import org.openhubframework.openhub.spi.alerts.AlertsConfiguration;
import org.openhubframework.openhub.test.AbstractTest;

import org.junit.Test;


/**
 * Test suite for {@link AlertsCheckingServiceDbImpl}.
 *
 * @author Petr Juza
 * @since 0.4
 */
public class AlertsCheckingServiceDbImplTest extends AbstractTest {

    @Test
    public void testCheckAlerts() {
        // prepare data
        AlertsCheckingServiceDbImpl checkingService = new AlertsCheckingServiceDbImpl();

        AlertInfo alert = new AlertInfo("ID", 1, "sql", true, null, null);

        AlertsConfiguration alertsConfig = mock(AlertsConfiguration.class);
        when(alertsConfig.getAlerts(true)).thenReturn(Arrays.asList(alert));

        AlertListener listener = mock(AlertListener.class);
        when(listener.supports(alert)).thenReturn(true);

        AlertsDao alertsDao = mock(AlertsDao.class);
        when(alertsDao.runQuery(anyString())).thenReturn(2L);

        setPrivateField(checkingService, "alertsConfig", alertsConfig);
        setPrivateField(checkingService, "listeners", Arrays.asList(listener));
        setPrivateField(checkingService, "alertsDao", alertsDao);

        // action
        checkingService.checkAlerts();

        // verify
        verify(alertsDao).runQuery(alert.getSql());
        verify(listener).onAlert(alert, 2L);
    }
}
