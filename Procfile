web: java $JAVA_OPTS -Dserver.port=${PORT} -Dinfo.app.core.build.number=${HEROKU_RELEASE_VERSION} -Dspring.profiles.active=example-module,postgresql -Dspring.datasource.url=${DB_URL} -Dspring.datasource.username=${DB_USER} -Dspring.datasource.password=${DB_PASS} -Dspring.flyway.enabled=${DB_MIGRATION} -Dlogging.level.org.hibernate.SQL=warn -jar war/target/openhub-*.war
