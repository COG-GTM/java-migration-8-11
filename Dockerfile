# Build stage
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:11-jre
WORKDIR /app

# Create non-root user for security
RUN groupadd -r bankapp && useradd -r -g bankapp bankapp

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R bankapp:bankapp /app

# Switch to non-root user
USER bankapp

# Expose application port
EXPOSE 8989

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8989/bank-api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
