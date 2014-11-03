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

package org.cleverbus.core.common.asynch.repair;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Repairs hooked messages in the state {@link MsgStateEnum#PROCESSING}: these messages are after specified time changed
 * to {@link MsgStateEnum#PARTLY_FAILED} state with increasing failed count.
 * If failed count exceeds threshold then message is redirected to {@link AsynchConstants#URI_ERROR_FATAL}
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface RepairMessageService {

    public static final String BEAN = "repairMessageService";

    /**
     * Finds messages in state {@link MsgStateEnum#PROCESSING} and repairs them.
     */
    void repairProcessingMessages();
}
