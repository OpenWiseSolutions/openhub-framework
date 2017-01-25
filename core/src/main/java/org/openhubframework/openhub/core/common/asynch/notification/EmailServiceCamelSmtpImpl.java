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

package org.openhubframework.openhub.core.common.asynch.notification;

import static org.openhubframework.openhub.api.configuration.CoreProps.MAIL_ADMIN;
import static org.openhubframework.openhub.api.configuration.CoreProps.MAIL_FROM;
import static org.openhubframework.openhub.api.configuration.CoreProps.MAIL_SMTP_SERVER;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.common.Tools;


/**
 * Mail (SMTP) implementation of {@link EmailService} interface that uses Apache Camel SMTP component.
 *
 * @author Petr Juza
 */
@Service(EmailService.BEAN)
public class EmailServiceCamelSmtpImpl implements EmailService {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * Administrator email address.
     */
    @ConfigurableValue(key = MAIL_ADMIN)
    private ConfigurationItem<String> recipients;

    /**
     * Email address FROM for sending emails.
     */
    @ConfigurableValue(key = MAIL_FROM)
    private ConfigurationItem<String> from;

    /**
     * SMTP server.
     */
    @ConfigurableValue(key = MAIL_SMTP_SERVER)
    private ConfigurationItem<String> smtp;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        sendFormattedEmail(recipients.getValue(), subject, body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        Assert.hasText(subject, "the subject must not be empty");
        Assert.hasText(body, "the body must not be empty");

        if (StringUtils.isNotEmpty(recipients)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("To", recipients);
            map.put("From", from.getValue());
            map.put("Subject", subject);

            producerTemplate.sendBodyAndHeaders("smtp://" + smtp.getValue(), Tools.fm(body, values), map);
        }
    }
}
