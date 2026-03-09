# Java 17 Migration Analysis

**Project:** BankApp (java-migration-8-11)
**Analysis Date:** March 9, 2026
**Current Java Version:** 11
**Target Java Version:** 17 (LTS)
**Current Spring Boot Version:** 2.7.18
**Target Spring Boot Version:** 3.2.5
**JIRA Epic:** MBA-1031

## Executive Summary

This document provides a comprehensive analysis for migrating
the BankApp project from Java 11 to Java 17. Because
Spring Boot 2.x does not fully support Java 17 features,
upgrading to Spring Boot 3.2.x is required. This in turn
mandates the Jakarta EE 9+ namespace migration
(`javax.*` to `jakarta.*`), a Spring Security refactor, and
a SpringDoc OpenAPI major version bump. The codebase is
small and well-structured, making the migration
straightforward but requiring careful attention to every
layer.

**Key Findings:**

- 47 `javax.persistence.*` imports across 7 entity files
  must migrate to `jakarta.persistence.*`
- `SecurityConfig` extends the removed
  `WebSecurityConfigurerAdapter` and must be rewritten
- SpringDoc OpenAPI must upgrade from 1.6.x to 2.x
- JAXB runtime must upgrade from 2.3.x to 4.x
- H2, Lombok, and test dependencies are compatible with
  minor version bumps
- CI pipeline (`ci.yml`) must update from JDK 11 to JDK 17

**Overall Migration Risk: MEDIUM**

The migration is well-scoped. The highest risk items are
the `javax` to `jakarta` namespace change (broad but
mechanical) and the Spring Security rewrite (behavioral).

---

## 1. Current Project State

### 1.1 Build Tool and Configuration

| Component | Current Value |
|-----------|---------------|
| Build Tool | Maven 3.x (with wrapper) |
| Java Version | 11 |
| Spring Boot | 2.7.18 |
| Packaging | JAR |
| Context Path | `/bank-api` |
| Port | 8989 |

### 1.2 Dependency Inventory

| Dependency | Current Version | Source |
|------------|----------------|--------|
| spring-boot-starter-parent | 2.7.18 | parent POM |
| spring-boot-starter-actuator | managed | parent |
| spring-boot-starter-data-jpa | managed | parent |
| spring-boot-starter-security | managed | parent |
| spring-boot-starter-web | managed | parent |
| spring-boot-devtools | managed | parent |
| spring-boot-starter-test | managed | parent |
| spring-security-test | managed | parent |
| H2 Database | managed | parent |
| Lombok | managed (optional) | parent |
| springdoc-openapi-ui | 1.6.15 | explicit |
| jaxb-runtime (Glassfish) | 2.3.8 | explicit |
| maven-compiler-plugin | 3.11.0 | pluginMgmt |
| maven-surefire-plugin | 3.2.5 | pluginMgmt |
| maven-failsafe-plugin | 3.2.5 | pluginMgmt |
| maven-enforcer-plugin | 3.5.0 | pluginMgmt |
| maven-javadoc-plugin | 3.6.3 | pluginMgmt |

### 1.3 Project Structure

```
src/main/java/com/coding/exercise/bankapp/
  BankingApplication.java          (entry point)
  config/
    ApplicationConfig.java         (OpenAPI bean)
    SecurityConfig.java            (WebSecurityConfigurerAdapter)
  controller/
    AccountController.java         (REST - accounts)
    CustomerController.java        (REST - customers)
  domain/                          (7 DTO classes)
  model/                           (7 JPA entity classes)
  repository/                      (4 Spring Data interfaces)
  service/
    BankingService.java            (interface)
    BankingServiceImpl.java        (implementation)
    helper/
      BankingServiceHelper.java    (entity/DTO mapper)
```

---

## 2. javax.* to jakarta.* Namespace Audit

Spring Boot 3.x uses Jakarta EE 10, which renamed all
`javax.*` packages to `jakarta.*`. Every `javax.persistence`
import in the codebase must change.

### 2.1 Affected Files and Import Counts

