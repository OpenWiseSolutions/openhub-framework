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

import java.util.Set;

import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Contract for confirmation of processed asynchronous messages.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ExternalSystemConfirmation {

    /**
     * Confirms the message to the external system.
     * If the {@link Message#getState()}
     * is {@link MsgStateEnum#OK},
     * then confirms the message as OK,
     * if it's {@link MsgStateEnum#FAILED},
     * then confirms the message as Failed.
     *
     * @param msg the message to confirm
     */
    void confirm(Message msg);

    /**
     * Returns supported external systems.
     *
     * @return the supported external systems
     */
    Set<ExternalSystemExtEnum> getExternalSystems();

}
