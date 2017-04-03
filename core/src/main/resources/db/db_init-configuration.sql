--
-- DB initialization script of default configuration parameters
--

--
-- core.async
--

-- Count of partly fails before message will be marked as completely FAILED
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.countPartlyFailsBeforeFailed', 'Count of partly fails before message will be marked as completely FAILED',
    'core.async', 3, 3, 'INT', true, null);

-- How often to run repair process (in seconds)
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.repairRepeatTimeSec', 'How often to run repair process (in seconds)',
    'core.async', 300, 300, 'INT', true, null);

-- Number of concurrent consumers for processing of asynch. messages
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.concurrentConsumers', 'Number of concurrent consumers for processing of asynch. messages',
    'core.async', 5, 5, 'INT', true, null);

-- How often to run process for pooling partly failed messages (in seconds)
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.partlyFailedRepeatTimeSec', 'How often to run process for pooling partly failed messages (in seconds)',
    'core.async', 60, 60, 'INT', true, null);

-- Interval (in seconds) between two tries of partly failed messages
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.partlyFailedIntervalSec', 'Interval (in seconds) between two tries of partly failed messages',
    'core.async', 60, 60, 'INT', true, null);

-- Maximum count of confirmation fails when will finish further processing
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.failedLimit', 'Maximum count of confirmation fails when will finish further processing',
    'core.async', 3, 3, 'INT', true, null);

-- How often to run process for pooling failed confirmations (in seconds)
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.repeatTimeSec', 'How often to run process for pooling failed confirmations (in seconds)',
    'core.async', 60, 60, 'INT', true, null);

-- Interval (in seconds) between two tries of failed confirmations
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.confirmation.intervalSec', 'Interval (in seconds) between two tries of failed confirmations',
    'core.async', 60, 60, 'INT', true, null);

-- Skip asynchronous external calls for operation URIs that match the specified RegEx pattern
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.externalCall.skipUriPattern', 'Skip asynchronous external calls for operation URIs that match the specified RegEx pattern',
    'core.async', null, null, 'STRING', false, null);

-- Interval (in seconds) after that can be postponed message processed again
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.postponedIntervalSec', 'Interval (in seconds) after that can be postponed message processed again',
    'core.async', 5, 5, 'INT', true, null);

-- Interval (in seconds) after that postponed messages will fail
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.postponedIntervalWhenFailedSec', 'Interval (in seconds) after that postponed messages will fail',
    'core.async', 300, 300, 'INT', true, null);


--
-- core.mail
--

-- administrator email(s); if more emails, then separated them with semicolon (if empty then email won't be sent)
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.admin', 'Administrator email(s); if more emails, then separated them with semicolon (if empty then email won''t be sent)',
    'core.mail', null, null, 'STRING', false, null);

-- email address FROM for sending emails
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.from', 'Email address FROM for sending emails',
    'core.mail', 'OpenHub integration platform <openhub@openwise.cz>', null, 'STRING', false, null);

-- SMTP server for sending emails
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.smtp.server', 'SMTP server for sending emails',
    'core.mail', 'localhost', null, 'STRING', false, null);

--
-- core.dir
--

-- directory for storing temporary files
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.dir.temp', 'Directory for storing temporary files',
    'core.dir', null, null, 'FILE', false, null);

-- file repository directory where files will be stored
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.dir.fileRepository', 'File repository directory where files will be stored',
    'core.dir', null, null, 'FILE', false, null);

--
-- core.server
--

-- URI of this localhost application, including port number
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.server.localhostUri', 'URI of this localhost application, including port number',
    'core.server', 'http://localhost:8080', 'http://localhost:8080', 'STRING', false, null);

-- enable/disable checking of localhostUri
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.server.localhostUri.check', 'Enable/disable checking of localhostUri',
    'core.server', 'true', 'true', 'BOOLEAN', true, null);

--
-- core.throttling
--

-- true for disabling throttling at all
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.throttling.disable', 'True for disabling throttling at all',
    'core.throttling', 'false', 'false', 'BOOLEAN', true, null);

--
-- core.alerts
--

--  How often to run checking of alerts (in seconds), value -1 no run checking of alerts
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.alerts.repeatTimeSec', 'How often to run checking of alerts (in seconds), value -1 no run checking of alerts',
    'core.alerts', -1, -1, 'INT', true, null);

--
-- core.endpoints
--

-- pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.endpoints.includePattern', 'Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned',
    'core.endpoints', '^(spring-ws|servlet).*$', '^(spring-ws|servlet).*$', 'STRING', true, null);

--
-- core.requestSaving
--

-- true for enabling saving requests/responses for filtered endpoints URI
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.requestSaving.enable', 'True for enabling saving requests/responses for filtered endpoints URI',
    'core.requestSaving', 'false', 'false', 'BOOLEAN', true, null);

-- pattern for filtering endpoints URI which requests/response should be saved
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.requestSaving.endpointFilter', 'Pattern for filtering endpoints URI which requests/response should be saved',
    'core.requestSaving', '^(spring-ws|servlet).*$', '^(spring-ws|servlet).*$', 'STRING', true, null);
