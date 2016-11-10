OpenHub integration framework - admin web GUI
====================================================================

Goal: web frontend GUI offers main functionality for monitoring and administration of integration solution
    (you can get statistics, version info, message info, message log and you can re-process failed messages etc.)


Spring profiles
----------------------------
Info: http://blog.springsource.org/2011/02/14/spring-3-1-m1-introducing-profile/
Active profiles via system properties: -Dspring.profiles.active="dev"

There are defined the following Spring profiles:
- dev: development (enables lot of validations and logging)
- prod: deployment for production (DEFAULT)


MAVEN PROFILES
----------------------------
- esb.dev (default) - development
- esb.prod - production
- esb.psSql - PostgreSQL for development
- esb.psSql.prod - PostgreSQL for production (libraries are provided)


Common public services:
----------------------------
/http/version: get version (e.g. 0.1-SNAPSHOT.r123)
/http/ping: simple ping service

