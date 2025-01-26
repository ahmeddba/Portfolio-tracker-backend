# Stage 1: Build the JAR file using Maven
FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /app
COPY . /app
RUN mvn clean package

# Stage 2: Copy the JAR file into a minimal base image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Stocks-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
