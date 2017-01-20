--
-- DB increment script for version 0.4
--

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

ALTER TABLE request OWNER TO openhub;
ALTER TABLE response OWNER TO openhub;


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




