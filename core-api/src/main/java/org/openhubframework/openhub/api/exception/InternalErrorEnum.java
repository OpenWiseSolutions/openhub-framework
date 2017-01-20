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

package org.openhubframework.openhub.api.exception;

import org.springframework.util.Assert;


/**
 * Catalog of internal error codes.
 *
 * @author Petr Juza
 */
public enum InternalErrorEnum implements ErrorExtEnum {

    // ------------------------------------------------------------------------
    // internal errors
    // ------------------------------------------------------------------------

    /**
     * unspecified error
     */
    E100("unspecified error"),

    /**
     * the request message is not valid against to XSD schema
     */
    E101("the request message is not valid against to XSD schema"),

    /**
     * the validation error
     */
    E102("the validation error"),

    /**
     * I/O error during communication with target system
     */
    E103("I/O error during communication with target system"),

    /**
     * the request does not contain trace header
     */
    E104("the request does not contain trace header"),

    /**
     * the trace header does not contain all mandatory parameters
     */
    E105("the trace header does not contain all mandatory parameters"),

    /**
     * error during saving asynchronous message into storage
     */
    E106("error during saving asynchronous message into storage"),

    /**
     * no data found
     */
    E107("no data found"),

    /**
     * multiple results found
     */
    E108("multiple results found"),

    /**
     * the validation error - invalid data
     */
    E109("the validation error - invalid data"),

    /**
     * the validation error - there are no mandatory elements
     */
    E110("the validation error - there are no mandatory elements in XML request"),

    /**
     * the request message is not valid XML
     */
    E111("the request message is not valid XML"),

    /**
     * Locking exception - unsuccessful getting lock for the DB record.
     */
    E112("locking exception - unsuccessful getting lock for the DB record."),

    /**
     * There is no requested invoice in the repository.
     */
    E113("there is no requested invoice in the repository"),

    /**
     * Request is rejected because of throttling rules.
     */
    E114("request is rejected because of throttling rules"),

    /**
     * I/O error during saving file
     */
    E115("I/O error during saving file"),

    /**
     * Message stays repeatedly in PROCESSING state, probably because of some error.
     */
    E116("Message stays repeatedly in PROCESSING state, probably because of some error"),

    /**
     * Access is denied - there is no required authorization role.
     */
    E117("Access is denied - there is no required authorization role"),

    /**
     * Error occurred during extension loading, configuration error.
     */
    E118("Error occurred during extension loading, configuration error."),

    /**
     * Asynchronous request was rejected because ESB was stopping.
     */
    E119("Asynchronous request was rejected because ESB was stopping."),

    /**
     * the trace identifier does not contain allowed values
     */
    E120("the trace identifier does not contain allowed values"),

    /**
     * Message changed to POSTPONED state repeatedly and max. limit for starting processing was exceeded.
     */
    E121("message changed to POSTPONED state repeatedly and max. limit for starting processing was exceeded"),

    /**
     * the configuration error - error state of configuration item
     */
    E122("the configuration error - error state of configuration item");



    private String errDesc;

    /**
     * Creates new error code with specified description.
     *
     * @param errDesc the error description
     */
    private InternalErrorEnum(String errDesc) {
        Assert.hasText(errDesc, "the errDesc must not be empty");

        this.errDesc = errDesc;
    }

    @Override
    public String getErrorCode() {
        return name();
    }

    @Override
    public String getErrDesc() {
        return errDesc;
    }
}
