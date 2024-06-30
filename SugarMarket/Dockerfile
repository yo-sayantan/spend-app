# Use the official OpenJDK base image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/sugarmarket-0.0.1-SNAPSHOT.jar /app

# Specify the command to run your application
CMD ["java", "-jar", "your-application.jar"]