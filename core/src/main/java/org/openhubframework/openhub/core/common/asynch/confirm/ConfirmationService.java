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

package org.openhubframework.openhub.core.common.asynch.confirm;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.Message;

import org.apache.camel.Header;


/**
 * Service contract for manipulation with confirmations.
 *
 * @author Petr Juza
 */
public interface ConfirmationService {

    String BEAN = "confirmationService";

    /**
     * Inserts new failed confirmation.
     *
     * @param msg the message
     * @return creates {@link ExternalCall}
     */
    ExternalCall insertFailedConfirmation(@Header(AsynchConstants.MSG_HEADER) Message msg);

    /**
     * Marks confirmation as successfully completed.
     *
     * @param externalCall the external call
     */
    void confirmationComplete(ExternalCall externalCall);

    /**
     * Marks confirmation as successfully completed.
     *
     * @param externalCall the external call
     */
    void confirmationFailed(ExternalCall externalCall);

}
