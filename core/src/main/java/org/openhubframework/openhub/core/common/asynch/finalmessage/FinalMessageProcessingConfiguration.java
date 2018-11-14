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

import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessageProcessor;
import org.openhubframework.openhub.api.asynch.finalmessage.FinalMessagesProcessingService;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration for final messages processing.
 * Does declare all the necessary beans.
 *
 * For more info about final messages processing, see javadoc of {@link FinalMessagesProcessingService} &
 * {@link FinalMessageProcessor}.
 *
 * @author Karel Kovarik
 * @since 2.1
 */
@Configuration
@ConditionalOnProperty(value = CoreProps.ASYNCH_FINAL_MESSAGES_PROCESSING_ENABLED)
public class FinalMessageProcessingConfiguration {

    @Bean
    public FinalMessagesProcessingJob finalMessagesProcessingJob() {
        return new FinalMessagesProcessingJob();
    }

    @Bean
    public FinalMessagesProcessingService finalMessagesProcessingService(PlatformTransactionManager transactionManager) {
        return new FinalMessagesProcessingServiceImpl(transactionManager);
    }

    @Bean
    @ConditionalOnProperty(value = CoreProps.ASYNCH_FINAL_MESSAGES_DELETE_PROCESSOR_ENABLED)
    public FinalMessageProcessor deleteFinalMessageProcessor() {
        return new DeleteFinalMessageProcessor();
    }
}
