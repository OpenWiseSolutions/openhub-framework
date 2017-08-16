--
-- DB script for creating necessary tables (PostgreSQL 9.x)
--
-- PREREQUISITE for successful flyway run with PostgreSQL:
-- 	  there is user/role (e.g. openhubusr)
-- 	  there is database (e.g. openhubdb)
-- 	  there is schema openhub
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- START: create schema (call once only)
--

-- Note: Heroku runs the SQL below to create a user and database for you.
-- You cannot create or modify databases and roles on Heroku Postgres.
-- For simple use if we need upgrade database schema from command line, we uncomment privileges double lines which
-- global configure privileges of created objects in schema for appropriate user

--
-- END: create schema
--

SET search_path = openhub, pg_catalog;

drop sequence if exists openhub_sequence;

create sequence openhub_sequence;

--
-- table: message
--
drop table if exists message cascade;

create table message (
  msg_id int8 not null,
  correlation_id varchar(100) not null,
  process_id varchar(100),
  msg_timestamp timestamp not null,
  receive_timestamp timestamp not null,
  service varchar(30) not null,
  source_system varchar(15) not null,
  state varchar(15) not null,
  start_process_timestamp timestamp,
  object_id varchar(50),
  entity_type varchar(30),
  operation_name varchar(100) not null,
  payload text not null,
  envelope text null,
  failed_desc text,
  failed_error_code varchar(5),
  failed_count int4 not null,
  last_update_timestamp timestamp,
  custom_data varchar(20000),
  business_error varchar(20000),
  parent_msg_id int8,
  primary key (msg_id)
);

alter table message add constraint uq_correlation_system unique (correlation_id, source_system);

drop index if exists msg_state_idx;
create index msg_state_idx ON message (state);

-- adding funnel_value
alter table message add column funnel_value varchar(50) null;

drop index if exists funnel_value_idx;
create index funnel_value_idx ON message (funnel_value);

-- ALTER TABLE message OWNER TO openhubusr;


--
-- table: external_call
--
drop table if exists external_call cascade;

create table external_call (
  call_id int8 not null,
  creation_timestamp timestamp not null,
  entity_id varchar(150) not null,
  failed_count integer not null,
  last_update_timestamp timestamp not null,
  msg_timestamp timestamp not null,
  msg_id bigint not null,
  operation_name varchar(100) not null,
  state varchar(20) not null,
  primary key (call_id)
);

alter table external_call add constraint uq_ext_call_operation_entity_id unique (operation_name, entity_id);

alter table external_call add constraint fk_external_call_message foreign key (msg_id) references message;

drop index if exists operation_name_idx;
create index operation_name_idx ON external_call (operation_name);

drop index if exists ext_state_idx;
create index ext_state_idx ON external_call (state);

-- ALTER TABLE external_call OWNER TO openhubusr;

--
-- tables: request and response
--
drop table if exists request cascade;
drop table if exists response cascade;

create table request (
  req_id int8 not null,
  msg_id int8 null,
  res_join_id varchar(100) not null,
  uri varchar(400) not null,
  req_envelope text not null,
  req_timestamp timestamp not null,
  primary key (req_id)
);

alter table request add constraint fk_request_message foreign key (msg_id) references message;

create table response (
  res_id int8 not null,
  req_id int8 null,
  res_envelope text null,
  failed_reason text null,
  res_timestamp timestamp null,
  failed boolean not null default false,
  primary key (res_id)
);

alter table response add constraint fk_response_request foreign key (req_id) references request;

-- ALTER TABLE request OWNER TO openhubusr;
-- ALTER TABLE response OWNER TO openhubusr;


--
-- table changes: message
--

-- adding parent_binding_type
alter table message add column parent_binding_type varchar(25) null;

-- if defined parent ID then it was HARD binding type by default
update message set parent_binding_type = 'HARD' where parent_msg_id is not null;

-- adding guaranteed order
alter table message add column guaranteed_order boolean not null default false;

-- adding exclude FAILED state for guaranteed order
alter table message add column exclude_failed_state boolean not null default false;

-- adding funnel_component_id
alter table message add column funnel_component_id varchar(50) null;


--
-- tables: response
--

