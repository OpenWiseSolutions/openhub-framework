/*
 * Copyright 2020 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.notification;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.core.configuration.FixedConfigurationItem;


/**
 * Test suite for {@link EmailServiceCamelSmtpImpl}.
 *
 * @author Michal Sabol
 */
@ContextConfiguration(classes = {
        EmailServiceCamelSmtpImplTest.TestContext.class
})
public class EmailServiceCamelSmtpImplTest extends AbstractCoreDbTest {

    private static final String TEST_SUBJECT = "Test notification email";

    private static final String TEST_BODY = "Test notification email with body";

    @Autowired
    @Qualifier("testEmailService")
    private EmailService emailService;

    @MockBean
    private ProducerTemplate producerTemplateMock;

    @Before
    public void prepareData() {
        setPrivateField(emailService, "recipients", new FixedConfigurationItem<>("admin@oh.or"));
        setPrivateField(emailService, "smtp", new FixedConfigurationItem<>("smtp.server"));
    }

    @Test
    public void testSendEmailToAdmins_enabled() {
        setPrivateField(emailService, "enabled", new FixedConfigurationItem<>(Boolean.TRUE));

        emailService.sendEmailToAdmins(TEST_SUBJECT, TEST_BODY);

        verify(producerTemplateMock, times(1)).sendBodyAndHeaders(eq("smtp://smtp.server"), eq(TEST_BODY), any());
    }

    @Test
    public void testSendEmailToAdmins_disabled() {
        setPrivateField(emailService, "enabled", new FixedConfigurationItem<>(Boolean.FALSE));

        emailService.sendEmailToAdmins(TEST_SUBJECT, TEST_BODY);

        verify(producerTemplateMock, never()).sendBodyAndHeaders(eq("smtp://smtp.server"), eq(TEST_BODY), any());
    }

    @Test
    public void testSendFormattedEmail_ok() {
        final String recipients = "admin@oh.or";
        final String from = "openhub@openwise.cz";
        setPrivateField(emailService, "from", new FixedConfigurationItem<>(from));

        emailService.sendFormattedEmail(recipients, TEST_SUBJECT, TEST_BODY);

        final Map<String, Object> expected = new HashMap<>();
        expected.put("To", recipients);
        expected.put("From", from);
        expected.put("Subject", TEST_SUBJECT);

        verify(producerTemplateMock, times(1)).sendBodyAndHeaders(eq("smtp://smtp.server"), eq(TEST_BODY), eq(expected));
    }

    @Test
    public void testSendFormattedEmail_noRecipients() {
        emailService.sendFormattedEmail(null, TEST_SUBJECT, TEST_BODY);

        verify(producerTemplateMock, never()).sendBodyAndHeaders(eq("smtp://smtp.server"), eq(TEST_BODY), any());
    }

    public static class TestContext {

        @Bean("testEmailService")
        public EmailService getTestEmailService() {
            return new EmailServiceCamelSmtpImpl();
        }
    }
}
