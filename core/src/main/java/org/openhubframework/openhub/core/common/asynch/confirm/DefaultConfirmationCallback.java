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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.openhubframework.openhub.api.asynch.confirm.ConfirmationCallback;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.log.Log;

import org.springframework.util.Assert;


/**
 * Default implementation of {@link ConfirmationCallback} that only logs information and nothing more.
 *
 * @author Petr Juza
 */
public class DefaultConfirmationCallback implements ConfirmationCallback {

    private static final Set<MsgStateEnum> ALLOWED_STATES =
            Collections.unmodifiableSet(EnumSet.of(MsgStateEnum.OK, MsgStateEnum.FAILED));

    @Override
    public void confirm(Message msg) {
        Assert.notNull(msg, "Message must not be null");
        Assert.isTrue(ALLOWED_STATES.contains(msg.getState()), "Message must be in a final state to be confirmed");

        switch(msg.getState()) {
            case OK:
                Log.debug("Confirmation - the message " + msg.toHumanString() + " was successfully processed.");
                break;
            case FAILED:
                Log.debug("Confirmation - processing of the message " + msg.toHumanString() + " failed.");
                break;
        }
    }
}
