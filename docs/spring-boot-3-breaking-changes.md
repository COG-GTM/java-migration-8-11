# Spring Boot 3.x Breaking Changes Analysis

**Project:** BankApp (Aplicacion-de-Banca-Spring-Boot)  
**Analysis Date:** February 6, 2026  
**Current Spring Boot Version:** 2.7.18  
**Target Spring Boot Version:** 3.x (requires Java 17+)

## Executive Summary

This document analyzes the breaking changes required to migrate from Spring Boot 2.7.18 to Spring Boot 3.x. Spring Boot 3.x is a major release that requires Java 17 as a minimum and introduces significant changes including the Jakarta EE 9+ namespace migration.

**Key Findings:**

- **47 import statements** across **7 files** need to change from `javax.*` to `jakarta.*`
- **SecurityConfig.java** uses deprecated `WebSecurityConfigurerAdapter` which is **removed** in Spring Security 6.x
- SpringDoc OpenAPI needs to be upgraded from 1.x to 2.x
- No circular dependency issues detected
- H2 database configuration may need updates

**Estimated Migration Effort:** 8-16 hours

## 1. Jakarta EE Namespace Migration

### 1.1 Overview

Spring Boot 3.x requires Jakarta EE 9+, which changed the package namespace from `javax.*` to `jakarta.*`. This is the most significant change affecting the codebase.

### 1.2 Files Requiring Changes

The following files contain `javax.persistence.*` imports that must be changed to `jakarta.persistence.*`:

| File | Line Numbers | Import Count |
|------|--------------|--------------|
| `src/main/java/com/coding/exercise/bankapp/model/Account.java` | 6-14 | 9 |
| `src/main/java/com/coding/exercise/bankapp/model/Address.java` | 5-9 | 5 |
| `src/main/java/com/coding/exercise/bankapp/model/BankInfo.java` | 5-11 | 7 |
| `src/main/java/com/coding/exercise/bankapp/model/Contact.java` | 5-9 | 5 |
| `src/main/java/com/coding/exercise/bankapp/model/Customer.java` | 6-14 | 9 |
| `src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java` | 5-9 | 5 |
| `src/main/java/com/coding/exercise/bankapp/model/Transaction.java` | 6-12 | 7 |

**Total: 47 import statements in 7 files**

### 1.3 Detailed Import Changes Required

#### Account.java (Lines 6-14)

**Current:**
```java
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
```

**Required Change:**
```java
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
```

#### Address.java (Lines 5-9)

**Current:**
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

**Required Change:**
```java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

#### BankInfo.java (Lines 5-11)

**Current:**
```java
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
```

**Required Change:**
```java
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
```

#### Contact.java (Lines 5-9)

**Current:**
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

**Required Change:**
```java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

#### Customer.java (Lines 6-14)

**Current:**
```java
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
```

**Required Change:**
```java
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
```

#### CustomerAccountXRef.java (Lines 5-9)

**Current:**
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

**Required Change:**
```java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

#### Transaction.java (Lines 6-12)

**Current:**
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
```

**Required Change:**
```java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
```

### 1.4 Jakarta EE Namespace Summary

| Old Namespace | New Namespace | Used In |
|---------------|---------------|---------|
| `javax.persistence.*` | `jakarta.persistence.*` | All JPA entities |
| `javax.validation.*` | `jakarta.validation.*` | Not currently used |
| `javax.servlet.*` | `jakarta.servlet.*` | Not directly used |
| `javax.annotation.*` | `jakarta.annotation.*` | Transitive only |
| `javax.transaction.*` | `jakarta.transaction.*` | Transitive only |

## 2. Spring Security Configuration Changes

### 2.1 WebSecurityConfigurerAdapter Removal

**File:** `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`

**Current Implementation (Lines 1-27):**
```java
package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll();

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
```

**Issues:**

1. `WebSecurityConfigurerAdapter` is **deprecated** in Spring Security 5.7+ and **removed** in Spring Security 6.x
2. `antMatchers()` is replaced with `requestMatchers()` in Spring Security 6.x
3. Method chaining style has changed

### 2.2 Required Security Configuration for Spring Boot 3.x

**New Implementation:**
```java
package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );
        
        return http.build();
    }
}
```

