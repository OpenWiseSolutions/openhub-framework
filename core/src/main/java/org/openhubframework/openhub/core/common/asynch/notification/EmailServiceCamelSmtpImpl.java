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

import java.util.HashMap;
import java.util.Map;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.common.Strings;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;


/**
 * Mail (SMTP) implementation of {@link EmailService} interface that uses Apache Camel SMTP component.
 *
 * @author Petr Juza
 */
public class EmailServiceCamelSmtpImpl implements EmailService {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * Administrator email address.
     */
    @Value("${mail.admin}")
    private String recipients;

    /**
     * Email address FROM for sending emails.
     */
    @Value("${mail.from}")
    private String from;

    /**
     * SMTP server.
     */
    @Value("${mail.smtp.server}")
    private String smtp;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        sendFormattedEmail(recipients, subject, body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        Assert.hasText(subject, "the subject must not be empty");
        Assert.hasText(body, "the body must not be empty");

        if (StringUtils.isNotEmpty(recipients)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("To", recipients);
            map.put("From", from);
            map.put("Subject", subject);

            producerTemplate.sendBodyAndHeaders("smtp://" + smtp, Strings.fm(body, values), map);
        }
    }
}
