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


-- Call db_init-configuration.sql for inserting default configuration items

--
-- tables: node
--

drop table if exists node cascade;

create table node (
    node_id         int8            not null,
    code            varchar(64)     not null unique,
    name            varchar(256)    not null unique,
    description     varchar(2056)   null,
    state           varchar(64)     not null,
    primary key (node_id)
);

alter table node add constraint uq_node_code unique (code);
alter table node add constraint uq_node_name unique (name);

drop index if exists node_code_idx;
create index node_code_idx ON node (code);

ALTER TABLE node OWNER TO openhub;

--
-- Update in table message
--

alter table message add column node_id int8 null;
alter table message add constraint fk_message_node foreign key (node_id) references node;
alter table message add column start_in_queue_timestamp timestamp null;

drop index if exists msg_node_id_idx;
create index msg_node_id_idx ON message (node_id);
