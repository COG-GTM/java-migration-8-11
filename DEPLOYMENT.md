# BankApp Deployment Guide

This document provides step-by-step instructions for deploying the BankApp application after the Java 8 to Java 11 migration.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Environment Requirements](#environment-requirements)
- [Pre-Deployment Checklist](#pre-deployment-checklist)
- [Deployment Steps](#deployment-steps)
- [Post-Deployment Verification](#post-deployment-verification)
- [Monitoring and Health Checks](#monitoring-and-health-checks)
- [Troubleshooting](#troubleshooting)

## Prerequisites

Before deploying the Java 11 version of BankApp, ensure the following requirements are met.

### Java Runtime Environment

The application requires Java 11 (LTS) or later. Recommended distributions include Eclipse Temurin (formerly AdoptOpenJDK), Amazon Corretto 11, or Oracle JDK 11. Verify the Java version by running `java -version` and confirm the output shows version 11 or higher.

### Build Tools

Maven 3.6.0 or later is required for building the application. The project includes Maven wrapper scripts (`mvnw` and `mvnw.cmd`) that can be used if Maven is not installed globally.

### System Resources

For development and testing environments, allocate at least 512MB of heap memory. For production environments, allocate at least 1GB of heap memory with 2GB recommended for optimal performance. The application requires approximately 100MB of disk space for the JAR file and logs.

## Environment Requirements

### Development Environment

The development environment uses an H2 in-memory database with default Spring Boot configuration. No external database setup is required. The application runs on port 8989 with the context path `/bank-api`.

### Staging/Production Environment

For staging and production deployments, consider the following configuration changes. Replace the H2 in-memory database with a persistent database such as PostgreSQL or MySQL. Configure external database connection properties in `application.yml` or via environment variables. Update security credentials from the default values (`bankapp:changeit`) to secure production credentials.

### Environment Variables

The following environment variables can be used to configure the application:

| Variable | Description | Default |
|----------|-------------|---------|
| `JAVA_HOME` | Path to JDK 11 installation | System default |
| `SERVER_PORT` | Application server port | 8989 |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | default |
| `SPRING_DATASOURCE_URL` | Database connection URL | jdbc:h2:mem:testdb |
| `SPRING_DATASOURCE_USERNAME` | Database username | sa |
| `SPRING_DATASOURCE_PASSWORD` | Database password | (empty) |
| `SPRING_SECURITY_USER_NAME` | Basic auth username | bankapp |
| `SPRING_SECURITY_USER_PASSWORD` | Basic auth password | changeit |

## Pre-Deployment Checklist

Complete the following checklist before deploying to any environment.

### Build Verification

1. Ensure the build completes successfully by running `mvn clean package -DskipTests`
2. Run all unit tests with `mvn test` and verify all tests pass
3. Verify the JAR file is created in the `target/` directory

### Configuration Review

1. Review `application.yml` for environment-specific settings
2. Confirm database connection settings are correct for the target environment
3. Verify security credentials are appropriate for the target environment
4. Check logging configuration is set to the appropriate level

### Infrastructure Readiness

1. Confirm the target server has Java 11 installed and configured
2. Verify network connectivity to any external services (databases, APIs)
3. Ensure firewall rules allow traffic on the application port (default 8989)
4. Confirm sufficient disk space for application logs

## Deployment Steps

### Step 1: Build the Application

Clone the repository and build the application JAR file.

```bash
git clone https://github.com/COG-GTM/java-migration-8-11.git
cd java-migration-8-11
mvn clean package -DskipTests
```

The build produces `target/bank-app-1.0.0.jar`.

### Step 2: Transfer Artifacts

Copy the JAR file to the target server. Use secure copy (SCP) or your organization's artifact repository.

```bash
scp target/bank-app-1.0.0.jar user@server:/opt/bankapp/
```

### Step 3: Configure the Environment

On the target server, set up the Java 11 environment.

```bash
export JAVA_HOME=/path/to/jdk-11
export PATH=$JAVA_HOME/bin:$PATH
java -version  # Verify Java 11 is active
```

### Step 4: Start the Application

Start the application using the following command. Adjust JVM options based on your environment requirements.

```bash
java -Xms512m -Xmx1024m \
     -Dspring.profiles.active=production \
     -jar /opt/bankapp/bank-app-1.0.0.jar
```

For production deployments, consider running the application as a systemd service or using a process manager like supervisord.

### Step 5: Verify Startup

Monitor the application logs to confirm successful startup. Look for the Spring Boot banner and the message indicating the application has started on the configured port.

```bash
tail -f /opt/bankapp/logs/application.log
```

## Post-Deployment Verification

### Health Check

Verify the application is running and healthy by calling the actuator health endpoint.

```bash
curl -u bankapp:changeit http://localhost:8989/bank-api/actuator/health
```

Expected response: `{"status":"UP"}`

### API Verification

Test the API endpoints to confirm functionality.

```bash
# Get all customers
curl -u bankapp:changeit http://localhost:8989/bank-api/customers/all

# Check API documentation is accessible
curl -I http://localhost:8989/bank-api/swagger-ui.html
```

### Database Connectivity

For environments using external databases, verify database connectivity by checking the actuator health endpoint includes database status.

```bash
curl -u bankapp:changeit http://localhost:8989/bank-api/actuator/health
```

### Functional Smoke Tests

Execute a basic smoke test to verify core functionality.

1. Create a new customer via the POST `/customers/add` endpoint
2. Retrieve the customer via GET `/customers/{customerNumber}`
3. Create an account via POST `/accounts/add/{customerNumber}`
4. Verify the account via GET `/accounts/{accountNumber}`

## Monitoring and Health Checks

### Spring Boot Actuator Endpoints

The application exposes the following actuator endpoints for monitoring:

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |

All actuator endpoints require basic authentication.

### Log Monitoring

Application logs are written to standard output by default. Configure file-based logging by adding the following to `application.yml`:

```yaml
logging:
  file:
    name: /var/log/bankapp/application.log
  level:
    com.coding.exercise.bankapp: INFO
    org.springframework: WARN
```

### JVM Monitoring

For production environments, enable JMX monitoring by adding the following JVM options:

```bash
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

### GC Logging (Java 11 Unified Logging)

Enable garbage collection logging using Java 11's unified logging syntax:

```bash
-Xlog:gc*:file=/var/log/bankapp/gc.log:time,uptime,level,tags
```

## Troubleshooting

### Application Fails to Start

If the application fails to start, check the following:

1. Verify Java 11 is installed: `java -version`
2. Check for port conflicts: `netstat -tlnp | grep 8989`
3. Review application logs for error messages
4. Ensure sufficient memory is available

### Database Connection Issues

For database connection problems:

1. Verify database server is running and accessible
2. Check connection URL, username, and password
3. Confirm network connectivity to the database server
4. Review database driver compatibility with Java 11

### Authentication Failures

If receiving 401 Unauthorized responses:

1. Verify correct credentials are being used
2. Check that Spring Security is configured correctly
3. Review security configuration in `SecurityConfig.java`

### Memory Issues

If experiencing OutOfMemoryError:

1. Increase heap size with `-Xmx` option
2. Enable GC logging to analyze memory usage patterns
3. Consider profiling the application to identify memory leaks

### TLS/SSL Issues

Java 11 enables TLS 1.3 by default. If experiencing TLS handshake failures with legacy systems:

1. Check if the remote system supports TLS 1.2 or higher
2. Temporarily force TLS 1.2 with `-Djdk.tls.client.protocols=TLSv1.2`
3. Plan to upgrade legacy systems to support modern TLS versions

## Systemd Service Configuration

For production Linux deployments, create a systemd service file at `/etc/systemd/system/bankapp.service`:

```ini
[Unit]
Description=BankApp Spring Boot Application
After=network.target

[Service]
Type=simple
User=bankapp
Group=bankapp
Environment="JAVA_HOME=/usr/lib/jvm/java-11-openjdk"
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/bankapp/bank-app-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable bankapp
sudo systemctl start bankapp
sudo systemctl status bankapp
```

## Docker Deployment (Optional)

For containerized deployments, create a Dockerfile:

```dockerfile
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/bank-app-1.0.0.jar app.jar
EXPOSE 8989
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "app.jar"]
```

Build and run the container:

```bash
docker build -t bankapp:1.0.0 .
docker run -d -p 8989:8989 --name bankapp bankapp:1.0.0
```

## Related Documentation

- [MIGRATION_NOTES.md](MIGRATION_NOTES.md) - Java 8 to 11 migration details
- [ROLLBACK.md](ROLLBACK.md) - Rollback procedures if issues occur
- [README.md](README.md) - General application documentation
