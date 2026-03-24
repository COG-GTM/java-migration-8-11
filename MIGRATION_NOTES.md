# Java 8 to 11 Migration Notes

## Overview

This document summarizes the changes made to migrate the BankApp from Java 8 to Java 11 (LTS).

## Changes Made

### 1. Build Configuration Updates

**Maven Configuration (`pom.xml`)**:
- Updated `java.version` from `1.8` to `11`
- Added `maven.compiler.release` property set to `11`
- Added `project.build.sourceEncoding` set to `UTF-8`
- Upgraded Maven plugins to Java 11-compatible versions:
  - `maven-compiler-plugin`: 3.11.0 with `<release>11</release>`
  - `maven-surefire-plugin`: 3.2.5
  - `maven-failsafe-plugin`: 3.2.5
  - `maven-enforcer-plugin`: 3.5.0 with Java 11+ requirement
  - `maven-javadoc-plugin`: 3.6.3

### 2. Dependencies for Removed JDK Modules

**JAXB Runtime**:
- Added `org.glassfish.jaxb:jaxb-runtime:2.3.1` dependency
- Spring Boot already includes JAXB API and activation API as transitive dependencies
- No code changes required as Spring Boot handles JAXB integration

### 3. Source Code Changes

**Swagger Migration (Springfox to SpringDoc OpenAPI)**:
- Replaced `io.springfox` dependencies with `org.springdoc:springdoc-openapi-ui:1.6.15`
- Updated controller annotations from Springfox (`@Api`, `@ApiOperation`) to SpringDoc (`@Tag`, `@Operation`)
- Rewrote `ApplicationConfig.java` to use SpringDoc configuration instead of Springfox Docket

**Test Framework Migration (JUnit 4 to JUnit 5)**:
- Updated test classes to use JUnit 5 annotations (`@Test` from `org.junit.jupiter.api`)
- Spring Boot 2.7.x includes JUnit 5 by default via `spring-boot-starter-test`

### 4. CI/CD Updates

**GitHub Actions**:
- Created workflow file `.github/workflows/ci.yml` for Java 11 builds
- Configured to use JDK 11 with Temurin distribution
- Added Maven caching for improved build performance
- Runs compile, test, and verify steps on push and pull requests

### 4. Runtime Environment

**Java Version**:
- Application now runs on OpenJDK 11 (Temurin distribution)
- No illegal reflective access warnings observed
- All tests pass with same functionality as Java 8 baseline

## Verification Results

### Build and Test Status
- Maven compilation successful with Java 11
- All unit tests pass
- Spring Boot application starts correctly
- H2 database integration working
- API documentation accessible
- Spring Security configuration functional

### Application Startup Verification (MBA-782)

**Verification Date**: December 14, 2025

**Java Runtime Used**:
```
openjdk version "11.0.29" 2025-10-21
OpenJDK Runtime Environment (build 11.0.29+7-post-Ubuntu-1ubuntu122.04)
OpenJDK 64-Bit Server VM (build 11.0.29+7-post-Ubuntu-1ubuntu122.04, mixed mode, sharing)
```

**Startup Command**: `java -jar target/bank-app-1.0.0.jar`

**Startup Results**:
- Application started successfully in approximately 5.08 seconds
- Tomcat initialized on port 8989 with context path '/bank-api'
- Spring Boot version: 2.7.18
- Hibernate ORM version: 5.6.15.Final
- H2 database console available at '/h2-console'
- Spring Security filter chain configured correctly
- Actuator endpoint exposed at '/actuator'

**Startup Log Analysis**:
- No ERROR level messages in startup logs
- One WARN message about `spring.jpa.open-in-view` being enabled by default (expected, non-critical)
- All Spring Data JPA repositories bootstrapped successfully (4 repositories found)
- HikariCP connection pool started successfully
- JPA EntityManagerFactory initialized for persistence unit 'default'

**Service Verification**:
| Endpoint | Status | Response |
|----------|--------|----------|
| `/actuator/health` | 200 OK | `{"status":"UP"}` |
| `/actuator` | 200 OK | Links to health endpoints |
| `/customers/all` | 200 OK | Empty array (expected) |
| `/swagger-ui.html` | 302 Redirect | Redirects to Swagger UI |
| `/h2-console/` | 200 OK | H2 Console HTML page |

**Conclusion**: The application starts without errors on Java 11 and all services are running correctly.

### Performance and Compatibility
- No illegal reflective access warnings
- JAXB functionality working with added runtime dependency
- Default G1 garbage collector (Java 11 default) performing well
- TLS 1.3 support enabled by default

## Java 11 Benefits Gained

1. **Performance**: G1 garbage collector improvements and general JVM optimizations
2. **Security**: TLS 1.3 support and updated security algorithms
3. **Language Features**: Ready for future adoption of Java 9-11 language features
4. **Long-term Support**: Java 11 LTS provides extended support lifecycle

