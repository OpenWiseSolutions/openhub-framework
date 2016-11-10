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

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Implementation of {@link ConfirmationService} interface.
 *
 * @author Petr Juza
 */
public class ConfirmationServiceImpl implements ConfirmationService {

    /**
     * Maximum count of confirmation fails when will finish further processing.
     */
    @Value("${asynch.confirmation.failedLimit}")
    private int failedCountLimit;

    @Autowired
    private ExternalCallDao extCallDao;

    @Transactional
    @Override
    public ExternalCall insertFailedConfirmation(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.notNull(msg.getState() == MsgStateEnum.OK || msg.getState() == MsgStateEnum.FAILED,
                "the msg must in state OK or FAILED, but state is " + msg.getState());

        ExternalCall extCall = ExternalCall.createFailedConfirmation(msg);

        extCallDao.insert(extCall);

        Log.debug("Inserted confirmation failed call " + msg.toHumanString());

        return extCall;
    }

    @Transactional
    @Override
    public void confirmationComplete(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(ExternalCall.CONFIRM_OPERATION.equals(extCall.getOperationName()),
                "the extCall must be a " + ExternalCall.CONFIRM_OPERATION + ", but is " + extCall.getOperationName());
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the confirmation must be in PROCESSING state, but state is " + extCall.getState());

        extCall.setState(ExternalCallStateEnum.OK);

        extCallDao.update(extCall);

        Log.debug("Confirmation call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.OK);
    }

    @Transactional
    @Override
    public void confirmationFailed(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(ExternalCall.CONFIRM_OPERATION.equals(extCall.getOperationName()),
                "the extCall must be a " + ExternalCall.CONFIRM_OPERATION + ", but is " + extCall.getOperationName());
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the confirmation must be PROCESSING state, but is in " + extCall.getState());

        int failedCount = extCall.getFailedCount() + 1;
        extCall.setFailedCount(failedCount);

        ExternalCallStateEnum state = ExternalCallStateEnum.FAILED;
        if (failedCount > failedCountLimit) {
            state = ExternalCallStateEnum.FAILED_END;
        }

        extCall.setState(state);

        extCallDao.update(extCall);

        Log.debug("Confirmation call " + extCall.toHumanString() + " changed state to " + state);
    }
}
