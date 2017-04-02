FROM postgres:9.6
ENV POSTGRES_DB openhubdb
ENV POSTGRES_USER openhub
ENV POSTGRES_PASSWORD pass
COPY ohb-schema-create.sql /docker-entrypoint-initdb.d/