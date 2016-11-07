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


/**
 * Common exception for all cases where a business-logic-related exception occurred in some external system,
 * (e.g., the specified ID does not exist), which would not disappear by itself,
 * therefore it does not make sense to retry a message after this exception occurred.
 */
public class BusinessException extends IntegrationException {

    public BusinessException(ErrorExtEnum error) {
        super(error);
    }

    public BusinessException(ErrorExtEnum error, @Nullable String msg) {
        super(error, msg);
    }

    public BusinessException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable t) {
        super(error, msg, t);
    }
}
