# Migration Notes

## Current State

The BankApp now targets **Java 21 (LTS)** and **Spring Boot 3.5.3**. The project was migrated in two
phases:

1. Java 8 -> Java 11 (Spring Boot 2.1.4 -> 2.7.18), documented in Part 2 below.
2. Java 11 -> Java 21 (Spring Boot 2.7.18 -> 3.5.3), documented in Part 1 below.

---

# Part 1: Java 11 / Spring Boot 2.7 to Java 21 / Spring Boot 3 Migration Notes

## Overview

Spring Boot 2.7.x does not officially support Java 21, so running on Java 21 required upgrading to
Spring Boot 3.x (which requires Java 17+) and performing the associated breaking-change migrations
(Jakarta EE 9+ namespace, Spring Security 6, Spring Framework 6, Hibernate 6, SpringDoc 2).

## Changes Made

### 1. Build Configuration Updates (`pom.xml`)

- `spring-boot-starter-parent`: `2.7.18` -> `3.5.3`
- `java.version` and `maven.compiler.release`: `11` -> `21`
- `maven-compiler-plugin` `<release>`: `11` -> `21`
- `maven-enforcer-plugin` `requireJavaVersion`: `[11,)` -> `[21,)`
- `org.springdoc:springdoc-openapi-ui:1.6.15` -> `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9`
  (the 1.x artifact only supports Spring Boot 2.x / `javax.*`)
- Removed `org.glassfish.jaxb:jaxb-runtime:2.3.8`. It targeted the `javax.xml.bind` API, which is
  incompatible with Jakarta EE 9+. The application does not use JAXB directly and Spring Boot 3
  manages `jaxb-runtime` 4.x if a dependency needs it, so no replacement was required. Compilation
  and runtime were verified without it.

### 2. `javax.*` to `jakarta.*` Namespace Migration

All JPA entities under `src/main/java/com/coding/exercise/bankapp/model/` (`Account`, `Address`,
`BankInfo`, `Contact`, `Customer`, `CustomerAccountXRef`, `Transaction`) now import
`jakarta.persistence.*` instead of `javax.persistence.*`. No `javax.validation`, `javax.servlet`
or `javax.annotation` imports existed in main or test sources.

### 3. Spring Security 6 Configuration

`WebSecurityConfigurerAdapter` was removed in Spring Security 6. `SecurityConfig` was rewritten to
expose a `SecurityFilterChain` bean using the lambda DSL:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return httpSecurity.build();
    }
}
```

Behavior is preserved: `/` and `/h2-console/**` are permitted, CSRF is disabled and frame options
are disabled (required for the H2 console). `anyRequest().permitAll()` is required because
`authorizeHttpRequests` must cover every request; the previous `authorizeRequests()` configuration
only declared the two matchers and did not enforce authentication on other endpoints, so this keeps
the pre-migration behavior (for example `/customers/all` returns 200 without credentials).

### 4. Other API Changes Reviewed

- `ApplicationConfig` (SpringDoc `OpenAPI` bean) is source-compatible with SpringDoc 2.x.
- Controllers use `io.swagger.v3.oas.annotations.*`, which are unchanged in SpringDoc 2.x.
- Repositories use Spring Data `CrudRepository`/`JpaRepository` methods that are unchanged.
- Tests use JUnit 5 (`org.junit.jupiter.api.Test`) and `@SpringBootTest`, which are unchanged.
- No usages of Spring Framework 6 removed APIs were found in the codebase.

### 5. CI/CD Updates

`.github/workflows/ci.yml` now sets up JDK 21 (Temurin).

## Verification Results (Java 21 / Spring Boot 3.5.3)

**Java Runtime Used**:
```
openjdk version "21.0.12" 2026-07-21
OpenJDK Runtime Environment (build 21.0.12+8-1-22.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12+8-1-22.04-Ubuntu, mixed mode, sharing)
```

- `mvn clean test` and `mvn -DskipTests clean verify` succeed on Java 21
- Spring Boot 3.5.3, Hibernate ORM 6.6.18.Final, H2 2.3.232
- Application starts in ~3.2 seconds with no ERROR log entries

| Endpoint | Status |
|----------|--------|
| `/actuator/health` | 200 OK `{"status":"UP"}` |
| `/customers/all` | 200 OK |
| `POST /customers/add` | 200 OK ("New Customer created successfully.") |
| `POST /accounts/add/{customerNumber}` | 200 OK ("New Account created successfully.") |
| `GET /accounts/{accountNumber}` | 200 OK |
| `/swagger-ui.html` | 302 -> `/swagger-ui/index.html` (200) |
| `/v3/api-docs` | 200 OK |
| `/h2-console/` | 200 OK |

## Rollback Plan

Revert the Spring Boot 3 migration commit(s): restore `spring-boot-starter-parent` 2.7.18, Java 11
properties, `springdoc-openapi-ui` 1.6.15, `jaxb-runtime` 2.3.8, `javax.persistence` imports, the
`WebSecurityConfigurerAdapter`-based `SecurityConfig`, and JDK 11 in the CI workflow.

---

# Part 2: Java 8 to 11 Migration Notes (Historical)

## Overview

This document summarizes the changes made to migrate the BankApp from Java 8 to Java 11 (LTS).
These notes are retained for history; the project has since moved on to Java 21 / Spring Boot 3
(see Part 1).

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

The Java 11 migration was completed and later superseded by the Java 21 / Spring Boot 3 migration
described in Part 1.
