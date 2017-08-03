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

package org.openhubframework.openhub.web.common;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.openhubframework.openhub.admin.web.common.rpc.FieldInputErrorRpc;
import org.openhubframework.openhub.admin.web.common.rpc.GeneralFaultRpc;
import org.openhubframework.openhub.admin.web.common.rpc.ValidationFaultRpc;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.validation.InputValidationException;
import org.openhubframework.openhub.api.exception.validation.ValidationException;


/**
 * Error handler for the following exceptions:
 * <ul>
 *     <li>{@link IntegrationException}
 *     <li>common Java exception (=raw error).
 * </ul>
 *
 * @author Petr Juza
 * @since 2.0
 */
@ControllerAdvice
public class ExceptionRestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionRestHandler.class);

    @Autowired
    private MessageSource messageSource;

    /**
     * Handle {@link ValidationException}.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationFaultRpc> onValidationException(HttpServletRequest req, ValidationException ex) {
        Assert.notNull(req, "req can not be null");
        Assert.notNull(ex, "exception can not be null");

        HttpStatus status = getStatusFromException(ex);
        ValidationFaultRpc faultRpc = new ValidationFaultRpc(status, ex);

        // add field errors
        if (ex instanceof InputValidationException) {
            BindingResult bindingResult = ((InputValidationException) ex).getBindingResult();

            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                faultRpc.addInputField(new FieldInputErrorRpc(fieldError.getObjectName(), fieldError.getField(),
                        fieldError.getRejectedValue(), fieldError.getCode(), fieldError.getDefaultMessage()));
            }
        }

        LOG.error("Validation exception posted to client side: " + faultRpc, ex);

        return new ResponseEntity<>(faultRpc, status);
    }

    /**
     * Resolve {@link HttpStatus} from exception.
     * Exception is taken from {@link ResponseStatus#value()}. If is not found, return {@link HttpStatus#BAD_REQUEST}.
     *
     * @param exception exception from which we get {@link HttpStatus}
     * @return http status from exception
     */
    protected HttpStatus getStatusFromException(Exception exception) {
        Assert.notNull(exception, "exception can not be null");

        HttpStatus result = null;
        //we try find status from annotation
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            result = responseStatus.value();
        }
        //if we can not found from annotation we use default status
        if (result == null) {
            result = HttpStatus.BAD_REQUEST;
        }
        return result;
    }

    /**
     * Handle {@link IntegrationException}.
     */
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<GeneralFaultRpc> onIntegrationException(HttpServletRequest req, IntegrationException ex) {
        Assert.notNull(req, "req can not be null");
        Assert.notNull(ex, "ex can not be null");

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        GeneralFaultRpc faultRpc = new GeneralFaultRpc(status, ex);

        LOG.error("Integration exception posted to client side: " + faultRpc, ex);

        return new ResponseEntity<>(faultRpc, status);
    }

    /**
     * Handle common Java exception (=raw error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralFaultRpc> onException(HttpServletRequest req, Exception ex) {
        Assert.notNull(req, "req can not be null");
        Assert.notNull(ex, "ex can not be null");

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        GeneralFaultRpc faultRpc = new GeneralFaultRpc(status, InternalErrorEnum.E100, ex);

        LOG.error("Common exception posted to client side: " + faultRpc, ex);

        return new ResponseEntity<>(faultRpc, status);
    }

    @Nullable
    private String getLocalizedMessage(String code, Object[] values) {
        try {
            return messageSource.getMessage(code, values, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            LOG.debug("there is no localized message for code: " + code);
            return null;
        }
    }
}
