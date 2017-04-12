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

package org.openhubframework.openhub.api.exception.validation;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.common.Tools;


/**
 * Validation exception indicates error(s) in input data.
 * Exception encapsulates {@link BindingResult input errors}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InputValidationException extends ValidationException {

    private BindingResult bindingResult;

    /**
     * Creates exception with binding errors.
     *
     * @param bindingResult Binding errors
     */
    public InputValidationException(BindingResult bindingResult) {
        this(bindingResult, InternalErrorEnum.E109);
    }

    /**
     * Creates exception with binding errors and specific error code.
     *
     * @param bindingResult Binding errors
     * @param error The error code
     */
    public InputValidationException(BindingResult bindingResult, @Nullable ErrorExtEnum error) {
        super(error, null, null);

        Assert.notNull(bindingResult, "bindingResult must not be null");

        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public String getMessage() {
        return Tools.fm("Input validation error in object '{}'", bindingResult.getObjectName());
    }
}
