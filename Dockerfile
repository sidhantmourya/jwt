FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/jwt-0.0.1-SNAPSHOT.jar jwt.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "jwt.jar" ]