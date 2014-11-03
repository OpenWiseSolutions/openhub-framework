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

package org.cleverbus.spi.extcall;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.LockFailureException;

/**
 * Contract for checking duplicate and obsolete calls.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ExternalCallService {

    /**
     * Prepares a new external call for the specified unique operation invocation.
     * Operation URI and operation key (such as object ID) together
     * uniquely identify such an operation invocation.
     * <p/>
     * The attempted external call might not be allowed.
     * This could happen if the external call is either duplicate or outdated.
     * To verify it's a new call, the provided {@link Message#getMsgTimestamp()}
     * will be compared with an existing known one (if any) for this invocation:
     * if the provided timestamp is newer, the call is considered new,
     * otherwise the call is considered outdated or duplicate.
     *
     * @param operationUri the operation URI that the external call is done via
     * @param operationKey the operation key that is to be unique for this operation URI
     *                     (object ID for edits, correlation ID for single-entity creation messages, etc.)
     * @param message      the message with the correct msg timestamp
     * @return an instance of {@link ExternalCall}, if the attempted call is new and is prepared to be made;
     * null if the call is duplicate or outdated and should NOT be made
     * @throws LockFailureException if the requested call is currently in the processing state
     *                              and therefore is temporarily not allowed to be repeated
     */
    ExternalCall prepare(String operationUri, String operationKey, Message message);

    /**
     * Marks the external call as a successfully executed call.
     *
     * @param externalCall the external call to be finalized.
     */
    void complete(ExternalCall externalCall);

    /**
     * Marks the external call as an unsuccessfully executed call.
     *
     * @param externalCall the external call to be finalized.
     */
    void failed(ExternalCall externalCall);

}