| File | javax Imports | Annotations Used |
|------|--------------|------------------|
| `model/Account.java` | 9 | @Entity, @Id, @GeneratedValue, @Column, @OneToOne, @Temporal, CascadeType, GenerationType, TemporalType |
| `model/Customer.java` | 9 | @Entity, @Id, @GeneratedValue, @Column, @ManyToOne, @OneToOne, @Temporal, CascadeType, TemporalType |
| `model/BankInfo.java` | 7 | @Entity, @Id, @GeneratedValue, @Column, @OneToOne, CascadeType, GenerationType |
| `model/Transaction.java` | 7 | @Entity, @Id, @GeneratedValue, @Column, @Temporal, GenerationType, TemporalType |
| `model/Address.java` | 5 | @Entity, @Id, @GeneratedValue, @Column, GenerationType |
| `model/Contact.java` | 5 | @Entity, @Id, @GeneratedValue, @Column, GenerationType |
| `model/CustomerAccountXRef.java` | 5 | @Entity, @Id, @GeneratedValue, @Column, GenerationType |
| **Total** | **47** | |

### 2.2 Required Change

All imports of the form:

```java
import javax.persistence.*;
```

must become:

```java
import jakarta.persistence.*;
```

No `javax.validation`, `javax.servlet`, or
`javax.annotation` imports exist in application code,
so the migration is limited to `javax.persistence` only.

### 2.3 Migration Method

This is a mechanical find-and-replace operation:

```bash
# Dry-run
grep -rn "import javax\." src/main/java --include="*.java"

# Apply
find src/main/java -name "*.java" \
  -exec sed -i 's/import javax\.persistence/import jakarta.persistence/g' {} +
```

**Risk: LOW** -- purely syntactic; no behavioral change.

---

## 3. Spring Boot 2.7.18 to 3.2.x Breaking Changes

### 3.1 Spring Boot 3.0 (November 2022)

| Change | Impact on BankApp |
|--------|-------------------|
| Jakarta EE 9+ baseline (javax to jakarta) | HIGH -- 47 imports in 7 files |
| Java 17 minimum | Required -- this is the goal |
| Spring Framework 6.0 | Transitive -- no direct action |
| Hibernate 6.1 (JPA 3.1) | MEDIUM -- UUID generation strategy change |
| Spring Security 6.0 | HIGH -- WebSecurityConfigurerAdapter removed |
| Removed `spring.config.use-legacy-processing` | LOW -- not used |
| Trailing-slash matching disabled by default | LOW -- verify endpoints |

### 3.2 Spring Boot 3.1 (May 2023)

| Change | Impact on BankApp |
|--------|-------------------|
| Dependency upgrades | Transitive |
| Docker Compose support | Not applicable |
| Testcontainers support | Not applicable |

### 3.3 Spring Boot 3.2 (November 2023)

| Change | Impact on BankApp |
|--------|-------------------|
| Virtual threads support (JDK 21) | Not applicable now |
| RestClient introduction | Optional improvement |
| JdbcClient introduction | Optional improvement |
| SSL bundle reloading | Not applicable |

### 3.4 Hibernate 6.x Changes (via Spring Boot 3.x)

| Change | Impact |
|--------|--------|
| `GenerationType.AUTO` defaults to `SEQUENCE` instead of `IDENTITY`/`TABLE` | HIGH -- 6 of 7 entities use explicit `GenerationType.AUTO` with UUID primary keys; `Customer.java` uses bare `@GeneratedValue` (implicit AUTO) |
| UUID handling improvements | MEDIUM -- verify H2 UUID column type |
| Query behavior changes | LOW -- project uses Spring Data derived queries |

**Remediation for UUID generation:** With Hibernate 6.x
and H2, `GenerationType.AUTO` with UUID may require
switching to `@GeneratedValue(strategy = GenerationType.UUID)`
or using `@UuidGenerator`. Testing is required.

---

## 4. Dependency Compatibility Analysis

### 4.1 SpringDoc OpenAPI: 1.6.x to 2.x

| Attribute | Current (1.x) | Target (2.x) |
|-----------|---------------|---------------|
| Artifact | `springdoc-openapi-ui` | `springdoc-openapi-starter-webmvc-ui` |
| Version | 1.6.15 | 2.3.0+ |
| Spring Boot | 2.x | 3.x only |
| Java | 8+ | 17+ |

