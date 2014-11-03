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
 * Exception indicates non-valid, illegal data.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class IllegalDataException extends ValidationIntegrationException {

    /**
     * Creates exception with the message and {@link InternalErrorEnum#E109} error code.
     *
     * @param msg the message
     */
    public IllegalDataException(String msg) {
        this(InternalErrorEnum.E109, msg);
    }

    /**
     * Creates exception with the message, {@link InternalErrorEnum#E109} error code and specified cause.
     *
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public IllegalDataException(String msg, Throwable cause) {
        this(InternalErrorEnum.E109, msg, cause);
    }


    /**
     * Creates validation exception with the specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public IllegalDataException(ErrorExtEnum error, String msg) {
        super(error, msg);
    }

    /**
     * Creates validation exception with the specified error code, message and cause.
     *
     * @param error the error code
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public IllegalDataException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable cause) {
        super(error, msg, cause);
    }
}
