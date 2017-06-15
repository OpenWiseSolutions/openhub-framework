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
 * @author Karel Kovarik
 */
public class MessageHelperTest {

    @Test
    public void test_allowedActions() {
        assertThat(allowedActions(createMessage(MsgStateEnum.NEW)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.OK)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.OK)).contains(MessageActionType.RESTART),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.IN_QUEUE)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.PROCESSING)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.PARTLY_FAILED)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.FAILED)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.FAILED)).contains(MessageActionType.RESTART),
                is(Boolean.TRUE));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.WAITING_FOR_RES)), hasSize(1));
        assertThat(allowedActions(createMessage(MsgStateEnum.POSTPONED)), hasSize(1));
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