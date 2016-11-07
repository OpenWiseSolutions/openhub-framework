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

/**
 * Unsuccessful getting lock for the record from DB.
 *
 * @author Petr Juza
 */
public class LockFailureException extends IntegrationException {

    public LockFailureException(String message) {
        super(InternalErrorEnum.E112, message);
    }

    public LockFailureException(String message, Throwable cause) {
        super(InternalErrorEnum.E112, message, cause);
    }
}
