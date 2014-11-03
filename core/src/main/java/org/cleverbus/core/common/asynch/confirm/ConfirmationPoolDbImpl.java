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

package org.cleverbus.core.common.asynch.confirm;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Polls confirmations in the {@link ExternalCallStateEnum#FAILED} state.
 * If there is this confirmation available then try to get and lock it for further processing.

 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ConfirmationPoolDbImpl implements ConfirmationPool {

    @Autowired
    private ExternalCallDao extCallDao;

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    @Value("${asynch.confirmation.interval}")
    private int interval;

    @Nullable
    @Override
    @Transactional
    public ExternalCall getNextConfirmation() {
        // -- is there next confirmation for processing?
        final ExternalCall extCall = extCallDao.findConfirmation(interval);

        if (extCall == null) {
            Log.debug("There is no FAILED confirmation for processing.");
            return null;
        }

        // try to get lock for the confirmation
        return lockConfirmation(extCall);
    }

    private ExternalCall lockConfirmation(final ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");

        try {
            ExternalCall lockedCall = extCallDao.lockConfirmation(extCall);
            Log.debug("Success in getting lock for confirmation " + lockedCall.toHumanString());
            return lockedCall;
        } catch (Exception ex) {
            throw new LockFailureException("Not success in getting lock for confirmation " + extCall.toHumanString(), ex);
        }
    }
}
