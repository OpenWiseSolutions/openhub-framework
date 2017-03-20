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

package org.openhubframework.openhub.admin.web.common.rpc;

import javax.annotation.Nullable;

import org.springframework.util.Assert;


/**
 * Form input validation error.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class FieldInputErrorRpc {

    private final String field;

    private final String code;

    private final String message;

    public FieldInputErrorRpc(String field, String code, String message) {
        Assert.hasText(field, "field must not be empty");
        Assert.hasText(code, "code must not be empty");
        Assert.hasText(message, "message must not be empty");

        this.field = field;
        this.code = code;
        this.message = message;
    }

    /**
     * Gets field: 'category.key' (string) - field identification, e.g. entity.field.
     */
    public String getField() {
        return field;
    }

    /**
     * Gets code: 'valid.notEmpty' (string) - error code to be able identify input error.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets message: 'Key is not unique. Choose another key.' (string) - a description of the error in default language.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets messageI18n: 'Key is not unique. Choose another key.' (string,) - a description of the error in localized language.
     */
    @Nullable
    public String getMessageI18n() {
        return getMessage();
    }
}