**Required pom.xml change:**

```xml
<!-- Remove -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>

<!-- Add -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Code impact:** The existing `ApplicationConfig.java`
already uses `io.swagger.v3.oas.models.OpenAPI` and
`io.swagger.v3.oas.models.info.Info` -- these are from
the Swagger-core v3 library which is shared between
SpringDoc 1.x and 2.x. **No code changes needed** in
`ApplicationConfig.java`.

The controller annotations (`@Operation`, `@ApiResponse`,
`@ApiResponses`, `@Tag`) also come from the
`io.swagger.v3.oas.annotations` package and remain
unchanged.

**Swagger UI URL:** Stays at
`/bank-api/swagger-ui/index.html` (no change).

**Risk: LOW**

### 4.2 H2 Database

| Attribute | Current | Target |
|-----------|---------|--------|
| Version | managed (~2.1.214) | managed (~2.3.x via SB 3.2.x) |
| Compatibility | Java 11+ | Java 11+ |

H2 is already at version 2.x (managed by Spring Boot
2.7.18). Spring Boot 3.2.x upgrades it to 2.3.x. No
major breaking changes from H2 2.1 to 2.3.

**Consideration:** The in-memory database URL
`jdbc:h2:mem:testdb` is no longer auto-configured as the
default in Spring Boot 3.x. Add to `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
```

**Risk: LOW**

### 4.3 Lombok

| Attribute | Current | Target |
|-----------|---------|--------|
| Version | managed (~1.18.30) | 1.18.30+ (managed by SB 3.2.x) |
| Java 17 | Fully compatible since 1.18.22 | Yes |

Lombok 1.18.30 (managed by Spring Boot 2.7.18) is already
fully compatible with Java 17. Spring Boot 3.2.x manages
1.18.30+.

**Risk: NONE**

### 4.4 JAXB Runtime

| Attribute | Current | Target |
|-----------|---------|--------|
| Artifact | `org.glassfish.jaxb:jaxb-runtime` | `org.glassfish.jaxb:jaxb-runtime` |
| Version | 2.3.8 | 4.0.x |
| Namespace | `javax.xml.bind` | `jakarta.xml.bind` |

Spring Boot 3.x manages JAXB 4.x which uses the
`jakarta.xml.bind` namespace. Since the application code
does not directly import any `javax.xml.bind` classes,
the version bump is safe.

**Required pom.xml change:** Remove the explicit version
override and let Spring Boot 3.2.x manage it, or set:

```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <!-- version managed by Spring Boot 3.2.x BOM -->
</dependency>
```

**Risk: LOW**

### 4.5 Spring Security

| Attribute | Current | Target |
|-----------|---------|--------|
| Version | 5.7.x (managed) | 6.2.x (managed by SB 3.2.x) |
| Java 17 | Yes | Yes |

Spring Security 6.x removes
`WebSecurityConfigurerAdapter` entirely. See Section 5.

**Risk: HIGH** (code rewrite required)

### 4.6 Spring Boot Test / JUnit

Spring Boot 3.2.x manages JUnit 5.10.x. The project
currently uses `spring-boot-starter-test` and
`spring-security-test`. One test file exists
(`BankingApplicationTests.java` with a `@SpringBootTest`
context load test). It uses JUnit 5 and requires no
code changes, but must be run to verify the migrated
application context loads correctly.

**Risk: LOW**

### 4.7 Dependency Compatibility Summary

| Dependency | Compatible? | Action Required |
|------------|------------|-----------------|
| Spring Boot 3.2.5 | Yes (Java 17+) | Upgrade parent |
| SpringDoc OpenAPI 2.3.0 | Yes | Change artifact ID |
| H2 Database (managed) | Yes | Add explicit datasource URL |
| Lombok (managed) | Yes | None |
| JAXB Runtime | Yes | Remove version, let BOM manage |
| Spring Security 6.2.x | Yes | Rewrite SecurityConfig |
| Spring Data JPA (managed) | Yes | javax to jakarta imports |
| Actuator (managed) | Yes | None |
| DevTools (managed) | Yes | None |

---

## 5. Spring Security Configuration Migration

### 5.1 Current Implementation

```java
// SecurityConfig.java -- CURRENT (will not compile with SB 3.x)
@Configuration
public class SecurityConfig
    extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity)
        throws Exception {
        httpSecurity.authorizeRequests()
            .antMatchers("/").permitAll()
            .and()
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll();

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
```

### 5.2 Required Migration

`WebSecurityConfigurerAdapter` is removed in Spring
Security 6.x. The configuration must use a
`SecurityFilterChain` bean:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );
        return http.build();
    }
}
```

### 5.3 Key API Changes

| Spring Security 5.x | Spring Security 6.x |
|---------------------|---------------------|
| `WebSecurityConfigurerAdapter` | Removed -- use `SecurityFilterChain` bean |
| `.authorizeRequests()` | `.authorizeHttpRequests()` |
| `.antMatchers()` | `.requestMatchers()` |
| `.csrf().disable()` | `.csrf(csrf -> csrf.disable())` |
| `.headers().frameOptions().disable()` | `.headers(h -> h.frameOptions(f -> f.disable()))` |

**Risk: HIGH** -- This is a behavioral change. Testing
must verify that authentication, H2 console access, and
Swagger UI access all work correctly after migration.

---

## 6. Build Configuration Changes

### 6.1 pom.xml Updates Required

```xml
<!-- 1. Update parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
    <relativePath/>
</parent>

<!-- 2. Update Java version properties -->
<properties>
    <java.version>17</java.version>
    <maven.compiler.release>17</maven.compiler.release>
</properties>

<!-- 3. Update maven-enforcer-plugin rule -->
<requireJavaVersion>
    <version>[17,)</version>
</requireJavaVersion>

<!-- 4. Replace SpringDoc dependency -->
<!-- OLD: springdoc-openapi-ui 1.6.15 -->
<!-- NEW: springdoc-openapi-starter-webmvc-ui 2.3.0 -->

<!-- 5. Remove explicit jaxb-runtime version -->
<!-- Let Spring Boot 3.2.x BOM manage it -->

<!-- 6. Update maven-compiler-plugin -->
<configuration>
    <release>17</release>
</configuration>
```

### 6.2 CI Pipeline Update (`.github/workflows/ci.yml`)

```yaml
# Current
- name: Set up JDK 11
  uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '11'

# Target
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '17'
```

### 6.3 Maven Wrapper

The existing Maven wrapper (`mvnw`, `mvnw.cmd`) should
work with Java 17 without changes. Verify the wrapper
properties file points to Maven 3.9.x+.

---

## 7. Java 17 Language Features

Java 17 introduces several language features available
for use after migration. While not required for
the migration itself, these represent modernization
opportunities.

### 7.1 Records (JDK 16+)

**Opportunity:** The 7 DTO classes in `domain/` are
candidates for conversion to records. They are simple
data carriers with no business logic.

```java
// Current (with Lombok)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class TransferDetails {
    private Long fromAccountNumber;
    private Long toAccountNumber;
    private Double transferAmount;
}

// Potential (Java record)
public record TransferDetails(
    Long fromAccountNumber,
    Long toAccountNumber,
    Double transferAmount
) {}
```

**Caveat:** Records are immutable and do not have
no-arg constructors. Jackson deserialization works with
records (Spring Boot 3.x includes Jackson 2.15+ with
record support), but frameworks like Lombok's `@Builder`
are not compatible. Evaluate on a case-by-case basis.

**Applicable DTOs:**

| Class | Fields | Record Candidate? |
|-------|--------|-------------------|
| TransferDetails | 3 | Yes |
| TransactionDetails | 4 | Yes |
| ContactDetails | 3 | Yes |
| AddressDetails | 6 | Yes |
| BankInformation | 4 | Yes |
| CustomerDetails | 7 | Maybe (has nested objects) |
| AccountInformation | 6 | Maybe (has nested objects) |

### 7.2 Sealed Classes (JDK 17)

Not directly applicable to the current codebase. Could
be used if the `BankingService` interface is extended
with multiple specialized implementations in the future.

### 7.3 Pattern Matching for instanceof (JDK 16+)

Not directly applicable -- the codebase does not use
`instanceof` checks.

### 7.4 Text Blocks (JDK 15+)

Not directly applicable -- no multi-line strings in the
codebase. Could be useful for SQL queries or JSON
templates if added later.

### 7.5 Switch Expressions (JDK 14+)

Not directly applicable -- the codebase does not use
switch statements.

### 7.6 Security Manager Deprecation

The Security Manager is deprecated for removal in
Java 17 (JEP 411). This does not affect the BankApp
project as it does not use `SecurityManager` or
`Policy` APIs.

### 7.7 Strong Encapsulation of JDK Internals

Java 17 enforces strong encapsulation by default
(`--illegal-access=deny`). Dependencies that access
internal JDK APIs will fail at runtime unless JVM
flags are added.

**Impact Assessment:**

| Dependency | Uses Internal APIs? | Risk |
|------------|-------------------|------|
| Lombok (1.18.30+) | Yes (compiler APIs) | NONE -- handled via `--add-opens` in recent versions |
| Spring Framework 6.x | No | NONE |
| H2 (2.x) | No | NONE |
| Hibernate 6.x | No | NONE |

If any `InaccessibleObjectException` errors occur at
runtime, add JVM flags:

```
--add-opens java.base/java.lang=ALL-UNNAMED
```

This is unlikely to be needed with the modern dependency
versions managed by Spring Boot 3.2.x.

---

## 8. Risk Assessment

### 8.1 Risk Matrix

| # | Risk Item | Severity | Likelihood | Impact | Mitigation |
|---|-----------|----------|------------|--------|------------|
| 1 | javax to jakarta migration | MEDIUM | Certain | Compilation failure if missed | Automated find-and-replace; grep verification |
| 2 | SecurityConfig rewrite | HIGH | Certain | App won't start | Rewrite with SecurityFilterChain; test auth flows |
| 3 | Hibernate UUID generation change | MEDIUM | Likely | Data layer errors | Test entity creation; may need `@UuidGenerator` |
| 4 | SpringDoc artifact rename | LOW | Certain | Swagger UI unavailable | Simple dependency swap |
| 5 | H2 datasource URL not auto-configured | LOW | Likely | DB connection failure at startup | Add explicit URL in application.yml |
| 6 | Strong encapsulation (JDK internals) | LOW | Unlikely | Runtime reflection errors | Modern deps handle this; add `--add-opens` if needed |
| 7 | Trailing-slash matching disabled | LOW | Possible | 404 for URLs ending in `/` | Test endpoints; re-enable if needed |

### 8.2 Overall Risk Rating

**MEDIUM** -- The migration is well-understood with
clear, documented steps. The two HIGH items
(SecurityConfig rewrite and javax-to-jakarta) are
mechanical and well-documented by the Spring team.
Thorough testing is the primary mitigation.

---

## 9. Migration Plan

### 9.1 Recommended Approach

Perform the migration in a single branch with these
ordered steps:

### Phase 1: Build Configuration (Low Risk)

1. Update `pom.xml` parent to Spring Boot 3.2.5
2. Set `java.version` and `maven.compiler.release` to 17
3. Update `maven-enforcer-plugin` to require Java 17+
4. Replace `springdoc-openapi-ui` with
   `springdoc-openapi-starter-webmvc-ui` 2.3.0
5. Remove explicit `jaxb-runtime` version
6. Update `maven-compiler-plugin` release to 17

### Phase 2: Namespace Migration (Medium Risk)

7. Replace all `javax.persistence` imports with
   `jakarta.persistence` in 7 model files
8. Verify no other `javax.*` imports remain

### Phase 3: Security Rewrite (High Risk)

9. Rewrite `SecurityConfig.java` to use
   `SecurityFilterChain` bean pattern
10. Add `@EnableWebSecurity` annotation
11. Migrate to lambda-style DSL
12. Add SpringDoc endpoint paths to permit list

### Phase 4: Application Configuration (Low Risk)

13. Add explicit H2 datasource URL in `application.yml`
14. Verify `application.yml` syntax

### Phase 5: CI/CD Update (Low Risk)

15. Update `.github/workflows/ci.yml` to use JDK 17

### Phase 6: Verification (Critical)

16. Run `mvn clean compile` -- verify zero errors
17. Run `mvn test` -- verify all tests pass
18. Start application and verify:
    - Health endpoint: `/bank-api/actuator/health`
    - Swagger UI: `/bank-api/swagger-ui/index.html`
    - H2 Console: `/bank-api/h2-console/`
    - Customer CRUD endpoints
    - Account and transaction endpoints
19. Verify no illegal-access warnings in logs

### 9.2 Estimated Effort

| Phase | Description | Effort |
|-------|-------------|--------|
| 1 | Build configuration | 1-2 hours |
| 2 | Namespace migration | 30 minutes |
| 3 | Security rewrite | 1-2 hours |
| 4 | Application config | 30 minutes |
| 5 | CI/CD update | 15 minutes |
| 6 | Verification & testing | 2-4 hours |
| **Total** | | **5-9 hours** |

### 9.3 Rollback Strategy

The migration should be done on a feature branch. If
issues are found:

1. Keep the branch open for fixes
2. The `main` branch remains on Java 11 / Spring Boot
   2.7.18 until the migration branch is merged
3. No database migration is involved (H2 in-memory), so
   rollback is simply not merging the branch

---

## 10. File-by-File Change Checklist

| File | Changes |
|------|---------|
| `pom.xml` | Parent version, java.version, maven.compiler.release, enforcer rule, SpringDoc artifact, JAXB version removal, compiler plugin release |
| `model/Account.java` | 9 imports: javax.persistence to jakarta.persistence |
| `model/Customer.java` | 9 imports: javax.persistence to jakarta.persistence |
| `model/BankInfo.java` | 7 imports: javax.persistence to jakarta.persistence |
| `model/Transaction.java` | 7 imports: javax.persistence to jakarta.persistence |
| `model/Address.java` | 5 imports: javax.persistence to jakarta.persistence |
| `model/Contact.java` | 5 imports: javax.persistence to jakarta.persistence |
| `model/CustomerAccountXRef.java` | 5 imports: javax.persistence to jakarta.persistence |
| `config/SecurityConfig.java` | Full rewrite: SecurityFilterChain bean pattern |
| `config/ApplicationConfig.java` | No changes needed |
| `controller/AccountController.java` | No changes needed |
| `controller/CustomerController.java` | No changes needed |
| `service/*` | No changes needed |
| `domain/*` | No changes needed (optional: convert to records) |
| `repository/*` | No changes needed |
| `application.yml` | Add explicit datasource URL |
| `.github/workflows/ci.yml` | JDK 11 to JDK 17 |
| `README.md` | Update Java version requirement |

**Total files requiring changes: 11**
**Total files unchanged: 16+**

---

## 11. Post-Migration Opportunities

After the Java 17 migration is complete, consider these
optional modernization improvements:

1. **Convert DTOs to Records** -- Reduce boilerplate in
   the 7 domain classes
2. **Constructor Injection** -- Replace `@Autowired`
   field injection with constructor injection
   (recommended by Spring team)
3. **Replace `Date` with `java.time`** -- Entity
   timestamp fields use `java.util.Date`; migrate to
   `LocalDateTime` or `Instant`
4. **Typed ResponseEntity** -- Replace
   `ResponseEntity<Object>` with specific types for
   better OpenAPI documentation
5. **Add `spring-boot-starter-validation`** -- Enable
   bean validation on DTOs with `@Valid`

---

## References

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Boot 3.1 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.1-Release-Notes)
- [Spring Boot 3.2 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes)
- [Spring Security 6.0 Migration](https://docs.spring.io/spring-security/reference/migration/index.html)
- [SpringDoc OpenAPI v2 Migration](https://springdoc.org/#migrating-from-springdoc-v1)
- [Jakarta EE 9 Release](https://jakarta.ee/release/9/)
- [JEP 411: Deprecate the Security Manager](https://openjdk.org/jeps/411)
- [Hibernate 6 Migration Guide](https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc)
