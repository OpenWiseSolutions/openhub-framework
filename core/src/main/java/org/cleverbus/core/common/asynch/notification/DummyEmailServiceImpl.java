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

package org.cleverbus.core.common.asynch.notification;

import org.cleverbus.api.common.EmailService;
import org.cleverbus.common.Strings;
import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Value;


/**
 * Dummy implementation of {@link EmailService} interface, mainly for testing purposes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DummyEmailServiceImpl implements EmailService {

    /**
     * Administrator email address.
     */
    @Value("${mail.admin}")
    private String recipients;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        Log.debug("Sending email:"
            + "\nrecipients: " + recipients
            + "\nsubject: " + subject
            + "\nbody: " + body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        Log.debug("Sending email:"
                + "\nrecipients: " + recipients
                + "\nsubject: " + subject
                + "\nbody: " + Strings.fm(body, values));
    }
}
