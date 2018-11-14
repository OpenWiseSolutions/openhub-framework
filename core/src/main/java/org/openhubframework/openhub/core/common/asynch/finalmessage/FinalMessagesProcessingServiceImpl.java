/*
 * Copyright 2018 the original author or authors.
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

package org.openhubframework.openhub.core.common.asynch.finalmessage;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessagesProcessingService;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.core.common.asynch.LogContextHelper;
import org.openhubframework.openhub.core.configuration.ConfigurationService;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link FinalMessagesProcessingService}.
 *
 * Does find messages in final states & configured last update time.
 * After startup, fast check if all required params are filled is performed.
 *
 * Note: there is technical limit on how many messages for each state are processed at most.
 * ({@link CoreProps#ASYNCH_FINAL_MESSAGES_ITERATION_MESSAGE_LIMIT}).
 *
 * @author Karel Kovarik
 * @since 2.1
 */
public class FinalMessagesProcessingServiceImpl implements FinalMessagesProcessingService {
    private static final Logger LOG = LoggerFactory.getLogger(FinalMessagesProcessingServiceImpl.class);

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected ConfigurationService configurationService;

    @ConfigurableValue(key = CoreProps.ASYNCH_FINAL_MESSAGES_ITERATION_MESSAGE_LIMIT)
    protected ConfigurationItem<Long> messagesTechnicalLimit;

    // processors
    @Autowired(required = false)
    protected List<FinalMessageProcessor> finalMessageProcessorList = new ArrayList<>();

    // final message state map
    protected Map<MsgStateEnum, String> finalMessageStatesConfig = new HashMap<>();

    // transactionTemplate
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public FinalMessagesProcessingServiceImpl(PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    // verify configuration
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {

        final Set<MsgStateEnum> finalMsgStates = Arrays.stream(MsgStateEnum.values())
                                                 .filter(MsgStateEnum::isFinal)
                                                 .collect(Collectors.toSet());

        // fill map finalMessageStatesConfig, key is state, value is configurationKey
        finalMsgStates
                .forEach(msgStateEnum ->  finalMessageStatesConfig.put(msgStateEnum, resolveConfigurationKey(msgStateEnum)));

        LOG.debug("Checking configuration for final messages processing.");
        for (MsgStateEnum msgStateEnum : finalMsgStates) {
            final String key = finalMessageStatesConfig.get(msgStateEnum);
            Assert.notNull(configurationService.getValue(Long.class, key),
                    MessageFormat.format("Save time for messages in state {0} was not configured. " +
                            "Please set corresponding configuration property {1} accordingly.", msgStateEnum, key)
            );
        }
    }

    @Override
    public void processMessages() {
        Assert.notNull(finalMessageStatesConfig, "finalMessageStatesConfig must not be null");

        for (Map.Entry<MsgStateEnum, String> msgStateEntry : finalMessageStatesConfig.entrySet()) {
            // find messages
            final List<Message> messageList = findMessagesForProcessing(msgStateEntry.getKey());
            LOG.trace("Will process '{}' messages in state '{}'.", messageList.size(), msgStateEntry.getKey());

            for (Message message : messageList) {
                // set log context
                LogContextHelper.setLogContextParams(message, null);
                try {
                    // execute in new transaction
                    transactionTemplate.execute((TransactionStatus status) -> {
                        // invoke all the processors
                        for (FinalMessageProcessor finalMessageProcessor : finalMessageProcessorList) {
                            finalMessageProcessor.processMessage(message);
                        }
                       return null; // callback without result
                    });

                } catch (Exception ex) {
                    // continue with another message if handling of one fails.
                    LOG.error("Failed to process message : {}, will continue with next.", message, ex);
                }
                // clear MDC context
                LogContextHelper.removeLogContextParams();
            }
        }
    }

    /**
     * Find messages in given state that are eligible for processing.
     *
     * @param msgState the state.
     * @return list of messages, empty if none messages are eligible.
     */
    protected List<Message> findMessagesForProcessing(MsgStateEnum msgState) {
        long saveTimeInSeconds = getSaveTimeInSeconds(msgState);

        if (saveTimeInSeconds < 0) {
            LOG.trace("Messages in state [{}] will be skipped, as configured to be kept indefinitely.", msgState);
            return Collections.emptyList();
        }

        final MessageFilter messageFilter = new MessageFilter();
        messageFilter.setState(msgState);
        messageFilter.setLastChangeTo(Instant.now().minusSeconds(saveTimeInSeconds));

        long limit = messagesTechnicalLimit.getValue();
        LOG.trace("Will search for messages with filter {}, and technical limit {}.", messageFilter, limit);
        final List<Message> messageList = messageService.findMessagesByFilter(messageFilter, limit);

        if (limit == messageList.size()) {
            LOG.info("Reached limit for one iteration of job {}, probably there are other messages eligible" +
                    "to be processed. Will be processed in the next iteration.", limit);
        }

        return messageList;
    }

    /**
     * Get save time (meaning for how many seconds are messages kept in datastore, and ignored for the scope
     * of final message processing).
     *
     * @param msgStateEnum the msg state.
     * @return number of seconds to keep messages, before processing them as final.
     */
    protected long getSaveTimeInSeconds(MsgStateEnum msgStateEnum) {
        final String key = finalMessageStatesConfig.get(msgStateEnum);
        Assert.hasText(key, "the key was not set");
        final Long value = configurationService.getValue(Long.class, key);
        Assert.notNull(value, MessageFormat.format("The {0} is expected to be set.", key));
        return value;
    }

    /**
     * Get configuration key for final message save time.
     *
     * @param msgStateEnum the message state.
     * @return key to configuration.
     */
    protected String resolveConfigurationKey(MsgStateEnum msgStateEnum) {
        final StringBuilder sb = new StringBuilder();
        sb.append(CoreProps.ASYNCH_FINAL_MESSAGES_PREFIX);
        sb.append(msgStateEnum.name().toLowerCase());
        sb.append(CoreProps.ASYNCH_FINAL_MESSAGES_SAVE_TIME_IN_SEC_SUFFIX);
        return sb.toString();
    }
}
