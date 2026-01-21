# Java 11 to Java 17 Migration Analysis - Workstream A Deliverables

## Executive Summary

This document provides a comprehensive analysis for migrating the BankApp Spring Boot application from Java 11 with Spring Boot 2.7.18 to Java 17 with Spring Boot 3.x. This analysis serves as the foundation for Workstreams B, C, D, and E.

**Migration Branch**: `feature/java-17-migration`
**Backup Tag**: `java-11-final`
**Analysis Date**: January 21, 2026

---

## A1: Dependency Compatibility Analysis

### Current Project Configuration

| Component | Current Version | Required for Spring Boot 3.x |
|-----------|-----------------|------------------------------|
| Java | 11 | 17+ |
| Spring Boot Parent | 2.7.18 | 3.2.x or 3.3.x |
| Spring Framework | 5.3.x (managed) | 6.x (managed) |
| SpringDoc OpenAPI | 1.6.15 | 2.x |
| H2 Database | 2.x (managed) | 2.x (compatible) |
| Lombok | 1.18.30 (managed) | 1.18.30+ (compatible) |
| JAXB Runtime | 2.3.8 | 4.x (Jakarta namespace) |

### Dependency Compatibility Matrix

| Dependency | Current Version | Spring Boot 3.x Version | Breaking Changes | Migration Effort |
|------------|-----------------|-------------------------|------------------|------------------|
| `spring-boot-starter-parent` | 2.7.18 | 3.2.5 / 3.3.0 | Major - Jakarta EE 9+ | High |
| `spring-boot-starter-web` | Managed | Managed | Jakarta Servlet API | Medium |
| `spring-boot-starter-data-jpa` | Managed | Managed | Jakarta Persistence API | High |
| `spring-boot-starter-security` | Managed | Managed | Security config changes | High |
| `spring-boot-starter-actuator` | Managed | Managed | Minor endpoint changes | Low |
| `spring-boot-devtools` | Managed | Managed | Compatible | Low |
| `springdoc-openapi-ui` | 1.6.15 | N/A - Replace with 2.x | Major API changes | Medium |
| `h2` | Managed (2.x) | Managed (2.x) | Compatible | Low |
| `lombok` | Managed | Managed | Compatible | Low |
| `jaxb-runtime` | 2.3.8 | 4.0.x | Jakarta namespace | Medium |
| `spring-boot-starter-test` | Managed | Managed | JUnit 5 compatible | Low |
| `spring-security-test` | Managed | Managed | Compatible | Low |

### SpringDoc OpenAPI Migration

The current `springdoc-openapi-ui:1.6.15` dependency is NOT compatible with Spring Boot 3.x. It must be replaced:

**Current (Spring Boot 2.x)**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>
```

**Required (Spring Boot 3.x)**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### JAXB Runtime Migration

The current JAXB runtime uses `javax.xml.bind` namespace. For Spring Boot 3.x:

**Current**:
```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.8</version>
</dependency>
```

**Required**:
```xml
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>4.0.4</version>
</dependency>
```

### Maven Plugin Updates Required

| Plugin | Current Version | Required Version | Notes |
|--------|-----------------|------------------|-------|
| `maven-compiler-plugin` | 3.11.0 | 3.11.0+ | Update release to 17 |
| `maven-surefire-plugin` | 3.2.5 | 3.2.5 | Compatible |
| `maven-failsafe-plugin` | 3.2.5 | 3.2.5 | Compatible |
| `maven-enforcer-plugin` | 3.5.0 | 3.5.0 | Update Java version requirement |
| `spring-boot-maven-plugin` | Managed | Managed | Compatible |

### jdeps Analysis Results

The jdeps analysis was performed on the compiled classes to identify JDK internal API usage:

```
jdeps --jdk-internals target/classes
```

**Result**: No JDK internal API usage detected. The application code does not use any deprecated or removed JDK internal APIs that would cause issues with Java 17.

**Key Findings**:
- No use of `sun.*` or `com.sun.*` internal APIs
- No illegal reflective access patterns detected
- All dependencies use standard Java APIs

---

## A2: Jakarta Namespace Impact Assessment

### Overview

Spring Boot 3.x requires Jakarta EE 9+ which uses the `jakarta.*` namespace instead of `javax.*`. This is the most significant change affecting the codebase.

### Files Requiring Namespace Changes

#### Entity Classes (7 files, 47 import statements)

| File | javax.persistence Imports | Lines Affected |
|------|---------------------------|----------------|
| `model/Account.java` | 9 imports | Lines 6-14 |
| `model/Customer.java` | 9 imports | Lines 6-14 |
| `model/Transaction.java` | 7 imports | Lines 6-12 |
| `model/Contact.java` | 5 imports | Lines 5-9 |
| `model/Address.java` | 5 imports | Lines 5-9 |
| `model/BankInfo.java` | 7 imports | Lines 5-11 |
| `model/CustomerAccountXRef.java` | 5 imports | Lines 5-9 |

**Total javax.persistence imports**: 47

#### Detailed Import Changes Required

**Account.java** (Lines 6-14):
```java
// FROM:
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

