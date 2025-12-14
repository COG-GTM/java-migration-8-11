# Java 8 to Java 11 Migration Notes

## Overview

This document summarizes the migration of the BankApp Spring Boot application from Java 8 to Java 11.

## Build Configuration Changes

### pom.xml Updates

The following changes were made to support Java 11:

**Properties:**
- `java.version`: Changed from `1.8` to `11`
- Added `maven.compiler.release`: `11`
- Added `project.build.sourceEncoding`: `UTF-8`

**Plugin Management:**
- `maven-compiler-plugin`: Version 3.11.0 with `<release>11</release>` configuration
- `maven-surefire-plugin`: Version 3.2.5
- `maven-failsafe-plugin`: Version 3.2.5
- `maven-javadoc-plugin`: Version 3.6.3 with `-Xdoclint:none` option

**Enforcer Plugin:**
- Added `maven-enforcer-plugin` version 3.5.0 to require Java 11 or higher

## Build Process

### Prerequisites
- JDK 11 (OpenJDK 11 or Temurin 11 recommended)
- Maven 3.6.3 or higher

### Build Commands

```bash
# Set Java 11 environment
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
java -version

# Build the project
mvn clean package
```

### Build Verification

The build was verified with the following results:

- **Build Status**: SUCCESS
- **Tests**: All tests passed (1 test executed)
- **JAR Generated**: `target/bank-app-1.0.0.jar` (47MB)
- **Build JDK**: 11.0.29

### JAR Manifest

The generated JAR manifest confirms Java 11 compilation:

```
Manifest-Version: 1.0
Created-By: Apache Maven 3.6.3
Built-By: ubuntu
Build-Jdk: 11.0.29
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.coding.exercise.bankapp.BankingApplication
Spring-Boot-Version: 2.1.4.RELEASE
```

### Running the Application

```bash
# Run with Java 11
java -jar target/bank-app-1.0.0.jar

# Or with custom port
java -jar target/bank-app-1.0.0.jar --server.port=8990
```

The application starts successfully on the configured port with context path `/bank-api`.

## Compatibility Notes

### Spring Boot Version
- The application uses Spring Boot 2.1.4.RELEASE which is compatible with Java 11
- No Spring Boot version upgrade was required for this migration

### Dependencies
- All existing dependencies (Lombok, Springfox Swagger, H2, etc.) are compatible with Java 11
- No additional dependencies were required for removed JDK modules (JAXB, etc.) as the application does not use them

### No Breaking Changes
- No code changes were required
- No illegal reflective access warnings observed
- All tests pass without modification

## Validation Checklist

- [x] Build succeeds with Java 11
- [x] All tests pass
- [x] JAR generated correctly
- [x] JAR manifest shows Java 11
- [x] Application starts and runs correctly
- [x] No illegal reflective access warnings
- [x] Enforcer plugin validates Java 11 requirement

## Related Jira Task

- **Task**: MBA-781
- **Epic**: Fase 6: Despliegue (MBA-764)
