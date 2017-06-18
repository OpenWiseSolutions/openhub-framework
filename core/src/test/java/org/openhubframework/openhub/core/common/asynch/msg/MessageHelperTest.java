/*
 * Copyright 2012-2017 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.msg;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.openhubframework.openhub.core.common.asynch.msg.MessageHelper.allowedActions;

import java.time.Instant;
import java.util.UUID;

import org.junit.Test;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageActionType;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;


/**
 * Test suite for {@link MessageHelper}.
 *
 * @author Karel Kovarik
 */
public class MessageHelperTest {

    @Test
    public void test_allowedActions() {
        assertThat(allowedActions(createMessage(MsgStateEnum.NEW)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.NEW)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.OK)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.OK)).contains(MessageActionType.RESTART),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.IN_QUEUE)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.IN_QUEUE)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.PROCESSING)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.PROCESSING)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.PARTLY_FAILED)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.FAILED)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.FAILED)).contains(MessageActionType.RESTART),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING_FOR_RES)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING_FOR_RES)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.POSTPONED)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.POSTPONED)).contains(MessageActionType.CANCEL),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.CANCEL)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.CANCEL)).contains(MessageActionType.RESTART),
                is(Boolean.TRUE));
    }

    protected Message createMessage(MsgStateEnum stateEnum) {
        Message msg = new Message();

        Instant now = Instant.now();
        msg.setState(stateEnum);
        msg.setMsgTimestamp(now);
        msg.setReceiveTimestamp(now);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(UUID.randomUUID().toString());

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("helloWorld");
        msg.setPayload("test-payload");
        msg.setLastUpdateTimestamp(now);

        return msg;
    }
}