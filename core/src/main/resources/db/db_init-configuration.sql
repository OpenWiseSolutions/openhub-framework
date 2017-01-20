--
-- DB initialization script of default configuration parameters
--

--
-- core.async
--

-- failedCount of partly fails before message will be marked as completely FAILED
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.countPartlyFailsBeforeFailed', 'core.async', 3, 3, 'INT', true, null);

-- How often to run repair process (in seconds)
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.repairRepeatTimeSec', 'core.async', 300, 300, 'INT', true, null);

-- Number of concurrent consumers for processing of asynch. messages
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.concurrentConsumers', 'core.async', 5, 5, 'INT', true, null);

-- How often to run process for pooling partly failed messages (in seconds)
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.partlyFailedRepeatTimeSec', 'core.async', 60, 60, 'INT', true, null);

-- Interval (in seconds) between two tries of partly failed messages
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.partlyFailedIntervalSec', 'core.async', 60, 60, 'INT', true, null);

-- maximum count of confirmation fails when will finish further processing
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.failedLimit', 'core.async', 3, 3, 'INT', true, null);

-- How often to run process for pooling failed confirmations (in seconds)
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.repeatTimeSec', 'core.async', 60, 60, 'INT', true, null);

-- Interval (in seconds) between two tries of failed confirmations
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.intervalSec', 'core.async', 60, 60, 'INT', true, null);

-- Skip asynchronous external calls for operation URIs that match the specified RegEx pattern
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.externalCall.skipUriPattern', 'core.async', null, null, 'STRING', false, null);

-- Interval (in seconds) after that can be postponed message processed again
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.postponedIntervalSec', 'core.async', 5, 5, 'INT', true, null);

-- Interval (in seconds) after that postponed messages will fail
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.postponedIntervalSecWhenFailedSec', 'core.async', 300, 300, 'INT', true, null);


--
-- core.mail
--

-- administrator email(s); if more emails, then separated them with semicolon (if empty then email won't be sent)
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.admin', 'core.mail', null, null, 'STRING', false, null);

-- email address FROM for sending emails
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.from', 'core.mail', 'OpenHub integration platform <openhub@openwise.cz>', null, 'STRING', false, null);

-- SMTP server for sending emails
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.smtp.server', 'core.mail', 'localhost', null, 'STRING', false, null);

--
-- core.dir
--

-- directory for storing temporary files
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.dir.temp', 'core.dir', null, null, 'FILE', false, null);

-- file repository directory where files will be stored
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.dir.fileRepository', 'core.dir', null, null, 'FILE', false, null);

--
-- core.contextCall
--

-- URI of this localhost application, including port number
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.contextCall.localhostUri', 'core.contextCall', 'http://localhost:8080', 'http://localhost:8080', 'STRING', true, null);

--
-- core.throttling
--

-- true for disabling throttling at all
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.throttling.disable', 'core.throttling', 'false', 'false', 'BOOLEAN', true, null);

--
-- core.alerts
--

--  How often to run checking of alerts (in seconds), value -1 no run checking of alerts
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.alerts.repeatTimeSec', 'core.alerts', -1, -1, 'INT', true, null);

--
-- core.endpoints
--

-- pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.endpoints.includePattern', 'core.endpoints', '^(spring-ws|servlet).*$', '^(spring-ws|servlet).*$', 'STRING', true, null);

--
-- core.requestSaving
--

-- true for enabling saving requests/responses for filtered endpoints URI
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.requestSaving.enable', 'core.requestSaving', 'false', 'false', 'BOOLEAN', true, null);

-- pattern for filtering endpoints URI which requests/response should be saved
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.requestSaving.endpointFilter', 'core.requestSaving', '^(spring-ws|servlet).*$', '^(spring-ws|servlet).*$', 'STRING', true, null);
