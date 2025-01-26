# Stage 1: Build the JAR file using Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Copy the JAR file into a minimal base image
FROM maven:3.9.9-eclipse-temurin-17-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
