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

package org.cleverbus.api.exception;

import javax.annotation.Nullable;


/**
 * Exception indicates that the expected data was not found.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class NoDataFoundException extends IntegrationException {

    public static final ErrorExtEnum ERROR_CODE = InternalErrorEnum.E107;

    /**
     * Creates exception with the message.
     *
     * @param msg the message
     */
    public NoDataFoundException(@Nullable String msg) {
        super(ERROR_CODE, msg);
    }

    /**
     * @param message exception description message
     * @param cause   the exception that caused this exception
     */
    public NoDataFoundException(@Nullable String message, @Nullable Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
