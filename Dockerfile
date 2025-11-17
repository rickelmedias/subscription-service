FROM eclipse-temurin:21.0.2_13-jdk

# Set the working directory in the container
WORKDIR /subscription-service

# Copy the JAR file into the container
COPY target/subscription-service-*.jar app.jar

# Expose the port that your application will run on
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]
