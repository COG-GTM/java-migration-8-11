# Migration Notes

## Java 8 → Java 11 (Previous Migration)

- Updated `maven.compiler.source`, `maven.compiler.target`, and `maven.compiler.release` to 11
- Added JAXB dependencies for Java 11 compatibility (JAXB was removed from JDK in Java 11)
- Added H2 and Lombok compatibility documentation

## Java 11 → Java 21 with Spring Boot 3.2.5 (Current Migration)

### Overview

Upgraded from Java 11 + Spring Boot 2.1.4 to Java 21 (LTS) + Spring Boot 3.2.5.

### Changes Made

#### Build Configuration (pom.xml)
- Spring Boot parent: `2.1.4.RELEASE` → `3.2.5`
- Java version: `11` → `21`
- `maven.compiler.release`: `11` → `21`
- Added `maven-compiler-plugin` 3.11.0, `maven-surefire-plugin` 3.2.5, `maven-failsafe-plugin` 3.2.5
- Added `maven-enforcer-plugin` 3.5.0 requiring Java 21+
- Removed JAXB dependencies (no longer needed with Jakarta EE in Spring Boot 3.x)

#### Jakarta EE Migration
- All `javax.persistence.*` imports replaced with `jakarta.persistence.*` in entity classes:
  - `Customer.java`
  - `Account.java`
  - `Transaction.java`
  - `Address.java`
  - `Contact.java`
  - `BankInfo.java`
  - `CustomerAccountXRef.java`

#### Spring Security
- Replaced deprecated `WebSecurityConfigurerAdapter` with `SecurityFilterChain` bean
- Migrated to lambda DSL for `HttpSecurity` configuration
- Updated `antMatchers()` → `requestMatchers()`

#### API Documentation
- Replaced Springfox Swagger 2 (`springfox-swagger2`, `springfox-swagger-ui` 2.9.2) with SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui` 2.3.0)
- Rewrote `ApplicationConfig` from Springfox `Docket` bean to SpringDoc `OpenAPI` bean
- Migrated controller annotations:
  - `@Api` → `@Tag`
  - `@ApiOperation` → `@Operation`
  - `@ApiResponse(code=..., message=...)` → `@ApiResponse(responseCode=..., description=...)`

#### Testing
- Migrated from JUnit 4 to JUnit 5:
  - Removed `@RunWith(SpringRunner.class)` (not needed with JUnit 5)
  - `org.junit.Test` → `org.junit.jupiter.api.Test`

#### CI/CD
- Created `.github/workflows/ci.yml` with JDK 21 (Temurin distribution)
- CI runs on push/PR to `develop` and `master` branches

### Swagger UI URL

```
http://localhost:8989/bank-api/swagger-ui/index.html
```

### Rollback Plan

If issues arise, revert to the previous commit on the `develop` branch which uses Java 11 and Spring Boot 2.1.4.
