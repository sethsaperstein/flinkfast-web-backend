FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY src /app/src
COPY pom.xml /app/pom.xml
RUN mvn -f /app/pom.xml clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/flinkfast-web-backend-1.0.0.jar .
CMD ["java", "-jar", "flinkfast-web-backend-1.0.0.jar"]
