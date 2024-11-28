FROM eclipse-temurin:17.0.9_9-jre-alpine
VOLUME /tmp
RUN apk add --no-cache nmap-nping util-linux iputils-ping procps psmisc curl
RUN apk add --no-cache iperf3=3.16-r0 --repository=https://dl-cdn.alpinelinux.org/alpine/v3.19/main
ARG JAR_FILE
COPY build/libs/pulceo-node-agent-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]