// TO:
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

**Customer.java** (Lines 6-14):
```java
// FROM:
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

// TO:
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

**Transaction.java** (Lines 6-12):
```java
// FROM:
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

// TO:
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
```

**Contact.java** (Lines 5-9):
```java
// FROM:
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// TO:
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

**Address.java** (Lines 5-9):
```java
// FROM:
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// TO:
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

**BankInfo.java** (Lines 5-11):
```java
// FROM:
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

// TO:
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
```

**CustomerAccountXRef.java** (Lines 5-9):
```java
// FROM:
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// TO:
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
```

### Security Configuration Changes

**File**: `config/SecurityConfig.java`

The current security configuration extends `WebSecurityConfigurerAdapter` which is **deprecated and removed** in Spring Security 6.x (used by Spring Boot 3.x).

**Current Implementation**:
```java
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

**Required Implementation (Spring Boot 3.x)**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}
```

### Summary of Namespace Changes

| Namespace Category | Files Affected | Import Statements | Effort |
|--------------------|----------------|-------------------|--------|
| `javax.persistence.*` | 7 | 47 | Medium |
| `javax.validation.*` | 0 | 0 | None |
| `javax.servlet.*` | 0 | 0 | None |
| `javax.annotation.*` | 0 | 0 | None |
| **Total** | **7** | **47** | **Medium** |

### Automated Migration Option

The migration can be automated using OpenRewrite:

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
            <version>5.6.0</version>
        </dependency>
    </dependencies>
</plugin>
```

---

## A3: Risk Assessment and Mitigation Planning

### High-Risk Areas

#### 1. Jakarta EE Namespace Migration (Risk: HIGH)

**Description**: All JPA entities use `javax.persistence.*` imports which must be changed to `jakarta.persistence.*`.

**Impact**:
- 7 entity files affected
- 47 import statements to change
- Potential for runtime errors if any imports are missed

**Mitigation Strategy**:
1. Use OpenRewrite automated migration tool
2. Perform comprehensive search for any remaining `javax.*` imports
3. Run full test suite after migration
4. Verify all JPA operations work correctly

**Verification Steps**:
- `grep -r "javax\." src/` should return no results after migration
- All unit tests pass
- Application starts without errors
- CRUD operations work correctly

#### 2. Spring Security Configuration (Risk: HIGH)

**Description**: `WebSecurityConfigurerAdapter` is removed in Spring Security 6.x. The security configuration must be completely rewritten.

**Impact**:
- Complete rewrite of `SecurityConfig.java`
- New lambda-based DSL required
- Potential for security misconfigurations

**Mitigation Strategy**:
1. Study Spring Security 6.x migration guide
2. Rewrite using `SecurityFilterChain` bean approach
3. Test all secured endpoints
4. Verify H2 console access still works
5. Verify basic authentication still works

**Verification Steps**:
- All endpoints require authentication (except permitted ones)
- H2 console accessible at `/bank-api/h2-console/`
- Basic auth with `bankapp:changeit` works
- No security vulnerabilities introduced

#### 3. Spring Boot 3.x Breaking Changes (Risk: HIGH)

**Description**: Spring Boot 3.x includes numerous breaking changes in configuration properties and behavior.

