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

package org.cleverbus.api.asynch.msg;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;


/**
 * Contract for splitting original (parent) message into smaller messages which will be processed
 * separately.
 * When all child messages are successfully processed (state is {@link MsgStateEnum#OK OK}
 * then also parent message is successfully processed.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MsgSplitter {

    /**
     * Splits specified message into smaller messages.
     *
     * @param parentMsg the parent message
     * @param body the body
     */
    @Handler
    void splitMessage(@Header(AsynchConstants.MSG_HEADER) Message parentMsg, @Body Object body);
}
