DROP SCHEMA IF EXISTS openhub CASCADE;
CREATE SCHEMA openhub;

-- Note: Heroku runs the SQL below to create a user and database for you.
-- You cannot create or modify databases and roles on Heroku Postgres.
-- For simple use if we need upgrade database schema from command line, we uncomment privileges double lines which
-- global configure privileges of created objects in schema for appropriate user

ALTER SCHEMA openhub OWNER TO openhub;

ALTER DEFAULT PRIVILEGES IN SCHEMA openhub GRANT SELECT, USAGE, UPDATE ON SEQUENCES TO openhub;
ALTER DEFAULT PRIVILEGES IN SCHEMA openhub GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO openhub;