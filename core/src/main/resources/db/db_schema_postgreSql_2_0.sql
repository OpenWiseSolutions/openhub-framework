--
-- DB increment script for version 2.0
--

--
-- tables: configuration
--
drop table if exists configuration_item cascade;

create table configuration_item (
    -- unique code of one configuration item, e.g. ohf.asyncThread.processing.count.name
    code            varchar(100)    not null unique,
    -- unique code for specific configuration scope, e.g. "dataSource" for data source settings
    category_code   varchar(100)    not null,
    -- current (valid) value
    current_value   varchar(1000)   null,
    -- default value if there is no current_value defined
    default_value   varchar(1000)   null,
    -- data type of current and default value
    data_type       varchar(20)     not null,
    -- is this configuration item mandatory? In other worlds must be at least one current or default value defined?
    mandatory       boolean         not null default true,
    -- regular expression for checking if current value is valid
    validation      varchar(100)    null,
    primary key (code)
);

DROP INDEX IF EXISTS configuration_item_cat_code_idx;
CREATE INDEX configuration_item_cat_code_idx ON configuration_item (category_code);


ALTER TABLE configuration_item OWNER TO openhub;


-- grant privileges to 'openhub' user
GRANT SELECT, USAGE, UPDATE ON openhub_sequence TO openhub;
GRANT SELECT, INSERT, UPDATE, DELETE ON message, external_call, request, response, configuration_item TO openhub
