/*
 * Copyright 2002-2016 the original author or authors.
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
    public static final String ASYNCH_POSTPONED_INTERVAL_WHEN_FAILED_SEC = PREFIX + "asynch.postponedIntervalSecWhenFailedSec";

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

    private CoreProps() {
    }
}
