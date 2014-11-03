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

package org.cleverbus.api.asynch.confirm;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Header;


/**
 * Callback contract that confirms successfully processed message to the source system.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ConfirmationCallback {

    public static final String BEAN = "confirmationCallback";

    /**
     * Confirms that the message was fully processed.
     * The message must have a final {@link Message#getState() state},
     * that is either {@link MsgStateEnum#OK} or {@link MsgStateEnum#FAILED}.
     *
     * @param msg the message
     */
    void confirm(@Header(AsynchConstants.MSG_HEADER) Message msg);

}