## Areas With Minimal Changes

The following areas required no or minimal modifications:
- **TLS Configuration**: Application uses Spring Boot defaults, no custom TLS setup
- **GC Logging**: No custom GC logging was configured, using Java 11 defaults
- **Module System**: Staying on classpath (not adopting JPMS modules)

## Future Considerations

1. **Optional Modernizations** (future PRs):
   - Adopt `var` keyword for local variables (Java 10+)
   - Use new HTTP Client API (Java 11+) if external HTTP calls are added
   - Consider adopting Java modules (JPMS) if project grows

2. **Monitoring**:
   - Monitor application performance in production
   - Watch for any TLS compatibility issues with external services (if added)

## Rollback Plan

If rollback to Java 8 is needed:
1. Revert `pom.xml` changes (set `java.version` back to `1.8`)
2. Remove JAXB runtime dependency
3. Update CI workflow to use Java 8
4. Revert Maven plugin versions if needed

## Migration Completion

- Java 11 build configuration
- Dependencies for removed JDK modules
- CI/CD updated to Java 11
- All tests passing
- Documentation updated
- Migration notes created

The migration is complete and the application is ready for production deployment on Java 11.

---

# Java 11 to Java 18 / Spring Boot 2.7.18 to 3.2.5 Migration Notes

## Overview

This section documents the migration from Java 11 / Spring Boot 2.7.18 to Java 18 / Spring Boot 3.2.5. This is a major migration involving namespace changes, security rewrites, and dependency upgrades.

## Changes Made

### 1. Build Configuration (`pom.xml`)

- **Spring Boot Parent**: `2.7.18` → `3.2.5`
- **Java Version**: `11` → `18`
- **maven.compiler.release**: `11` → `18`
- **maven-compiler-plugin release**: `11` → `18`
- **maven-enforcer-plugin requireJavaVersion**: `[11,)` → `[18,)`

### 2. javax → jakarta Namespace Migration

Spring Boot 3.x requires Jakarta EE 9+ which uses the `jakarta.*` namespace instead of `javax.*`. All JPA entity imports were updated:

**Files Updated (7 model files)**:
- `model/Customer.java`
- `model/Account.java`
- `model/Transaction.java`
- `model/Address.java`
- `model/Contact.java`
- `model/BankInfo.java`
- `model/CustomerAccountXRef.java`

**Example Change**:
```java
// Before
import javax.persistence.Entity;
import javax.persistence.Id;

// After
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
```

### 3. Spring Security Configuration Rewrite

Spring Security 6.x (included in Spring Boot 3.2.x) removes `WebSecurityConfigurerAdapter`. The `SecurityConfig` class was completely rewritten:

**Key Changes**:
- Removed `extends WebSecurityConfigurerAdapter`
- Added `@EnableWebSecurity` annotation
- Replaced `configure(HttpSecurity)` override with `@Bean SecurityFilterChain`
- `.antMatchers()` → `.requestMatchers()`
- `.authorizeRequests()` → `.authorizeHttpRequests()`
- Adopted lambda-style DSL (e.g., `.csrf(csrf -> csrf.disable())`)

### 4. SpringDoc OpenAPI 1.x → 2.x

- **Removed**: `org.springdoc:springdoc-openapi-ui:1.6.15`
- **Added**: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0`

SpringDoc 2.x is required for Spring Boot 3.x compatibility. The `ApplicationConfig.java` and controller annotations (`@Tag`, `@Operation`, `@ApiResponse`) did not require changes as they use `io.swagger.v3.oas` which is the same in both versions.

### 5. JAXB Runtime 2.x → 4.x

- **Updated**: `org.glassfish.jaxb:jaxb-runtime` from `2.3.8` to `4.0.4`

JAXB 4.x aligns with the Jakarta EE namespace migration (jakarta.xml.bind).

### 6. CI/CD Updates

- **GitHub Actions**: Updated JDK version from `11` to `18` with Temurin distribution in `.github/workflows/ci.yml`

## Files NOT Changed

The following files required no modifications:
- `ApplicationConfig.java` — already uses `io.swagger.v3.oas.models.OpenAPI` (compatible with SpringDoc 2.x)
- Controller annotations (`@Tag`, `@Operation`, `@ApiResponse`) — same in SpringDoc 2.x
- `BankingApplicationTests.java` — already on JUnit 5
- `application.yml` — no changes needed
- Domain/DTO classes — no `javax` imports

## Notes

- Java 18 is NOT an LTS release. Consider upgrading to Java 21 (LTS) in the future — the migration steps would be identical.
- Spring Boot 3.2.x requires a minimum of Java 17.