### 2.3 Key Security Changes Summary

| Spring Security 5.x | Spring Security 6.x |
|---------------------|---------------------|
| `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` |
| `configure(HttpSecurity http)` | `securityFilterChain(HttpSecurity http)` |
| `antMatchers()` | `requestMatchers()` |
| `authorizeRequests()` | `authorizeHttpRequests()` |
| Method chaining | Lambda DSL (recommended) |

## 3. SpringDoc OpenAPI Migration

### 3.1 Current Configuration

**File:** `src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java`

The application currently uses SpringDoc OpenAPI 1.6.15, which is compatible with Spring Boot 2.x.

### 3.2 Required Changes for Spring Boot 3.x

**Dependency Change in pom.xml:**

**Current:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>
```

**Required:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 3.3 API Annotations

The current SpringDoc annotations (`@Tag`, `@Operation`, `@ApiResponse`, `@ApiResponses`) from `io.swagger.v3.oas.annotations` package are compatible with SpringDoc 2.x. No changes required to controller annotations.

**Files using SpringDoc annotations:**

- `src/main/java/com/coding/exercise/bankapp/controller/CustomerController.java` (Lines 19-22)
- `src/main/java/com/coding/exercise/bankapp/controller/AccountController.java` (Lines 20-23)

## 4. Dependency Updates Required

### 4.1 pom.xml Changes

**Current Parent:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>
```

**Required Parent:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
</parent>
```

**Java Version:**
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

**Maven Enforcer Plugin:**
```xml
<requireJavaVersion>
    <version>[17,)</version>
