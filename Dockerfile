FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
ENV KTOR_PORT=8080
ENV KTOR_IP="0.0.0.0"
ENV MYSQL_URL="ivm-accounts.ch53lvtsczj2.eu-west-3.rds.amazonaws.com"
ENV MYSQL_PORT=3306
ENV MYSQL_DB="ivmaccounts"
ENV MYSQL_USER="admin"
ENV MYSQL_PASS="ivmmanager"
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/GYServer.jar
ENTRYPOINT ["java","-jar","/app/GYServer.jar"]