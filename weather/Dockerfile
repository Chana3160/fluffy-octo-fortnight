# Use OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder into the container
COPY target/weather-service-0.0.1-SNAPSHOT.jar /app/weather-service.jar

# Expose the port the app runs on (Spring Boot default is 8080)
EXPOSE 8085

# Run the Spring Boot application with the JAR file
ENTRYPOINT ["java", "-jar", "weather-service.jar"]
