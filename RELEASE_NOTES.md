# Release Notes - BankApp v1.0.0 (Java 17 Migration)

## Release Date
January 2026

## Overview
This release completes the migration of the BankApp from Java 8/11 to Java 17 with Spring Boot 3.x. The application is now fully compatible with modern Java runtime environments and uses the latest Jakarta EE specifications.

## Major Changes

### Java Version Upgrade
- **From**: Java 8/11
- **To**: Java 17 (LTS)
- **Maven Compiler**: Updated to use `release` flag for Java 17

### Spring Boot Upgrade
- **From**: Spring Boot 2.x
- **To**: Spring Boot 3.2.0
- Includes Spring Framework 6.x and Spring Security 6.x

### Jakarta EE Migration (javax → jakarta)
All Java EE packages have been migrated to Jakarta EE namespace:

| Old Package | New Package |
|-------------|-------------|
| `javax.persistence.*` | `jakarta.persistence.*` |
| `javax.validation.*` | `jakarta.validation.*` |
| `javax.xml.bind.*` | `jakarta.xml.bind.*` |
| `javax.annotation.*` | `jakarta.annotation.*` |

### Spring Security Configuration
- Removed deprecated `WebSecurityConfigurerAdapter`
- Implemented new `SecurityFilterChain` bean-based configuration
- Updated to use `authorizeHttpRequests()` instead of `authorizeRequests()`
- Updated to use `requestMatchers()` instead of `antMatchers()`

### API Documentation Migration
- **From**: Springfox Swagger 2.x
- **To**: SpringDoc OpenAPI 2.3.0
- New Swagger UI URL: `/bank-api/swagger-ui/index.html`
- OpenAPI 3.0 specification: `/bank-api/v3/api-docs`

### Annotation Changes
| Old Annotation | New Annotation |
|----------------|----------------|
| `@Api` | `@Tag` |
| `@ApiOperation` | `@Operation` |
| `@ApiResponse(code=...)` | `@ApiResponse(responseCode=...)` |
| `@EnableSwagger2` | Removed (auto-configured) |

### Test Framework
- Migrated from JUnit 4 to JUnit 5 (Jupiter)
- Removed `@RunWith(SpringRunner.class)` annotation
- Updated test imports to `org.junit.jupiter.api.*`

## Environment Requirements

### Production Environment
- **Java**: JDK 17 or higher
- **JAVA_HOME**: Must point to JDK 17 installation
- **JVM Flags**: No special flags required

### Build Requirements
- **Maven**: 3.8.x or higher
- **Java**: JDK 17 for compilation

## Running the Application

### Build
```bash
export JAVA_HOME=/path/to/jdk17
mvn clean package
```

### Run
```bash
java -jar target/bank-app-1.0.0.jar
```

### Access Points
- **Application**: http://localhost:8989/bank-api/
- **Swagger UI**: http://localhost:8989/bank-api/swagger-ui/index.html
- **H2 Console**: http://localhost:8989/bank-api/h2-console/
- **Health Check**: http://localhost:8989/bank-api/actuator/health

## Breaking Changes
- Applications using Java 8 or 11 must upgrade to Java 17
- Any code referencing `javax.*` packages must be updated to `jakarta.*`
- Swagger annotations must be updated to SpringDoc OpenAPI annotations

## Dependencies

### Updated Dependencies
| Dependency | Old Version | New Version |
|------------|-------------|-------------|
| Spring Boot | 2.7.x | 3.2.0 |
| Spring Security | 5.x | 6.x |
| Hibernate | 5.x | 6.x |
| Jakarta Persistence API | - | 3.1.0 |
| SpringDoc OpenAPI | - | 2.3.0 |
| JAXB Runtime | 2.3.x | 4.0.3 |

## Known Issues
None at this time.

## Contributors
- Migration completed by Devin AI
