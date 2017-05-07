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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;


/**
 * Form input validation error.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class FieldInputErrorRpc {

    private final String objectName;

    private final String field;

    private final String code;

    private final String message;

    private final Object rejectedValue;

    public FieldInputErrorRpc(String objectName, String field, String code, String message) {
        this(objectName, field, null, code, message);
    }

    public FieldInputErrorRpc(String objectName, String field, @Nullable Object rejectedValue, String code, String message) {
        Assert.hasText(objectName, "objectName must not be empty");
        Assert.hasText(field, "field must not be empty");
        Assert.hasText(code, "code must not be empty");
        Assert.hasText(message, "message must not be empty");

        this.objectName = objectName;
        this.field = field;
        this.code = code;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    /**
     * Gets + objectName: 'category' - object name, e.g. category.
     */
    public String getObjectName() {
        return objectName;
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

    /**
     * Gets rejectedValue: '' (string,) - rejected field value.
     */
    @Nullable
    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("objectName", objectName)
                .append("field", field)
                .append("rejectedValue", rejectedValue)
                .append("code", code)
                .append("message", message)
                .toString();
    }
}