**Impact**:
- Configuration property changes
- Default behavior changes
- Potential for subtle bugs

**Mitigation Strategy**:
1. Review Spring Boot 3.x release notes thoroughly
2. Update `application.yml` as needed
3. Test all application features
4. Monitor logs for deprecation warnings

**Key Configuration Changes**:
- `spring.redis.*` -> `spring.data.redis.*`
- `spring.elasticsearch.*` -> `spring.elasticsearch.uris`
- Actuator endpoint changes

### Medium-Risk Areas

#### 1. SpringDoc OpenAPI Migration (Risk: MEDIUM)

**Description**: SpringDoc 1.x is not compatible with Spring Boot 3.x. Must migrate to SpringDoc 2.x.

**Impact**:
- Dependency change required
- Potential annotation changes
- Swagger UI URL may change

**Mitigation Strategy**:
1. Replace `springdoc-openapi-ui` with `springdoc-openapi-starter-webmvc-ui`
2. Update version to 2.3.0+
3. Verify Swagger UI accessible
4. Test all API documentation

**Verification Steps**:
- Swagger UI accessible at `/bank-api/swagger-ui.html`
- All endpoints documented correctly
- Try/Execute functionality works

#### 2. H2 Database Compatibility (Risk: MEDIUM)

**Description**: H2 database is already at version 2.x which is compatible with Spring Boot 3.x.

**Impact**:
- Minimal changes expected
- Console URL may need verification

**Mitigation Strategy**:
1. Verify H2 console configuration
2. Test database operations
3. Verify data persistence

#### 3. Maven Plugin Configuration (Risk: MEDIUM)

**Description**: Maven plugins need configuration updates for Java 17.

**Impact**:
- Compiler plugin release version change
- Enforcer plugin Java version requirement

**Mitigation Strategy**:
1. Update `maven.compiler.release` to 17
2. Update enforcer plugin to require Java 17+
3. Verify build succeeds

### Low-Risk Areas

#### 1. Java 17 Language Features (Risk: LOW)

**Description**: Optional adoption of Java 17 language features.

**Impact**:
- No immediate changes required
- Can adopt features incrementally

**Mitigation Strategy**:
1. Keep existing code patterns initially
2. Adopt new features in future PRs
3. Consider: records, sealed classes, pattern matching

#### 2. Lombok Compatibility (Risk: LOW)

**Description**: Lombok is compatible with Java 17.

**Impact**:
- No changes required
- Current version (1.18.30) supports Java 17

**Mitigation Strategy**:
1. Verify Lombok annotations work correctly
2. Update if newer version available

#### 3. Test Framework (Risk: LOW)

**Description**: JUnit 5 is already in use and compatible with Spring Boot 3.x.

**Impact**:
- No changes required
- Tests should work as-is

**Mitigation Strategy**:
1. Run all tests after migration
2. Fix any test failures

---

## A4: Migration Branch Setup and Rollback Procedure

### Branch Infrastructure

**Feature Branch**: `feature/java-17-migration`
- Created from: `master`
- Purpose: Contains all Java 17 migration changes

**Backup Tag**: `java-11-final`
- Points to: Last commit on `master` before migration
- Purpose: Quick rollback reference point

### Rollback Procedure

If the migration needs to be reverted, follow these steps:

#### Option 1: Revert to Backup Tag (Recommended)

```bash
# Checkout the backup tag
git checkout java-11-final

# Create a new branch from the backup
git checkout -b hotfix/revert-java17-migration

# Push the revert branch
git push origin hotfix/revert-java17-migration

# Create PR to merge revert into master
```

#### Option 2: Revert Specific Commits

```bash
# Identify commits to revert
git log --oneline master..feature/java-17-migration

# Revert each commit (in reverse order)
git revert <commit-hash>

# Push reverts
git push origin feature/java-17-migration
```

#### Option 3: Hard Reset (Emergency Only)

```bash
# WARNING: This will lose all migration work
git checkout master
git reset --hard java-11-final
git push --force origin master  # DANGEROUS - requires admin approval
```

### Staging Environment Plan

#### Pre-Production Testing Strategy

