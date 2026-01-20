# Build stage
FROM maven:3.8-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app

# Create non-root user for security
RUN groupadd -r bankapp && useradd -r -g bankapp bankapp

# Copy the built JAR from build stage
COPY --from=build /app/target/bank-app-*.jar app.jar

# Change ownership to non-root user
RUN chown -R bankapp:bankapp /app
USER bankapp

# Expose the application port
EXPOSE 8989

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8989/bank-api/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
