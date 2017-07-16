-- pattern for all input URIs into ESB
INSERT INTO configuration_item (code, description, category_code, current_value, default_value, data_type, mandatory, validation)
    VALUES('ohf.uri.inputPattern', 'Pattern for all input URIs into ESB',
'core.server', '^(spring-ws).*$', '^(spring-ws).*$', 'STRING', true, null);