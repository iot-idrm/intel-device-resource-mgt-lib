FROM openjdk:8-jdk-alpine

ARG JAVA_OPTS="--spring.jmx.enabled=false -noverify -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
ARG JAR_FILE=./target/httpproxy-0.0.1.jar

COPY ${JAR_FILE} /app/app.jar

COPY run.sh        /
RUN chmod 777 /run.sh

WORKDIR ["/app"]

EXPOSE 8081

ENTRYPOINT ["/run.sh"]
