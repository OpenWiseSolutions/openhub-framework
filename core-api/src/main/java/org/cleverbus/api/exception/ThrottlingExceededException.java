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

/**
 * Exception indicates that throttling rules were exceeded.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingExceededException extends IntegrationException {

    /**
     * Creates throttling exception with the message and {@link InternalErrorEnum#E114} error code.
     *
     * @param msg the message
     */
    public ThrottlingExceededException(String msg) {
        super(InternalErrorEnum.E114, msg);
    }
}
