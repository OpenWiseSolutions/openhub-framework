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

package org.openhubframework.openhub.core.common.ws;

import java.util.Locale;

import javax.xml.namespace.QName;

import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.ThrottlingExceededException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;


/**
 * Simple, SOAP-specific {@link org.springframework.ws.server.EndpointExceptionResolver EndpointExceptionResolver}
 * implementation that stores the exception's message as the fault string.
 * <p/>
 * The fault code is always set to a Server (in SOAP 1.1) or Receiver (SOAP 1.2).
 * <p/>
 * Example of the generated SOAP fault response for SOAP 1.1:
 * <pre>
    &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    &lt;SOAP-ENV:Header/>
    &lt;SOAP-ENV:Body>
       &lt;SOAP-ENV:Fault>
          &lt;faultcode>SOAP-ENV:Server</faultcode>
          &lt;faultstring xml:lang="en">E102: the validation error</faultstring>
          &lt;detail>
            &lt;errorCode xmlns="http://openhubframework.org">E102&lt;/errorCode>
          &lt;/detail>
       &lt;/SOAP-ENV:Fault>
    &lt;/SOAP-ENV:Body>
    &lt;/SOAP-ENV:Envelope>
 * </pre>
 *
 * @author Petr Juza
 */
public class ErrorCodeAwareSoapExceptionResolver extends AbstractEndpointExceptionResolver {

    private Locale locale = Locale.ENGLISH;
    private boolean throttlingAsServerError = false;

    private static final QName ERR_CODE = new QName("http://openhubframework.org", "errorCode");

    /**
     * Returns the locale for the faultstring or reason of the SOAP Fault.
     * <p/>
     * Defaults to {@link Locale#ENGLISH}.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale for the faultstring or reason of the SOAP Fault.
     * <p/>
     * Defaults to {@link Locale#ENGLISH}.
     */
    public void setLocale(Locale locale) {
        Assert.notNull(locale, "locale must not be null");
        this.locale = locale;
    }

    @Override
    protected final boolean resolveExceptionInternal(MessageContext messageContext, Object endpoint, Exception ex) {
        Assert.isInstanceOf(SoapMessage.class, messageContext.getResponse(),
                "SimpleSoapExceptionResolver requires a SoapMessage");

        if (throttlingAsServerError && ex instanceof ThrottlingExceededException) {
            // no SOAP fault => server error
            return false;
        } else {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            String faultString = StringUtils.hasLength(ex.getMessage()) ? ex.getMessage() : ex.toString();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addServerOrReceiverFault(faultString, getLocale());
            customizeFault(messageContext, endpoint, ex, fault);

            return true;
        }
    }

    /**
     * Sets {@code false} if {@link ThrottlingExceededException} should be handled and SOAP fault generated
     * or {@code true} if {@link ThrottlingExceededException} should be treated as HTTP 500 error.
     * Default value is {@code false}.
     *
     * @param throttlingAsServerError sets behaviour for throttling exception
     */
    public void setThrottlingAsServerError(boolean throttlingAsServerError) {
        this.throttlingAsServerError = throttlingAsServerError;
    }

    /**
     * Adds error code element detail into SOAP fault.
     *
     * @param messageContext current message context
     * @param endpoint       the executed endpoint, or <code>null</code> if none chosen at the time of the exception
     * @param ex             the exception that got thrown during endpoint execution
     * @param fault          the SOAP fault to be customized.
     */
    protected void customizeFault(MessageContext messageContext, Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof IntegrationException) {
            SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(ERR_CODE).addText(((IntegrationException) ex).getError().getErrorCode());
        }
    }
}
