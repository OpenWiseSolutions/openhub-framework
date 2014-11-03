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

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Common integration exception, parent exception for all exceptions thrown by this integration platform.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class IntegrationException extends RuntimeException {

    private final ErrorExtEnum error;

    /**
     * Creates new integration exception with specified error code.
     *
     * @param error the error code
     */
    public IntegrationException(ErrorExtEnum error) {
        this(error, null);
    }

    /**
     * Creates new integration exception with specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public IntegrationException(ErrorExtEnum error, @Nullable String msg) {
        this(error, msg, null);
    }

    /**
     * Creates new integration exception with specified error code, message and exception.
     *
     * @param error the error code
     * @param msg   the message
     * @param t     the cause exception
     */
    public IntegrationException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable t) {
        super(msg, t);

        Assert.notNull(error, "the error must not be null");

        this.error = error;
    }

    /**
     * Gets the error code.
     *
     * @return error code
     */
    public ErrorExtEnum getError() {
        return error;
    }

    @Override
    public String getMessage() {
        String superMsg = super.getMessage();
        if (StringUtils.hasText(superMsg)) {
            return superMsg;
        }

        return getError().getErrDesc();
    }
}
