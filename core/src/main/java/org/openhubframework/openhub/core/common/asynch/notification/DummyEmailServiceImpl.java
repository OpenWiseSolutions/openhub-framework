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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.openhubframework.openhub.api.common.EmailService;
import org.openhubframework.openhub.common.Strings;


/**
 * Dummy implementation of {@link EmailService} interface, mainly for testing purposes.
 *
 * @author Petr Juza
 */
public class DummyEmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(DummyEmailServiceImpl.class);

    /**
     * Administrator email address.
     */
    @Value("${ohf.mail.admin}")
    private String recipients;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        LOG.debug("Sending email:"
                + "\nrecipients: " + recipients
                + "\nsubject: " + subject
                + "\nbody: " + body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        LOG.debug("Sending email:"
                + "\nrecipients: " + recipients
                + "\nsubject: " + subject
                + "\nbody: " + Strings.fm(body, values));
    }
}
