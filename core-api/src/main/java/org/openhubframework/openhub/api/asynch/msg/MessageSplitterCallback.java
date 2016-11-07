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

package org.openhubframework.openhub.api.asynch.msg;

import java.util.List;

import org.openhubframework.openhub.api.entity.Message;


/**
 * Callback contract for getting child messages.
 *
 * @author Petr Juza
 */
public interface MessageSplitterCallback {

    /**
     * Gets child messages for next processing.
     * Order of child messages in the list determines order of first synchronous processing.
     *
     * @param parentMsg the parent message
     * @param body the exchange body
     * @return list of child messages
     */
    List<ChildMessage> getChildMessages(Message parentMsg, Object body);
}
