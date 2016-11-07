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

import javax.annotation.Nullable;

import org.openhubframework.openhub.api.entity.MsgStateEnum;


/**
 * Exception indicates error during validation.
 * <p/>
 * If there is validation error than there is no next try, message gets to {@link MsgStateEnum#FAILED FAILED state}.
 *
 * @author Petr Juza
 */
public class ValidationIntegrationException extends IntegrationException {

    /**
     * Creates validation exception with the message and {@link InternalErrorEnum#E102} error code.
     *
     * @param msg the message
     */
    public ValidationIntegrationException(String msg) {
        super(InternalErrorEnum.E102, msg);
    }

    /**
     * Creates validation exception with the specified error code.
     *
     * @param error the error code
     */
    public ValidationIntegrationException(ErrorExtEnum error) {
        super(error);
    }

    /**
     * Creates validation exception with the specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public ValidationIntegrationException(ErrorExtEnum error, String msg) {
        super(error, msg);
    }

    /**
     * Creates validation exception with the specified error code, message and cause.
     *
     * @param error the error code
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public ValidationIntegrationException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable cause) {
        super(error, msg, cause);
    }
}
