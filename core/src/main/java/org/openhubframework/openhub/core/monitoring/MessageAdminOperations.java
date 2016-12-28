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

package org.openhubframework.openhub.core.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import org.openhubframework.openhub.core.common.asynch.msg.MessageOperationService;
import org.openhubframework.openhub.core.common.asynch.queue.JobStarterForMessagePooling;
import org.openhubframework.openhub.core.common.asynch.repair.RepairMessageService;


/**
 * JMX exporter of message administration operations.
 *
 * @author Petr Juza
 */
@Service
@ManagedResource(objectName = "org.openhubframework.openhub.core.monitoring:name=MessagesAdmin",
        description = "Message Administration")
public class MessageAdminOperations {

    private static final Logger LOG = LoggerFactory.getLogger(MessageAdminOperations.class);

    @Autowired
    private MessageOperationService messageOperationService;

    @Autowired
    private JobStarterForMessagePooling messagePooling;

    @Autowired
    private RepairMessageService repairMessageService;


    @ManagedOperation(description = "Restarts message for next processing. "
            + "totalRestart parameter determines if message should start from scratch again (true) or "
            + "if message should continue when it failed (false).")
    public void restartMessage(long msgId, boolean totalRestart) {
        LOG.debug("Restart message by JMX ...");

        messageOperationService.restartMessage(msgId, totalRestart);
    }

    @ManagedOperation(description = "Cancels next message processing, sets message to CANCEL state.")
    public void cancelMessage(long msgId) {
        LOG.debug("Cancel message (id = " + msgId + ") by JMX ...");

        messageOperationService.cancelMessage(msgId);
    }

    @ManagedOperation(description = "Starts next processing of PARTLY_FAILED and POSTPONED messages.")
    public void startNextProcessing() throws Exception {
        LOG.debug("Start next processing by JMX ...");

        messagePooling.start();
    }

    @ManagedOperation(description = "Starts repairing processing messages.")
    public void repairProcessingMessages() throws Exception {
        LOG.debug("Starts repairing processing messages by JMX ...");

        repairMessageService.repairProcessingMessages();
    }
}