-- adding reference to message
alter table response add column msg_id int8 null;


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
  -- description
  description   varchar(1000)   null,
  -- regular expression for checking if current value is valid
  validation      varchar(100)    null,
  primary key (code)
);

DROP INDEX IF EXISTS configuration_item_cat_code_idx;
CREATE INDEX configuration_item_cat_code_idx ON configuration_item (category_code);

-- ALTER TABLE configuration_item OWNER TO openhubusr;


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

-- ALTER TABLE node OWNER TO openhubusr;

--
-- sequence for node table
--
create sequence openhub_node_sequence;

--
-- Update in table message
--

alter table message add column node_id int8 null;
alter table message add constraint fk_message_node foreign key (node_id) references node;
alter table message add column start_in_queue_timestamp timestamp null;

drop index if exists msg_node_id_idx;
create index msg_node_id_idx ON message (node_id);



--
--  =============== ARCHIVATION FUNCTION ==================
--

--
-- table: archive_message
--
drop table if exists archive_message cascade;

create table archive_message (
  msg_id bigint not null,
  correlation_id character varying(100) not null,
  msg_timestamp timestamp without time zone not null,
  receive_timestamp timestamp without time zone not null,
  service character varying(30) not null,
  source_system character varying(15) not null,
  state character varying(15) not null,
  start_process_timestamp timestamp without time zone,
  object_id character varying(50),
  entity_type character varying(30),
  operation_name character varying(100) not null,
  payload text not null,
  envelope text,
  failed_desc text,
  failed_error_code character varying(5),
  failed_count integer not null,
  last_update_timestamp timestamp without time zone,
  custom_data character varying(20000),
  business_error character varying(20000),
  parent_msg_id bigint,
  funnel_value character varying(50),
  process_id character varying(100),
  parent_binding_type character varying(25),
  guaranteed_order boolean not null default false,
  exclude_failed_state boolean not null default false,
  funnel_component_id character varying(50)
);

--
-- table: archive_external_call
--
drop table if exists archive_external_call cascade;

create table archive_external_call (
  call_id bigint NOT NULL,
  creation_timestamp timestamp without time zone not null,
  entity_id character varying(150) not null,
  failed_count integer NOT NULL,
  last_update_timestamp timestamp without time zone not null,
  msg_timestamp timestamp without time zone not null,
  msg_id bigint not null,
  operation_name character varying(100) not null,
  state character varying(20) not null
);

--
-- tables: archive_request and archive_response
--
drop table if exists archive_request cascade;
drop table if exists archive_response cascade;

create table archive_request (
  req_id int8 not null,
  msg_id int8 null,
  res_join_id varchar(100) not null,
  uri varchar(400) not null,
  req_envelope text not null,
  req_timestamp timestamp not null
);

create table archive_response (
  res_id int8 not null,
  req_id int8 null,
  res_envelope text null,
  failed_reason text null,
  res_timestamp timestamp null,
  failed boolean not null default false,
  msg_id int8 null
);

--
-- function: archive_records(integer)
-- input: number of months after that the message will be archived
--
drop function if exists archive_records(integer);

create or replace function archive_records(integer)
  RETURNS void as
$BODY$
declare
  KeepTime TIMESTAMP;
