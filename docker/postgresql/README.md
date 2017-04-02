## PostgreSQL with OpenHub schema

#### About
* PostgreSQL initialized with OpenHub schema, for fast setup of postgres db instantly usable with OpenHub.

#### Usage
* Just build docker image and run with port forwarded to your localhost
```
docker build . -t postgres-ohb
docker run -p 5432:5432 postgres-ohb
```
* Then you can just run OpenHub with the default postgresql configuration:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/openhubdb?currentSchema=openHub
spring.datasource.username=openhub
spring.datasource.password=pass
```

#### Access database with external tool
* You can access the running instance of database using application user
(openhub/pass).

#### More info
* Docker: https://docs.docker.com/
* PostgreSQL image: https://hub.docker.com/_/postgres/
