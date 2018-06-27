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

package org.openhubframework.openhub.core.common.asynch.msg;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.core.common.dao.MessageDao;
import org.openhubframework.openhub.core.common.exception.ExceptionTranslator;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.spi.node.NodeService;


/**
 * Implementation of the {@link MessageService} interface.
 *
 * @author Petr Juza
 */
@Service(MessageService.BEAN)
public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final TransactionTemplate transactionTemplate;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    public MessageServiceImpl(PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Transactional
    @Override
    public void insertMessage(Message message) {
        Assert.notNull(message, "the message must not be null");
        Assert.state(message.getState() == MsgStateEnum.NEW, "new message can be in NEW state only");

        message.setLastUpdateTimestamp(Instant.now());

        messageDao.insert(message);

        LOG.debug("Inserted new message " + message.toHumanString());
    }

    @Transactional
    @Override
    public void insertMessages(Collection<Message> messages) {
        Assert.notNull(messages, "the messages must not be null");

        for (Message msg : messages) {
            insertMessage(msg);
        }
    }

    @Transactional
    @Override
    public void setStateOk(Message msg, Map<String, Object> props) {
        Assert.notNull(msg, "the msg must not be null");

        msg.setState(MsgStateEnum.OK);
        msg.setLastUpdateTimestamp(Instant.now());

        if (msg.isParentMessage()) {
            //set business error from child messages
            List<Message> childMessages = messageDao.findChildMessagesForParent(msg);
            msg.setBusinessError(getBusinessErrorsFromChildMessages(childMessages));
        } else {
            if (!MapUtils.isEmpty(props)) {
                // move new business errors to message:
                MessageHelper.updateBusinessErrors(msg, props);
            }
        }

        messageDao.update(msg);

        if (msg.isParentMessage()) {
            LOG.debug("State of the parent message " + msg.toHumanString() + " was changed to "
                    + MsgStateEnum.OK);
        } else {
            LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.OK);
        }

        // check parent message with HARD binding - if any
        if (msg.existHardParent()) {
            List<Message> childMessages = messageDao.findChildMessages(msg);

            // are all child messages processed?
            boolean finishedOK = true;
            for (Message childMsg : childMessages) {
                //note: input message doesn't have to be in valid state in DB if Hibernate is used ...
                if (childMsg.getState() != MsgStateEnum.OK && !childMsg.equals(msg)) {
                    finishedOK = false;
                    break;
                }
            }

            if (finishedOK) {
                // mark parent message as successfully processed only if parent message is in waiting state
                Message parentMsg = messageDao.getMessage(msg.getParentMsgId());
                if (parentMsg.getState().equals(MsgStateEnum.WAITING)) {
                    setStateOk(parentMsg, null);
                }
            }
        }
    }

    @Transactional
    @Override
    public void setStateProcessing(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.isTrue(msg.getState() == MsgStateEnum.WAITING_FOR_RES,
                "the message must be in WAITING_FOR_RES state, but state is " + msg.getState());

        msg.setState(MsgStateEnum.PROCESSING);
        Instant currDate = Instant.now();
        msg.setStartProcessTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setNodeId(nodeService.getActualNode().getNodeId());

        messageDao.update(msg);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.PROCESSING);
    }

    private String getBusinessErrorsFromChildMessages(List<Message> messages) {
        List<String> errorList = new ArrayList<String>();

        for (Message msg : messages) {
            errorList.addAll(msg.getBusinessErrorList());
        }

        return StringUtils.join(errorList, Message.ERR_DESC_SEPARATOR);
    }

    @Transactional
    @Override
    public void setStateWaiting(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.isTrue(msg.isParentMessage(), "the message must be parent");

        // check current state (it's possible that parent message has been already finished)
        Message currMsg = messageDao.getMessage(msg.getMsgId());

        if (currMsg.getState() == MsgStateEnum.PROCESSING) {
            msg.setState(MsgStateEnum.WAITING);
            msg.setLastUpdateTimestamp(Instant.now());

            messageDao.update(msg);

            LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.WAITING);
        }
    }

    @Transactional
    @Override
    public void setStateWaitingForResponse(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        // note: we can send more VF messages in one asynch. message but one by one
        Assert.isTrue(msg.getState() == MsgStateEnum.PROCESSING,
                "the message must be in PROCESSING state, but state is " + msg.getState());

        if (msg.getState() != MsgStateEnum.WAITING_FOR_RES) {
            msg.setState(MsgStateEnum.WAITING_FOR_RES);
            msg.setLastUpdateTimestamp(Instant.now());

            messageDao.update(msg);

            LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.WAITING_FOR_RES);
        } else {
            LOG.debug("Message " + msg.toHumanString() + " was already in " + MsgStateEnum.WAITING_FOR_RES + " state.");
        }
    }

    @Transactional
    @Override
    public void setStatePartlyFailedWithoutError(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.isTrue(!msg.isParentMessage(), "the message must not be parent");

        msg.setState(MsgStateEnum.PARTLY_FAILED);
        msg.setLastUpdateTimestamp(Instant.now());

        messageDao.update(msg);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.PARTLY_FAILED
                + ", but WITHOUT increasing error counter");
    }

    @Transactional
    @Override
    public void setStatePartlyFailed(Message msg, Exception ex, ErrorExtEnum errCode, String customData,
            Map<String, Object> props) {

        Assert.notNull(msg, "the msg must not be null");

        msg.setState(MsgStateEnum.PARTLY_FAILED);
        updateErrorMessage(msg, ex, errCode, customData, props);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to "
                + MsgStateEnum.PARTLY_FAILED + " (failed count = " + msg.getFailedCount() + ")");
    }

    @Transactional
    @Override
    public void setStateFailed(Message msg, Exception ex, ErrorExtEnum errCode, String customData,
            Map<String, Object> props) {

        Assert.notNull(msg, "the msg must not be null");

        msg.setState(MsgStateEnum.FAILED);
        updateErrorMessage(msg, ex, errCode, customData, props);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to "
                + MsgStateEnum.FAILED + " (failed count = " + msg.getFailedCount() + ")");

        // check parent message with HARD binding - if any
        if (msg.existHardParent()) {
            setParentMsgFailed(msg);
        }
    }

    private void setParentMsgFailed(Message msg) {
        Assert.notNull(msg, "msg must not be null");
        Assert.isTrue(msg.existHardParent(), "only parent message with HARD binding can be affected");

        // mark parent message as failed too
        Message parentMsg = messageDao.getMessage(msg.getParentMsgId());

        parentMsg.setState(MsgStateEnum.FAILED);
        parentMsg.setLastUpdateTimestamp(Instant.now());

        // set information about error from child message
        parentMsg.setFailedErrorCode(msg.getFailedErrorCode());
        parentMsg.setFailedDesc(msg.getFailedDesc());
        parentMsg.setFailedCount(msg.getFailedCount());

        messageDao.update(parentMsg);

        LOG.debug("State of the parent message " + parentMsg.toHumanString() + " was changed to " + MsgStateEnum.FAILED);

        //if message has parent another parent message then set parent message into FAILED
        if (parentMsg.existHardParent()) {
            setParentMsgFailed(parentMsg);
        }
    }

    @Transactional
    @Override
    public void setStateFailed(Message msg, ErrorExtEnum errCode, String errDesc) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.notNull(errCode, "the errCode must not be null");
        Assert.hasText(errDesc, "the errDesc must not be empty");

        msg.setState(MsgStateEnum.FAILED);
        msg.setLastUpdateTimestamp(Instant.now());
        msg.setFailedErrorCode(errCode);
        msg.setFailedCount(msg.getFailedCount() + 1);
        msg.setFailedDesc(errDesc);

        messageDao.update(msg);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to "
                + MsgStateEnum.FAILED + " (failed count = " + msg.getFailedCount() + ")");

        // check parent message with HARD binding - if any
        if (msg.existHardParent()) {
            setParentMsgFailed(msg);
        }
    }

    @Override
    public boolean setStateInQueueForLock(final Message message) {
        Assert.notNull(message, "message must not be null");
        Assert.state(message.getState().equals(MsgStateEnum.NEW)
                        || message.getState().equals(MsgStateEnum.POSTPONED)
                        || message.getState().equals(MsgStateEnum.PARTLY_FAILED)
                        || message.getState().equals(MsgStateEnum.WAITING_FOR_RES),
                "the message must be in NEW, POSTPONED, PARTLY_FAILED or WAITING_FOR_RES state, but state is "
                        + message.getState());

        boolean result;
        final Node actualNode = nodeService.getActualNode();
        try {
            result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(final TransactionStatus transactionStatus) {
                    return messageDao.updateMessageInQueueUnderLock(message, actualNode);
                }
            });
        } catch (DataAccessException ex) {
            LOG.warn("Caught DataAccessException during setStateInQueueForLock :", ex);
            result = false;
        }

        if (result) {
            LOG.debug("Successfully locked message: {} for changed state: {} in node: {}", message.toHumanString(),
                    MsgStateEnum.IN_QUEUE, actualNode.toHumanString());
        } else {
            LOG.debug("Failed to lock message: {} for change state: {} in node: {}", message.getMsgId(),
                    MsgStateEnum.IN_QUEUE, actualNode.toHumanString());
        }
        return result;
    }

    @Override
    public boolean setStateProcessingForLock(final Message message) {
        Assert.notNull(message, "message must not be null");
        Assert.state(message.getState().equals(MsgStateEnum.IN_QUEUE),
                "the message must be in IN_QUEUE state, but state is " + message.getState());

        boolean result;
        final Node actualNode = nodeService.getActualNode();
        try {
            result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(final TransactionStatus transactionStatus) {
                    return messageDao.updateMessageProcessingUnderLock(message, actualNode);
                }
            });
        } catch (DataAccessException ex) {
            LOG.warn("Caught DataAccessException during setStateProcessingForLock :", ex);
            result = false;
        }

        if (result) {
            LOG.debug("Successfully locked message: {} for changed state: {} in node: {}", message.toHumanString(),
                    MsgStateEnum.PROCESSING, actualNode.toHumanString());
        } else {
            LOG.debug("Failed to lock message: {} for change state: {} in node: {}", message.getMsgId(),
                    MsgStateEnum.PROCESSING, actualNode.toHumanString());
        }
        return result;
    }

    @Nullable
    @Override
    public Message findMessageById(Long msgId) {
        return messageDao.findMessage(msgId);
    }

    @Nullable
    @Override
    public Message findEagerMessageById(Long msgId) {
        return messageDao.findEagerMessage(msgId);
    }

    private void updateErrorMessage(Message msg, Exception ex, @Nullable ErrorExtEnum errCode,
            @Nullable String customData, Map<String, Object> props) {

        Assert.notNull(msg, "the msg must not be null");
        Assert.notNull(ex, "the ex must not be null");
        Assert.notNull(props, "the props must not be null");

        ErrorExtEnum tmpErrCode = errCode;
        if (tmpErrCode == null) {
            tmpErrCode = ExceptionTranslator.getError(ex);
        }

        msg.setLastUpdateTimestamp(Instant.now());
        msg.setFailedCount(msg.getFailedCount() + 1);
        msg.setFailedErrorCode(tmpErrCode);

        // save error description also with stack trace
        String errDesc = ExceptionTranslator.composeErrorMessage(tmpErrCode, ex);
        errDesc += "\n";
        errDesc += ExceptionUtils.getStackTrace(ex);

        msg.setFailedDesc(errDesc);

        if (customData != null) {
            msg.setCustomData(customData);
        }

        // move new business errors to message:
        MessageHelper.updateBusinessErrors(msg, props);

        messageDao.update(msg);
    }

    @Override
    public List<Message> findMessagesByFilter(final MessageFilter messageFilter, long limit) {
        Assert.notNull(messageFilter, "the messageFilter must not be null");

        return messageDao.findMessagesByFilter(messageFilter, limit);
    }

    @Override
    public int getCountMessages(MsgStateEnum state, Duration interval) {
        Assert.notNull(state, "the state must not be null");

        return messageDao.getCountMessages(state, interval);
    }

    @Override
    public int getCountProcessingMessagesForFunnel(String funnelValue, Duration idleInterval, String funnelCompId) {
        Assert.hasText(funnelValue, "the funnelValue must not be empty");
        Assert.notNull(idleInterval, "the idleInterval must not be null");
        Assert.hasText(funnelCompId, "the funnelCompId must not be empty");

        return messageDao.getCountProcessingMessagesForFunnel(funnelValue, idleInterval, funnelCompId);
    }

    @Override
    public List<Message> getMessagesForGuaranteedOrderForRoute(String funnelValue, boolean excludeFailedState) {
        return messageDao.getMessagesForGuaranteedOrderForRoute(funnelValue, excludeFailedState);
    }

    @Override
    public List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, Duration idleInterval,
            boolean excludeFailedState, String funnelCompId) {
        return messageDao.getMessagesForGuaranteedOrderForFunnel(funnelValue, idleInterval, excludeFailedState,
                funnelCompId);
    }

    @Transactional
    @Override
    public void setStatePostponed(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.isTrue(msg.getState().equals(MsgStateEnum.PROCESSING)
                        || msg.getState().equals(MsgStateEnum.NEW)
                        || msg.getState().equals(MsgStateEnum.IN_QUEUE),
                "the message must be in PROCESSING, NEW or IN_QUEUE state, but state is " + msg.getState());

        msg.setState(MsgStateEnum.POSTPONED);
        msg.setLastUpdateTimestamp(Instant.now());

        messageDao.update(msg);

        LOG.debug("State of the message " + msg.toHumanString() + " was changed to " + MsgStateEnum.POSTPONED);
    }

    @Transactional
    @Override
    public void setFunnelComponentId(Message msg, String funnelCompId) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.hasText(funnelCompId, "the funnelCompId must not be empty");

        Assert.isTrue(msg.getState().equals(MsgStateEnum.PROCESSING)
                || msg.getState().equals(MsgStateEnum.IN_QUEUE),
                "the message must be in PROCESSING or IN_QUEUE state, but state is " + msg.getState());

        msg.setFunnelComponentId(funnelCompId);
        msg.setLastUpdateTimestamp(Instant.now());

        messageDao.update(msg);

        LOG.debug("Sets funnel ID of the message " + msg.toHumanString() + " to value: " + funnelCompId);
    }

    @Nullable
    @Override
    @Transactional
    public Message findPostponedMessage(Duration interval) {
        Assert.notNull(interval, "interval must not be null");

        return messageDao.findPostponedMessage(interval);
    }

    @Nullable
    @Override
    @Transactional
    public Message findPartlyFailedMessage(Duration interval) {
        Assert.notNull(interval, "interval must not be null");

        return messageDao.findPartlyFailedMessage(interval);
    }
}
