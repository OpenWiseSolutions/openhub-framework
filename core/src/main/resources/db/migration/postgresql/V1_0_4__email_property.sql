-- true for enabling sending emails to administrators
INSERT INTO configuration_item (code, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.mail.admin.enabled', 'core.mail', 'false', 'false', 'BOOLEAN', true, null);