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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

import org.openhubframework.openhub.api.exception.ValidationIntegrationException;


/**
 * Validation (business) error on the server.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class ValidationFaultRpc extends GeneralFaultRpc {

    private final List<FieldInputErrorRpc> inputFields = new ArrayList<>();

    public ValidationFaultRpc(ValidationIntegrationException ex) {
        super(HttpStatus.BAD_REQUEST, ex);
    }

    public ValidationFaultRpc(HttpStatus status, ValidationIntegrationException ex) {
        super(status, ex);
    }

    public boolean addInputField(FieldInputErrorRpc... field) {
        return inputFields.addAll(Arrays.asList(field));
    }

    public boolean addInputFields(List<FieldInputErrorRpc> fields) {
        return inputFields.addAll(fields);
    }

    public List<FieldInputErrorRpc> getInputFields() {
        return Collections.unmodifiableList(inputFields);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("inputFields", inputFields)
                .toString();
    }
}
