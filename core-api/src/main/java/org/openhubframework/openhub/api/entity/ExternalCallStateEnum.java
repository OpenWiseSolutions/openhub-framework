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

package org.openhubframework.openhub.api.entity;

/**
 * Enumeration of possible processing states of {@link ExternalCall}.
 *
 * @author Petr Juza
 */
public enum ExternalCallStateEnum {

    /**
     * External call is just processing.
     */
    PROCESSING,

    /**
     * External call is successfully done.
     */
    OK,

    /**
     * External call failed, try it next time.
     */
    FAILED,

    /**
     * All confirmation tries failed, no further processing.
     */
    FAILED_END
}
