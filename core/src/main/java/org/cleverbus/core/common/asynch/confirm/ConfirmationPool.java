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

package org.cleverbus.core.common.asynch.confirm;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.exception.LockFailureException;


/**
 * Pools confirmations (=external calls) in the {@link ExternalCallStateEnum#FAILED} state.
 * If there is this confirmation available then try to get and lock it for further processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ConfirmationPool {

    /**
     * Gets confirmation for next processing.
     *
     * @return external call or {@code null} if not available any confirmation
     * @throws LockFailureException if found a confirmation ({@link ExternalCall}), but failed to get a lock
     */
    @Nullable
    ExternalCall getNextConfirmation();
}
