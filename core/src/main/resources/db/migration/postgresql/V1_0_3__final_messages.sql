--
-- core.async
--

-- final messages processing job interval, in seconds.
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.finalMessages.processingIntervalSec', 'core.async', 3600, 3600, 'INT', true, null);

-- maximum number of messages processed per job.
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.finalMessages.iterationMessageLimit', 'core.async', 10000, 10000, 'INT', true, null);

-- duration in seconds to keep messages in OK state in the datastore
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.finalMessages.ok.saveTimeInSec', 'core.async', 0, 0, 'INT', true, null);

-- duration in seconds to keep messages in FAILED state in the datastore
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.finalMessages.failed.saveTimeInSec', 'core.async', 2592000, 2592000, 'INT', true, null);

-- duration in seconds to keep messages in CANCEL state in the datastore
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.asynch.finalMessages.cancel.saveTimeInSec', 'core.async', 2592000, 2592000, 'INT', true, null);

