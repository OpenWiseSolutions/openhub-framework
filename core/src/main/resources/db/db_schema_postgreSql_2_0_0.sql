--
-- DB increment script for version 2.0.0
--

-- adding start_in_queue_timestamp
alter table message add column start_in_queue_timestamp timestamp null;
