FROM openjdk:8-jdk-alpine
MAINTAINER author=github.com/alicanakkus

COPY target/springboot-prometheus-grafana-0.0.1-SNAPSHOT.jar lib/app.jar

ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar lib/app.jar