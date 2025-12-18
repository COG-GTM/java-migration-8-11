# Build stage using Maven image with Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Java 17 module system compatibility flags for older Maven plugins and Lombok
ENV MAVEN_OPTS="\
--add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
--add-opens jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"

# Copy pom.xml first for better dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install wget for health checks (curl not available in JRE image)
RUN apt-get update && apt-get install -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/*

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

# Health check using wget instead of curl
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8989/bank-api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
