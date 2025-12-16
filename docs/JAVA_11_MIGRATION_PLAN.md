# Java 8 to Java 11 Migration Plan

## Banking Application Migration Guide

This document provides a comprehensive migration plan for upgrading the banking application from Java 8 to Java 11. The migration involves 5 major areas that need to be addressed to ensure full compatibility and take advantage of Java 11 features.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Step-by-Step Changes](#step-by-step-changes)
   - [Build Configuration Updates](#1-build-configuration-updates-pomxml)
   - [JAXB Runtime Dependency](#2-jaxb-runtime-dependency)
   - [SpringDoc OpenAPI Migration](#3-springdoc-openapi-migration)
   - [Testing Framework Migration](#4-testing-framework-migration-to-junit-5)
   - [Maven Plugin Updates](#5-maven-plugin-updates)
4. [Rationale](#rationale)
5. [Verification Steps](#verification-steps)
6. [Rollback Plan](#rollback-plan)

## Overview

This migration plan outlines the necessary changes to upgrade the banking application from Java 8 to Java 11. Java 11 is a Long-Term Support (LTS) release that provides improved performance, new language features, and better security. However, several breaking changes between Java 8 and Java 11 require updates to our build configuration, dependencies, and code.

The migration touches the following areas:

- **Build Configuration**: Update Maven compiler settings for Java 11
- **JAXB Dependency**: Add external JAXB runtime since it was removed from the JDK
- **API Documentation**: Migrate from deprecated Springfox to SpringDoc OpenAPI
- **Testing Framework**: Upgrade from JUnit 4 to JUnit 5 (Jupiter)
- **Maven Plugins**: Update plugin versions for Java 11 compatibility

## Prerequisites

Before starting the migration, ensure the following requirements are met:

### Java 11 JDK Installation

1. Download and install Java 11 JDK from one of the following sources:
   - [Oracle JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (requires license for production)
   - [OpenJDK 11](https://adoptium.net/) (recommended, free and open source)
   - [Amazon Corretto 11](https://aws.amazon.com/corretto/) (free, production-ready)

2. Verify the installation:
   ```bash
   java -version
   # Expected output: openjdk version "11.x.x" or similar
   
   javac -version
   # Expected output: javac 11.x.x
   ```

3. Set `JAVA_HOME` environment variable:
   ```bash
   # Linux/macOS
   export JAVA_HOME=/path/to/jdk-11
   
   # Windows
   set JAVA_HOME=C:\path\to\jdk-11
   ```

### Development Environment

- Maven 3.6.0 or higher (recommended: 3.8.x or 3.9.x)
- IDE with Java 11 support (IntelliJ IDEA 2019.1+, Eclipse 2019-03+, VS Code with Java Extension Pack)
- Git for version control

## Step-by-Step Changes

### 1. Build Configuration Updates (pom.xml)

Update the Maven build configuration to target Java 11.

**File**: `pom.xml`

#### Java Version Properties (lines 17-21)

Update the properties section to specify Java 11:

**Before (Java 8)**:
```xml
<properties>
    <java.version>8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**After (Java 11)**:
```xml
<properties>
    <java.version>11</java.version>
    <maven.compiler.release>11</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

#### Maven Compiler Plugin (lines 81-91)

Update the Maven Compiler Plugin to version 3.11.0 and configure it for Java 11:

**Before**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```

**After**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <release>11</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

**Note**: The `<release>` configuration replaces the separate `<source>` and `<target>` settings and ensures consistent compilation across different JDK versions.

### 2. JAXB Runtime Dependency

JAXB (Java Architecture for XML Binding) was included in the JDK until Java 8 but was deprecated in Java 9 and removed in Java 11. If your application uses XML binding, you must add the JAXB runtime as an external dependency.

**File**: `pom.xml`

**Location**: Add after the existing dependencies (around line 71-75)

```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.8</version>
</dependency>
```

This dependency provides the JAXB implementation that was previously bundled with the JDK. Version 2.3.8 is compatible with Java 11 and provides all the necessary XML binding functionality.

### 3. SpringDoc OpenAPI Migration

Springfox Swagger is deprecated and incompatible with Spring Boot 2.6+. The recommended replacement is SpringDoc OpenAPI, which provides better support for modern Spring Boot versions and Java 11.

#### Dependency Update

**File**: `pom.xml`

**Location**: Replace Springfox dependencies (around lines 57-60)

**Before (Springfox)**:
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

**After (SpringDoc)**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>
```

#### Controller Annotations Update

**File**: `src/main/java/com/coding/exercise/bankapp/controller/AccountController.java`

**Before (Springfox annotations)**:
```java
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("accounts")
@Api(value = "Accounts and Transactions REST endpoints")
public class AccountController {

    @GetMapping(path = "/{accountNumber}")
    @ApiOperation(value = "Get account details", notes = "Find account details by account number")
    public ResponseEntity<Object> getByAccountNumber(@PathVariable Long accountNumber) {
        // ...
    }
}
```

**After (SpringDoc annotations)** (lines 20-34):
```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("accounts")
@Tag(name = "Accounts and Transactions REST endpoints")
public class AccountController {

    @GetMapping(path = "/{accountNumber}")
    @Operation(summary = "Get account details", description = "Find account details by account number")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error") 
    })
    public ResponseEntity<Object> getByAccountNumber(@PathVariable Long accountNumber) {
        // ...
    }
}
```

#### Configuration Update

**File**: `src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java`

**Before (Springfox Docket)**:
```java
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
```

**After (SpringDoc OpenAPI)** (lines 3-19):
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class ApplicationConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BANKING APPLICATION REST API")
                        .description("API for Banking Application.")
                        .version("1.0.0"));
    }
}
```

**Note**: The `@EnableSwagger2` annotation is no longer needed with SpringDoc. The Swagger UI will be available at `/swagger-ui.html` or `/swagger-ui/index.html`.

### 4. Testing Framework Migration to JUnit 5

JUnit 5 (Jupiter) provides better support for Java 11 features and is the default testing framework in modern Spring Boot versions. The `spring-boot-starter-test` dependency already includes JUnit 5 by default.

**File**: `src/test/java/com/coding/exercise/bankapp/BankingApplicationTests.java`

**Before (JUnit 4)**:
```java
package com.coding.exercise.bankapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankingApplicationTests {

    @Test
    public void contextLoads() {
    }
}
```

**After (JUnit 5)** (lines 1-13):
```java
package com.coding.exercise.bankapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BankingApplicationTests {

    @Test
    public void contextLoads() {
    }
}
```

**Key Changes**:
- Import changed from `org.junit.Test` to `org.junit.jupiter.api.Test` (line 3)
- Removed `@RunWith(SpringRunner.class)` annotation (not needed with JUnit 5)
- The `@Test` annotation now comes from JUnit 5 (line 9)

### 5. Maven Plugin Updates

Update Maven plugins to versions that fully support Java 11.

**File**: `pom.xml`

#### Surefire Plugin (line 94-96)

Update to version 3.2.5 for proper JUnit 5 and Java 11 support:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

#### Failsafe Plugin (lines 97-101)

Update to version 3.2.5 for integration test support:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

#### Enforcer Plugin (lines 102-118)

Update to version 3.5.0 and add Java version enforcement:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <goals><goal>enforce</goal></goals>
            <configuration>
                <rules>
                    <requireJavaVersion>
                        <version>[11,)</version>
                    </requireJavaVersion>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This configuration ensures that the build fails if someone attempts to build with a Java version lower than 11.

#### Javadoc Plugin (lines 119-126)

Update to version 3.6.3 for Java 11 compatibility:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
        <additionalJOption>-Xdoclint:none</additionalJOption>
    </configuration>
</plugin>
```

## Rationale

### Why Java 11?

Java 11 is a Long-Term Support (LTS) release that provides several benefits over Java 8:

1. **Performance Improvements**: Java 11 includes the G1 garbage collector as default, providing better performance for most applications.
2. **New Language Features**: Local variable type inference (`var`), new String methods, and improved APIs.
3. **Security Updates**: Java 11 includes important security patches and improvements.
4. **Long-Term Support**: Oracle provides extended support for Java 11 until at least 2026.

### Why JAXB as External Dependency?

JAXB was part of Java EE, which was deprecated in Java 9 and removed in Java 11 as part of the modularization effort (Project Jigsaw). The `jaxb-runtime` dependency provides the same functionality as an external library, ensuring backward compatibility for applications that rely on XML binding.

### Why SpringDoc over Springfox?

Springfox Swagger has several issues with modern Spring Boot versions:

1. **Incompatibility**: Springfox is incompatible with Spring Boot 2.6+ due to changes in Spring MVC path matching.
2. **Maintenance**: Springfox is no longer actively maintained.
3. **OpenAPI 3.0**: SpringDoc supports OpenAPI 3.0 specification, which is the current standard.
4. **Better Integration**: SpringDoc integrates seamlessly with Spring Boot auto-configuration.

### Why JUnit 5?

JUnit 5 provides several advantages over JUnit 4:

1. **Better Java 11 Support**: JUnit 5 is designed to work with modern Java versions.
2. **Improved Architecture**: Modular architecture with separate components for different testing needs.
3. **New Features**: Parameterized tests, nested tests, and better extension model.
4. **Spring Boot Default**: JUnit 5 is the default testing framework in Spring Boot 2.2+.

### Why Update Maven Plugins?

Older Maven plugin versions may not fully support Java 11 features or may have bugs when running on Java 11. Updated plugins ensure:

1. **Compatibility**: Full support for Java 11 bytecode and features.
2. **Bug Fixes**: Important fixes for issues discovered in older versions.
3. **Performance**: Improved build performance with newer plugin versions.

## Verification Steps

After completing the migration, verify each change works correctly:

### 1. Verify Java Version

```bash
# Check Java version
java -version
# Should show Java 11.x.x

# Verify JAVA_HOME
echo $JAVA_HOME
# Should point to Java 11 installation
```

### 2. Clean Build

```bash
# Clean and build the project
mvn clean install

# Expected: BUILD SUCCESS
# The enforcer plugin should verify Java 11 is being used
```

### 3. Run Tests

```bash
# Run all tests
mvn test

# Expected: All tests pass
# Verify JUnit 5 is being used (check test output format)
```

### 4. Verify Application Startup

```bash
# Start the application
mvn spring-boot:run

# Expected: Application starts without errors
# Check logs for any deprecation warnings
```

### 5. Verify Swagger UI

1. Start the application
2. Navigate to `http://localhost:8989/bank-api/swagger-ui.html`
3. Verify the API documentation is displayed correctly
4. Test at least one endpoint through the Swagger UI

### 6. Verify H2 Console

1. Navigate to `http://localhost:8989/bank-api/h2-console/`
2. Connect using JDBC URL: `jdbc:h2:mem:testdb`
3. Verify database tables are created correctly

### 7. Test API Endpoints

```bash
# Test customer endpoint
curl -u bankapp:changeit http://localhost:8989/bank-api/customers/all

# Test account endpoint (replace {accountNumber} with actual value)
curl -u bankapp:changeit http://localhost:8989/bank-api/accounts/{accountNumber}
```

### 8. Verify JAXB Functionality

If your application uses XML binding, test XML serialization/deserialization to ensure JAXB is working correctly.

## Rollback Plan

If issues are encountered during or after the migration, follow these steps to rollback:

### Immediate Rollback (Git)

If you haven't committed the changes yet:

```bash
# Discard all changes
git checkout -- .

# Or discard specific files
git checkout -- pom.xml
git checkout -- src/main/java/com/coding/exercise/bankapp/controller/AccountController.java
git checkout -- src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java
git checkout -- src/test/java/com/coding/exercise/bankapp/BankingApplicationTests.java
```

### Rollback After Commit

If changes have been committed:

```bash
# Revert the migration commit
git revert <commit-hash>

# Or reset to previous commit (use with caution)
git reset --hard <previous-commit-hash>
```

### Manual Rollback Steps

If you need to manually rollback specific changes:

#### 1. Revert pom.xml

Change Java version properties back to Java 8:
```xml
<properties>
    <java.version>8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

Remove the `maven.compiler.release` property and update compiler plugin:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```

#### 2. Remove JAXB Dependency

Remove the `jaxb-runtime` dependency from pom.xml (it's included in Java 8).

#### 3. Revert to Springfox

Replace SpringDoc dependency with Springfox dependencies and revert controller annotations.

#### 4. Revert to JUnit 4

Change test imports back to JUnit 4 and add `@RunWith(SpringRunner.class)` annotation.

#### 5. Revert Maven Plugin Versions

Downgrade plugin versions to Java 8 compatible versions and remove Java version enforcement.

### Environment Rollback

Ensure your environment is configured for Java 8:

```bash
# Set JAVA_HOME to Java 8
export JAVA_HOME=/path/to/jdk-8

# Verify
java -version
# Should show Java 1.8.x
```

## Summary

This migration plan covers all necessary changes to upgrade the banking application from Java 8 to Java 11. The key changes are:

1. **Build Configuration**: Updated Java version properties and Maven compiler plugin
2. **JAXB Dependency**: Added external JAXB runtime to replace removed JDK module
3. **API Documentation**: Migrated from Springfox to SpringDoc OpenAPI
4. **Testing Framework**: Upgraded from JUnit 4 to JUnit 5
5. **Maven Plugins**: Updated all plugins to Java 11 compatible versions

Following this plan ensures a smooth migration while maintaining backward compatibility and taking advantage of Java 11 improvements.

## References

- [Oracle Java 11 Migration Guide](https://docs.oracle.com/en/java/javase/11/migrate/index.html)
- [Spring Boot 2.7 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)
