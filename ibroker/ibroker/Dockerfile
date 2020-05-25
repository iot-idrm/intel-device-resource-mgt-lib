FROM openjdk:8-jdk-alpine

ARG JAVA_OPTS="--spring.jmx.enabled=false -noverify -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
ARG JAR_FILE=./target/ibroker-0.0.1.jar

COPY run.sh        /
RUN chmod 777 /run.sh

COPY ${JAR_FILE} /app/app.jar

WORKDIR ["/app"]

EXPOSE 5000

ENTRYPOINT ["/run.sh"]