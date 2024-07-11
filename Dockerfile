# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/easybackend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 9999

# Run the JAR file
ENTRYPOINT ["java","-jar","app.jar"]