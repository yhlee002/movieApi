FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/moviesite_api_server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /api_server.jar

ENTRYPOINT ["java","-jar","/api_server.jar"]