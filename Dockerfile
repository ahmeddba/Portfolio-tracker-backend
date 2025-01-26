# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the 'target' folder into the container
COPY target/Stocks-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that your Spring Boot app will run on (default is 8080)
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
