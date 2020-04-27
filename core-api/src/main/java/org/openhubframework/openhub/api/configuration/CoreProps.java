/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.configuration;

import static org.openhubframework.openhub.common.OpenHubPropertyConstants.PREFIX;


/**
 * Constants of core property names.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class CoreProps {

    /**
     * Failed count of partly fails before message will be marked as completely FAILED.
     */
    public static final String ASYNCH_COUNT_PARTLY_FAILS_BEFORE_FAILED = PREFIX + "asynch.countPartlyFailsBeforeFailed";

    /**
     * How often to run repair process (in seconds).
     */
    public static final String ASYNCH_REPAIR_REPEAT_TIME_SEC = PREFIX + "asynch.repairRepeatTimeSec";

    /**
     * Number of concurrent consumers for processing of asynch. messages.
     */
    public static final String ASYNCH_CONCURRENT_CONSUMERS = PREFIX + "asynch.concurrentConsumers";

    /**
     * How often to run process for pooling partly failed messages (in seconds).
     */
    public static final String ASYNCH_PARTLY_FAILED_REPEAT_TIME_SEC = PREFIX + "asynch.partlyFailedRepeatTimeSec";

    /**
     * Interval (in seconds) between two tries of partly failed messages.
     */
    public static final String ASYNCH_PARTLY_FAILED_INTERVAL_SEC = PREFIX + "asynch.partlyFailedIntervalSec";

    /**
     * Maximum count of confirmation fails when will finish further processing.
     */
    public static final String ASYNCH_CONFIRMATION_FAILED_LIMIT = PREFIX + "asynch.confirmation.failedLimit";

    /**
     * How often to run process for pooling failed confirmations (in seconds).
     */
    public static final String ASYNCH_CONFIRMATION_REPEAT_TIME_SEC = PREFIX + "asynch.confirmation.repeatTimeSec";

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    public static final String ASYNCH_CONFIRMATION_INTERVAL_SEC = PREFIX + "asynch.confirmation.intervalSec";

    /**
     * Skip asynchronous external calls for operation URIs that match the specified RegEx pattern.
     */
    public static final String ASYNCH_EXTERNAL_CALL_SKIP_URI_PATTERN = PREFIX + "asynch.externalCall.skipUriPattern";

    /**
     * Interval (in seconds) after that can be postponed message processed again.
     */
    public static final String ASYNCH_POSTPONED_INTERVAL_SEC = PREFIX + "asynch.postponedIntervalSec";

    /**
     * Interval (in seconds) after that postponed messages will fail.
     */
    public static final String ASYNCH_POSTPONED_INTERVAL_WHEN_FAILED_SEC = PREFIX + "asynch.postponedIntervalWhenFailedSec";

    /**
     * Final messages processing enabled or disabled. Note: cannot be defined in database, needs to be in properties.
     */
    public static final String ASYNCH_FINAL_MESSAGES_PROCESSING_ENABLED = PREFIX + "asynch.finalMessages.processingEnabled";

    /**
     * Final messages processing job interval, in seconds.
     */
    public static final String ASYNCH_FINAL_MESSAGES_PROCESSING_INTERVAL_SEC = PREFIX + "asynch.finalMessages.processingIntervalSec";

    /**
     * Maximum number of messages processed per job.
     */
    public static final String ASYNCH_FINAL_MESSAGES_ITERATION_MESSAGE_LIMIT = PREFIX + "asynch.finalMessages.iterationMessageLimit";

    /**
     * Configuration of final message processor that does delete messages from the datastore.
     * Note: final message processing needs to be enabled in order to do that.
     */
    public static final String ASYNCH_FINAL_MESSAGES_DELETE_PROCESSOR_ENABLED = PREFIX + "asynch.finalMessages.deleteProcessor.enabled";

    /**
     * Prefix for setting duration to keep messages in final states in the datastore.
     * After this period expires and there is no other action with the message, it will be processed as final (deleted probably).
     * Setting to '0' means, that messages will be processed as soon as possible (next scheduled job).
     * Setting to '-1' means, that messages in given state will NOT be processed.
     * Property is set for each state separately, in pattern:
     * ASYNCH_FINAL_MESSAGES_PREFIX + <lower-case state name> + ASYNCH_FINAL_MESSAGES_SAVE_TIME_IN_SEC_SUFFIX
     * Example:
     * ohf.asynch.finalMessages.ok.saveTimeInSec
     */
    public static final String ASYNCH_FINAL_MESSAGES_PREFIX = PREFIX + "asynch.finalMessages.";

    /**
     * Suffix to be used in conjuction with {@link CoreProps.ASYNCH_FINAL_MESSAGES_PREFIX}.
     */
    public static final String ASYNCH_FINAL_MESSAGES_SAVE_TIME_IN_SEC_SUFFIX = ".saveTimeInSec";

    /**
     * Sending emails to administrators enabled or disabled.
     */
    public static final String MAIL_ADMIN_ENABLED = PREFIX + "mail.admin.enabled";

    /**
     * Administrator email(s); if more emails, then separated them with semicolon, if empty then email won't be sent.
     */
    public static final String MAIL_ADMIN = PREFIX + "mail.admin";

    /**
     * Email address FROM for sending emails.
     */
    public static final String MAIL_FROM = PREFIX + "mail.from";

    /**
     * SMTP server for sending emails.
     */
    public static final String MAIL_SMTP_SERVER = PREFIX + "mail.smtp.server";

    /**
     * Directory for storing temporary files.
     */
    public static final String DIR_TEMP = PREFIX + "dir.temp";

    /**
     * File repository directory where files will be stored.
     */
    public static final String DIR_FILE_REPOSITORY = PREFIX + "dir.fileRepository";

    /**
     * URI of this localhost application, including port number (necessary for direct and context calls).
     */
    public static final String SERVER_LOCALHOST_URI = PREFIX + "server.localhostUri";

    /**
     * Enable/disable checking of localhostUri.
     */
    public static final String SERVER_LOCALHOST_URI_CHECK = PREFIX + "server.localhostUri.check";

    /**
     * True for disabling throttling at all.
     */
    public static final String DISABLE_THROTTLING = PREFIX + "disable.throttling";

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    public static final String ENDPOINTS_INCLUDE_PATTERN = PREFIX + "endpoints.includePattern";

    /**
     * True for enabling saving requests/responses for filtered endpoints URI.
     */
    public static final String REQUEST_SAVING_ENABLE = PREFIX + "requestSaving.enable";

    /**
     * Pattern for filtering endpoints URI which requests/response should be saved.
     */
    public static final String REQUEST_SAVING_ENDPOINT_FILTER = PREFIX + "requestSaving.endpointFilter";

    /**
     * How often to run checking of alerts (in seconds), value -1 no run checking of alerts.
     */
    public static final String ALERTS_REPEAT_TIME_SEC = PREFIX + "alerts.repeatTimeSec";

    /**
     * Pattern that defines which property names should be loaded from DB.
     */
    public static final String PROPERTY_INCLUDE_PATTERN = PREFIX + "dbProperty.includePattern";

    /**
     * Code of actual node for this application server instance.
     */
    public static final String CLUSTER_ACTUAL_NODE_INSTANCE_CODE = PREFIX + "cluster.actualNodeInstance.code";

    /**
     * Pattern for all input URIs into ESB.
     */
    public static final String URI_INPUT_PATTERN_FILTER = PREFIX + "uri.inputPattern";

    private CoreProps() {
    }
}
