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

package org.openhubframework.openhub.core.common.asynch.repair;

import static java.lang.Math.min;

import java.util.List;

import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * Implementation that uses DB to find external calls
 * and also uses DB to write them back after they're repaired.
 */
public class RepairExternalCallDbImpl implements RepairExternalCallService {

    private static final int BATCH_SIZE = 10;

    private TransactionTemplate transactionTemplate;

    @Autowired
    private ExternalCallDao externalCallDao;

    /**
     * How often to run repair process (in seconds).
     */
    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;

    @Override
    public void repairProcessingExternalCalls() {
        // find external calls in PROCESSING state
        List<ExternalCall> extCalls = findProcessingExternalCalls();

        Log.debug("Found {} external call(s) for repairing ...", extCalls.size());

        // repair external calls in batches
        int batchStartIncl = 0;
        int batchEndExcl;
        while (batchStartIncl < extCalls.size()) {
            batchEndExcl = min(batchStartIncl + BATCH_SIZE, extCalls.size());
            updateExternalCallsInDB(extCalls.subList(batchStartIncl, batchEndExcl));
            batchStartIncl = batchEndExcl;
        }

    }

    private List<ExternalCall> findProcessingExternalCalls() {
        return transactionTemplate.execute(new TransactionCallback<List<ExternalCall>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<ExternalCall> doInTransaction(TransactionStatus status) {
                return externalCallDao.findProcessingExternalCalls(repeatInterval);
            }
        });
    }

    private void updateExternalCallsInDB(final List<ExternalCall> externalCalls) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (ExternalCall extCall : externalCalls) {
                    Log.warn("The extCall {} is in {} state and is being changed to {}.",
                            extCall.toHumanString(), extCall.getState(), ExternalCallStateEnum.FAILED);

                    extCall.setState(ExternalCallStateEnum.FAILED);
                    externalCallDao.update(extCall);
                }
            }
        });
    }

    @Required
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void setExternalCallDao(ExternalCallDao externalCallDao) {
        this.externalCallDao = externalCallDao;
    }
}
