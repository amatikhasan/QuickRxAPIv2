# Use an official OpenJDK runtime as a parent image (Java 17)
FROM eclipse-temurin:17-jre-jammy

# Set a working directory
WORKDIR /app

# Copy the executable JAR file from the build context to the image
# The JAR file is typically found in build/libs/your-app-name-version.jar
# Adjust the JAR name as per your build output
COPY build/libs/quickrx-springboot-0.0.1-SNAPSHOT.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Define environment variables for external configuration (examples)
# These can be overridden when running the container
ENV SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/quickrx_db"
ENV SPRING_DATASOURCE_USERNAME="root"
ENV SPRING_DATASOURCE_PASSWORD="password"
ENV JWT_SECRET="default-super-secret-key-please-change-in-prod"
ENV FILE_UPLOAD_DIR="/app/uploads"

# Ensure the upload directory exists within the container
RUN mkdir -p /app/uploads

# Run the JAR file when the container launches
# Using exec form is preferred
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Alternatively, for more complex startup or passing JVM options:
# ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
# Or use a script:
# COPY entrypoint.sh /app/entrypoint.sh
# RUN chmod +x /app/entrypoint.sh
# ENTRYPOINT ["/app/entrypoint.sh"]
