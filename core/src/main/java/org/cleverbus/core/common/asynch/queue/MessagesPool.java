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

package org.cleverbus.core.common.asynch.queue;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Pools messages in the {@link MsgStateEnum#PARTLY_FAILED} and {@link MsgStateEnum#POSTPONED} state.
 * If there is this message available then try to get and lock it for further processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MessagesPool {

    /**
     * Gets message for next processing.
     *
     * @return message or {@code null} if not available any message
     */
    @Nullable
    Message getNextMessage();

}