</requireJavaVersion>
```

### 4.2 Dependency Version Changes

| Dependency | Current Version | Spring Boot 3.x Version |
|------------|-----------------|------------------------|
| Spring Framework | 5.3.31 | 6.1.x |
| Spring Security | 5.7.11 | 6.2.x |
| Hibernate | 5.6.15.Final | 6.4.x |
| H2 Database | 2.1.214 | 2.2.x |
| Jackson | 2.13.5 | 2.16.x |
| Tomcat | 9.0.83 | 10.1.x |
| Lombok | 1.18.30 | 1.18.30 (compatible) |
| SpringDoc | 1.6.15 | 2.3.x |

### 4.3 JAXB Runtime

**Current:**
```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.8</version>
</dependency>
```

**Required for Spring Boot 3.x:**
```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>4.0.4</version>
</dependency>
```

## 5. Circular Dependency Analysis

### 5.1 Current Bean Dependencies

Analyzed the service layer for potential circular dependencies:

| Bean | Dependencies |
|------|--------------|
| `BankingServiceImpl` | `CustomerRepository`, `AccountRepository`, `TransactionRepository`, `CustomerAccountXRefRepository`, `BankingServiceHelper` |
| `BankingServiceHelper` | None (utility class) |
| `CustomerController` | `BankingServiceImpl` |
| `AccountController` | `BankingServiceImpl` |
| `SecurityConfig` | None |
| `ApplicationConfig` | None |

**Result:** No circular dependencies detected.

### 5.2 Spring Boot 3.x Circular Reference Behavior

Spring Boot 3.x is stricter about circular references. The default behavior prohibits circular references:

```properties
spring.main.allow-circular-references=false  # default in Spring Boot 3.x
```

Since no circular dependencies exist in the current codebase, no changes are required.

## 6. H2 Database Configuration

### 6.1 Current Configuration

The application uses H2 in-memory database with console access enabled.

### 6.2 Spring Boot 3.x Changes

H2 console access requires explicit configuration in Spring Boot 3.x:

**application.yml or application.properties:**
```yaml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: false
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
```

### 6.3 Security Configuration for H2 Console

The security configuration must explicitly allow H2 console access (already covered in Section 2.2).

## 7. Migration Checklist

### 7.1 Pre-Migration

- [ ] Ensure all tests pass on current version
- [ ] Document current application behavior
- [ ] Backup database schemas (if applicable)
- [ ] Review this breaking changes document

### 7.2 Phase 1: Java 17 Migration

- [ ] Update `pom.xml` to Java 17
- [ ] Update CI/CD to use JDK 17
- [ ] Run tests and verify functionality
- [ ] See `docs/java-17-compatibility-audit.md`

### 7.3 Phase 2: Jakarta EE Namespace Migration

- [ ] Update `Account.java` imports (9 changes)
- [ ] Update `Address.java` imports (5 changes)
- [ ] Update `BankInfo.java` imports (7 changes)
- [ ] Update `Contact.java` imports (5 changes)
- [ ] Update `Customer.java` imports (9 changes)
- [ ] Update `CustomerAccountXRef.java` imports (5 changes)
- [ ] Update `Transaction.java` imports (7 changes)
- [ ] Verify compilation succeeds

### 7.4 Phase 3: Spring Security Migration

- [ ] Rewrite `SecurityConfig.java` using component-based configuration
- [ ] Replace `WebSecurityConfigurerAdapter` with `SecurityFilterChain` bean
- [ ] Update `antMatchers()` to `requestMatchers()`
- [ ] Test H2 console access
- [ ] Test API endpoints

### 7.5 Phase 4: Dependency Updates

- [ ] Update Spring Boot parent to 3.x
- [ ] Update SpringDoc to 2.x
- [ ] Update JAXB runtime to 4.x
- [ ] Update Maven enforcer plugin
- [ ] Run `mvn dependency:tree` to verify

### 7.6 Phase 5: Testing and Validation

- [ ] Run full test suite
- [ ] Test all REST endpoints
- [ ] Verify Swagger UI at `/swagger-ui.html`
- [ ] Verify H2 console at `/h2-console`
- [ ] Verify actuator endpoints
- [ ] Test authentication/authorization

## 8. Risk Assessment

### 8.1 High Risk Items

| Item | Risk | Mitigation |
|------|------|------------|
| SecurityConfig rewrite | Application security | Thorough testing of all endpoints |
| Jakarta namespace migration | Compilation errors | IDE refactoring tools |
| SpringDoc upgrade | API documentation | Verify Swagger UI functionality |

### 8.2 Medium Risk Items

| Item | Risk | Mitigation |
|------|------|------------|
| Hibernate 6.x upgrade | Query behavior changes | Test all database operations |
| H2 2.2.x upgrade | SQL syntax changes | Test database initialization |

### 8.3 Low Risk Items

| Item | Risk | Mitigation |
|------|------|------------|
| Java 17 upgrade | Minimal code changes | Already analyzed |
| Lombok compatibility | None expected | Version 1.18.30 is compatible |

## 9. Estimated Timeline

| Phase | Description | Estimated Effort |
|-------|-------------|------------------|
| 1 | Java 17 Migration | 2-4 hours |
| 2 | Jakarta EE Namespace | 1-2 hours |
| 3 | Spring Security | 2-4 hours |
| 4 | Dependency Updates | 1-2 hours |
| 5 | Testing & Validation | 2-4 hours |
| **Total** | | **8-16 hours** |

## 10. References

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Security 6.0 Migration Guide](https://docs.spring.io/spring-security/reference/migration/index.html)
- [Jakarta EE 9 Release Notes](https://jakarta.ee/release/9/)
- [SpringDoc OpenAPI 2.x Migration](https://springdoc.org/v2/)
- [Hibernate 6 Migration Guide](https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc)

## Appendix A: Automated Migration Tools

### OpenRewrite Recipes

OpenRewrite provides automated recipes for Spring Boot 3.x migration:

```xml
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.23.1</version>
    <configuration>
        <activeRecipes>
            <recipe>org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-spring</artifactId>
            <version>5.5.0</version>
        </dependency>
    </dependencies>
</plugin>
```

Run with: `mvn rewrite:run`

### IntelliJ IDEA Migration

IntelliJ IDEA provides refactoring support:
1. **Refactor > Migrate Packages and Classes**
2. Select "javax to jakarta" migration
3. Review and apply changes

## Appendix B: Related Documentation

- [Java 17 Compatibility Audit](java-17-compatibility-audit.md)
- [Java 8 to 11 Migration Notes](../MIGRATION_NOTES.md)
- [Spring Boot Compatibility Analysis](spring-boot-compatibility.md)
- [Swagger Migration Guide](swagger-migration-guide.md)
