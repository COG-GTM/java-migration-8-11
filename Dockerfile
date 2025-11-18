FROM openjdk:8-jdk-alpine

LABEL maintainer="bankapp@example.com"
LABEL description="Banking Application - Spring Boot"

WORKDIR /app

COPY target/bank-app-*.jar app.jar

EXPOSE 8989

ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_SECURITY_USER_NAME=bankapp
ENV SPRING_SECURITY_USER_PASSWORD=changeit

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
