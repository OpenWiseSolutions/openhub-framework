# OpenHub integration framework - admin web GUI

Goal: web frontend GUI offers main functionality for monitoring and administration of integration solution.
> **Note:** You can get statistics, version info, message info, message log and you can re-process failed messages etc.).


## Spring profiles
Info: http://blog.springsource.org/2011/02/14/spring-3-1-m1-introducing-profile/
Active profiles via system properties: `-Dspring.profiles.active="dev"`

There are defined the following Spring profiles:
- `dev`: development (enables lot of validations and logging)
- `test`: profile is activated for unit testing  
- `prod`: deployment for production (DEFAULT)

Next Spring profiles are used for switching between databases:
- `h2` (default): database H2 in embedded in-memory mode, more information in chapter about H2 database
- `postgreSql`: profile for PostgreSQL database

## Maven profiles
- `esb.dev` (default) - development
- `esb.prod` - production
- `esb.psSql` - PostgreSQL for development
- `esb.psSql.prod` - PostgreSQL for production (libraries are provided)
- `full-build` - profile which is activated by default, can be deactivated for example to exclude admin-console maven module 
from build (faster build).
- `full-clean` - profile which is activated by default, it can be deactivated for example if cached admin-console maven module libs 
(npm packages and so on) should be used (=> faster build)


## Common public services:
- `/http/version`: get version (e.g. 0.1-SNAPSHOT.r123)
- `/http/ping`: simple ping service

> **Note:** [OpenHub uses Spring Boot] which includes a number of additional features to help you monitor and manage your application when it’s 
pushed to production.



[OpenHub uses Spring Boot]: https://openhubframework.atlassian.net/wiki/display/OHF/Spring+Boot+Actuator

