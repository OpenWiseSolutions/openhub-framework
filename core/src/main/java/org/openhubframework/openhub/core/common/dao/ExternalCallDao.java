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

package org.openhubframework.openhub.core.common.dao;

import java.time.Duration;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.PersistenceException;

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;


/**
 * DAO for {@link ExternalCall} entity.
 *
 * @author Petr Juza
 */
public interface ExternalCallDao {

    /**
     * Inserts new call.
     *
     * @param externalCall the external call
     */
    void insert(ExternalCall externalCall);

    /**
     * Updates call.
     *
     * @param externalCall the external call
     */
    void update(ExternalCall externalCall);

    /**
     * Finds an existing external call for the specified operation and entityId.
     *
     * @param operationName the operation name (uri) to find the call for
     * @param entityId      the entity id (operation key) to find the call for
     * @return the found external call or null, if no such call is registered
     */
    @Nullable
    ExternalCall getExternalCall(String operationName, String entityId);

    /**
     * Updates the specified attached external call entity to
     * start processing a new external call (set state to PROCESSING).
     *
     * @param extCall the external call that is attached to local EntityManager
     *                (e.g., acquired via {@link #getExternalCall(String, String)} in the same transaction)
     * @throws PersistenceException e.g., if the lock fails
     */
    void lockExternalCall(ExternalCall extCall) throws PersistenceException;

    /**
     * Finds ONE confirmation in state {@link ExternalCallStateEnum#FAILED}.
     *
     * @param interval Interval (in seconds) between two tries of failed confirmations.
     * @return external call or {@code null} if there is no any confirmation
     */
    @Nullable
    ExternalCall findConfirmation(Duration interval);

    /**
     * Updates confirmation (set state to PROCESSING) - gets lock for this confirmation.
     *
     * @param extCall the external call
     * @return the locked external call, if lock is successful; throws an exception, if the lock failed
     * @throws PersistenceException e.g., if the lock fails
     */
    ExternalCall lockConfirmation(ExternalCall extCall);

    /**
     * Finds "processing" external calls in specified interval.
     *
     * @param interval the interval
     * @return list of calls
     */
    List<ExternalCall> findProcessingExternalCalls(Duration interval);
}