1. **Local Development Testing**
   - Run all unit tests: `./mvnw test`
   - Run integration tests: `./mvnw verify`
   - Start application locally: `./mvnw spring-boot:run`
   - Test all endpoints manually

2. **CI/CD Pipeline Testing**
   - GitHub Actions workflow updated for Java 17
   - Automated tests run on every push
   - Build artifacts verified

3. **Staging Environment Deployment**
   - Deploy to staging environment
   - Run smoke tests
   - Verify all functionality
   - Performance testing

4. **Production Deployment**
   - Blue-green deployment recommended
   - Gradual rollout if possible
   - Monitor logs and metrics
   - Quick rollback capability

### CI/CD Updates Required

Update `.github/workflows/ci.yml`:

```yaml
name: Java CI

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '17'
        cache: maven
    
    - name: Compile with Maven
      run: mvn -B -e clean compile
    
    - name: Run tests
      run: mvn -B test
    
    - name: Verify build
      run: mvn -B -e -DskipTests clean verify
```

---

## Migration Checklist for Other Workstreams

### Workstream B: Core Configuration Updates
- [ ] Update `pom.xml` Spring Boot parent to 3.2.x
- [ ] Update `java.version` property to 17
- [ ] Update `maven.compiler.release` to 17
- [ ] Update enforcer plugin Java version requirement
- [ ] Update SpringDoc dependency to 2.x
- [ ] Update JAXB runtime to 4.x

### Workstream C: Jakarta Namespace Migration
- [ ] Update all `javax.persistence.*` imports to `jakarta.persistence.*`
- [ ] Verify no remaining `javax.*` imports
- [ ] Run tests to verify JPA operations

### Workstream D: Security Configuration Migration
- [ ] Rewrite `SecurityConfig.java` using `SecurityFilterChain`
- [ ] Remove `WebSecurityConfigurerAdapter` extension
- [ ] Update to lambda-based DSL
- [ ] Test all security configurations

### Workstream E: Testing and Validation
- [ ] Update CI/CD workflow for Java 17
- [ ] Run all unit tests
- [ ] Run all integration tests
- [ ] Verify application startup
- [ ] Test all REST endpoints
- [ ] Verify Swagger UI
- [ ] Verify H2 console
- [ ] Performance testing

---

## Appendix: Complete File Inventory

### Files Requiring Changes

| File Path | Change Type | Priority |
|-----------|-------------|----------|
| `pom.xml` | Dependency updates | High |
| `src/main/java/.../model/Account.java` | Jakarta namespace | High |
| `src/main/java/.../model/Customer.java` | Jakarta namespace | High |
| `src/main/java/.../model/Transaction.java` | Jakarta namespace | High |
| `src/main/java/.../model/Contact.java` | Jakarta namespace | High |
| `src/main/java/.../model/Address.java` | Jakarta namespace | High |
| `src/main/java/.../model/BankInfo.java` | Jakarta namespace | High |
| `src/main/java/.../model/CustomerAccountXRef.java` | Jakarta namespace | High |
| `src/main/java/.../config/SecurityConfig.java` | Complete rewrite | High |
| `.github/workflows/ci.yml` | Java version update | Medium |

### Files NOT Requiring Changes

| File Path | Reason |
|-----------|--------|
| `src/main/java/.../BankingApplication.java` | No javax imports |
| `src/main/java/.../config/ApplicationConfig.java` | Uses OpenAPI v3 annotations |
| `src/main/java/.../controller/*.java` | No javax imports |
| `src/main/java/.../service/*.java` | No javax imports |
| `src/main/java/.../repository/*.java` | No javax imports |
| `src/main/java/.../domain/*.java` | No javax imports |
| `src/main/resources/application.yml` | Compatible configuration |

---

## References

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Security 6.0 Migration Guide](https://docs.spring.io/spring-security/reference/migration/index.html)
- [Jakarta EE 9 Migration](https://jakarta.ee/resources/jakarta-ee-9-migration/)
- [SpringDoc OpenAPI 2.x Migration](https://springdoc.org/v2/)
- [OpenRewrite Spring Boot 3 Recipes](https://docs.openrewrite.org/recipes/java/spring/boot3)