begin
  IF $1 < 2
  then
    KeepTime := NOW() - INTERVAL '2 months';
  else
    KeepTime := NOW() - '1 months'::interval * $1;
  end if;

  RAISE NOTICE 'Older records in the number of months: %', $1;
  RAISE NOTICE 'Maximum limit for the records to be archived: %', KeepTime;

  --
  -- the place where you can insert a new table for archiving
  --
  insert into archive_request (
    req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
  )
  -- only request with reference on message
    select
      t.req_id,t.msg_id,t.res_join_id,t.uri,t.req_envelope,t.req_timestamp
    from request as t, message
    where message.msg_id = t.msg_id
          and message.state in ('OK', 'FAILED', 'CANCEL')
          and message.last_update_timestamp < KeepTime
    union
    -- all other without reference on message
    select
      req_id,msg_id,res_join_id,uri,req_envelope,req_timestamp
    from request
    where msg_id is null
          and req_timestamp < KeepTime;

  insert into archive_response (
    res_id,req_id,res_envelope,failed_reason,res_timestamp,failed,msg_id
  )
  -- response with reference on message or withnout
    select
      res.res_id,res.req_id,res.res_envelope,res.failed_reason,res.res_timestamp,res.failed,res.msg_id
    from response as res
      left outer join request as req on res.req_id = req.req_id
      left outer join message ON req.msg_id = message.msg_id
    where message.state in ('OK', 'FAILED', 'CANCEL')
          and (message.last_update_timestamp < KeepTime) or (res.res_timestamp < KeepTime);

  insert into archive_external_call (
    call_id,creation_timestamp,entity_id,failed_count,last_update_timestamp,
    msg_timestamp,msg_id,operation_name,state
  )
    select
      t.call_id,t.creation_timestamp,t.entity_id,t.failed_count,t.last_update_timestamp,
      t.msg_timestamp,t.msg_id,t.operation_name,t.state
    from external_call as t, message
    where message.msg_id = t.msg_id
          and message.state in ('OK', 'FAILED', 'CANCEL')
          and message.last_update_timestamp < KeepTime;

  insert into archive_message (
    msg_id,correlation_id,msg_timestamp,receive_timestamp,
    service,source_system,state,start_process_timestamp,object_id,entity_type,
    operation_name,payload,envelope,failed_desc,failed_error_code,failed_count,
    last_update_timestamp,custom_data,business_error,parent_msg_id,funnel_value,
    process_id,parent_binding_type,guaranteed_order,exclude_failed_state,funnel_component_id
  )
    select
      msg_id,correlation_id,msg_timestamp,receive_timestamp,
      service,source_system,state,start_process_timestamp,object_id,entity_type,
      operation_name,payload,envelope,failed_desc,failed_error_code,failed_count,
      last_update_timestamp,custom_data,business_error,parent_msg_id,funnel_value,
      process_id,parent_binding_type,guaranteed_order,exclude_failed_state,funnel_component_id
    from message
    where state in ('OK', 'FAILED', 'CANCEL')
          and last_update_timestamp < KeepTime;

  --
  -- the place where you can insert the command for truncate new table for archiving
  --

  --
  -- truncate request table
  --
  drop table if exists tmp_req cascade;
  create TEMP table tmp_req as
    -- only request with reference on message
    select r1.*
    from request r1
      join message m on (m.msg_id = r1.msg_id)
    where m.state in ('OK', 'FAILED', 'CANCEL')
          and m.last_update_timestamp < KeepTime
    union
    -- all other without reference on message
    select r2.*
    from request r2
    where msg_id is null
          and req_timestamp < KeepTime;

  execute 'TRUNCATE TABLE request CASCADE';
  insert into request select * from tmp_req;

  --
  -- truncate response table
  --
  drop table if exists tmp_resp cascade;
  create TEMP table tmp_resp as
    select r.*
    from response r
      left outer join request as req on r.req_id = req.req_id
      left outer join message ON req.msg_id = message.msg_id
    where message.state in ('OK', 'FAILED', 'CANCEL')
          and (message.last_update_timestamp < KeepTime) or (r.res_timestamp < KeepTime);

  execute 'TRUNCATE TABLE response CASCADE';
  insert into response select * from tmp_resp;

  --
  -- truncate external_call table
  --
  drop table if exists tmp_extcall cascade;
  create TEMP table tmp_extcall as
    select e.*
    from external_call e
      join message m on (m.msg_id = e.msg_id)
    where (m.state not in ('OK', 'FAILED', 'CANCEL')
           or m.last_update_timestamp >= KeepTime);

  execute 'TRUNCATE TABLE external_call CASCADE';
  insert into external_call select * from tmp_extcall;

  --
  -- truncate message table
  --
  drop table if exists tmp_msg cascade;
  create TEMP table tmp_msg AS
    select m.*
    from message m
    where (m.state not in ('OK', 'FAILED', 'CANCEL')
           or m.last_update_timestamp >= KeepTime);

  execute 'TRUNCATE TABLE message CASCADE';
  insert into message select * from tmp_msg;

  RETURN;

end;
$BODY$
language plpgsql VOLATILE
COST 100;