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

package org.cleverbus.core.common.exception;

import java.io.IOException;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.exception.ErrorExtEnum;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.ws.client.WebServiceIOException;


/**
 * Processor creates uniform error responses - processor translates Camel exceptions to our exception hierarchy.
 * Custom error codes from {@link IntegrationException}s are preferred to default error codes in exceptions.
 * But error codes defined via {@link AsynchConstants#EXCEPTION_ERROR_CODE} are preferred to exceptions at all.
 *
 * <p/>
 * Example of the generated SOAP fault response:
 * <pre>
    &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    &lt;SOAP-ENV:Header/>
    &lt;SOAP-ENV:Body>
       &lt;SOAP-ENV:Fault>
          &lt;faultcode>SOAP-ENV:Server</faultcode>
          &lt;faultstring xml:lang="en">E102: the validation error (PredicateValidationException: Validation failed
            for Predicate[header{header(chargingKey)} is not null]. Exchange[SpringWebserviceMessage[SaajSoapMessage
            {http://cleverbss.cleverlance.com/ws/SubscriberService-v1}getCounterDataRequest]] )</faultstring>
       &lt;/SOAP-ENV:Fault>
    &lt;/SOAP-ENV:Body>
    &lt;/SOAP-ENV:Envelope>
 * </pre>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ExceptionTranslator implements Processor {

    private static ExceptionTranslator instance;

    private ExceptionTranslator() {
    }

    /**
     * Gets instance of {@link ExceptionTranslator}.
     *
     * @return instance
     */
    public static ExceptionTranslator getInstance() {
        if (instance == null) {
            ExceptionTranslator.instance = new ExceptionTranslator();
        }

        return ExceptionTranslator.instance;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        // determine error code => prefer to use error code defined as exchange parameter
        ErrorExtEnum error;
        if (exchange.getProperty(AsynchConstants.EXCEPTION_ERROR_CODE) != null) {
            error = exchange.getProperty(AsynchConstants.EXCEPTION_ERROR_CODE, ErrorExtEnum.class);
        } else {
            error = getError(e);
        }


        // E100: unspecified error (java.net.UnknownHostException: billing)
        String errMsg = composeErrorMessage(error, e);

        exchange.setException(new IntegrationException(error, errMsg, e));
    }

    /**
     * Composes error message.
     *
     * @param error the error code
     * @param ex the exception
     * @return error message
     */
    public static String composeErrorMessage(ErrorExtEnum error, Exception ex) {
        Assert.notNull(error, "the error must not be null");
        Assert.notNull(ex, "the ex must not be null");

        // find root exception and get error code for it
        if (error == InternalErrorEnum.E100) {
            Throwable rootException = ExceptionUtils.getRootCause(ex);
            if (ex != rootException) {
                error = getError(rootException);
            }
        }

        return error.toString() + ": " + error.getErrDesc() + " (" + getRootExceptionMessage(ex) + ")";
    }

    /**
     * Returns recursively error messages from the whole exception hierarchy.
     *
     * @param ex the exception
     * @return exception message
     */
    private static String getMessagesInExceptionHierarchy(Throwable ex) {
        Assert.notNull(ex, "the ex must not be null");

        // use a util that can handle recursive cause structure:
        Throwable[] hierarchy = ExceptionUtils.getThrowables(ex);

        StringBuilder messages = new StringBuilder();
        for (Throwable throwable : hierarchy) {
            if (messages.length() > 0) {
                messages.append(" => ");
            }
            messages.append(throwable.getClass().getSimpleName())
                    .append(": ")
                    .append(throwable.getMessage());
        }

        return messages.toString();
    }

    /**
     * Returns error message for root exception.
     *
     * @param ex the exception
     * @return exception message
     */
    private static String getRootExceptionMessage(Throwable ex) {
        Assert.notNull(ex, "the ex must not be null");

        Throwable rootEx = ExceptionUtils.getRootCause(ex);
        if (rootEx == null) {
            rootEx = ex;
        }

        return rootEx.getClass().getSimpleName() + ": " + rootEx.getMessage();
    }

    /**
     * Gets {@link ErrorExtEnum} of specified exception.
     *
     * @param ex the exception
     * @return InternalErrorEnum
     */
    public static ErrorExtEnum getError(Throwable ex) {
        if (ex instanceof IntegrationException) {
            return ((IntegrationException) ex).getError();

        } else if (ex instanceof ValidationException) {
            return InternalErrorEnum.E102;

        } else if (ex instanceof IOException || ex instanceof WebServiceIOException) {
            return InternalErrorEnum.E103;

        } else if (ex instanceof CamelAuthorizationException || ex instanceof AccessDeniedException) {
            return InternalErrorEnum.E117;

        } else {
            return InternalErrorEnum.E100;
        }
    }
}
