# Java 17 Migration Analysis - Phase 1 Pre-Migration Report

## Executive Summary

This document provides a comprehensive analysis of the BankApp codebase in preparation for migrating from Java 11 to Java 17. The analysis covers JDK tool outputs, dependency compatibility, code issues, risk assessment, and recommendations.

**Overall Assessment**: The migration from Java 11 to Java 17 is **LOW RISK** with the current Spring Boot 2.7.18 configuration. No blocking issues were identified, and all dependencies are compatible with Java 17.

## 1. JDeps/JDeprscan Output

### 1.1 JDK Internal API Analysis (`jdeps --jdk-internals`)

```
Result: No JDK internal API usage detected
```

The codebase does not use any JDK internal APIs (`sun.*` or `com.sun.*` packages). This is excellent for Java 17 compatibility, as these APIs are encapsulated by default in Java 17's strong encapsulation.

### 1.2 JDeps Summary (`jdeps -summary`)

```
classes -> java.base
classes -> not found
```

The application classes only depend on `java.base` module from the JDK. The "not found" entries refer to Spring Framework and other third-party dependencies (expected behavior when analyzing only compiled classes without the full classpath).

### 1.3 JDeprscan Analysis (`jdeprscan --release 17`)

```
Result: No deprecated JDK API usage detected in application code
```

The jdeprscan tool reported errors about missing Spring Framework classes (expected when scanning without dependencies), but **no deprecated JDK APIs were flagged** in the application code itself.

### 1.4 Compilation Warnings

During compilation with `./mvnw clean compile`, the following warning was observed:

```
WARNING: org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter 
         has been deprecated
```

This is a Spring Security deprecation (not a JDK deprecation) and is addressed in the recommendations section.

## 2. Dependency Compatibility Matrix

### 2.1 Core Dependencies (Managed by Spring Boot 2.7.18)

| Dependency | Current Version | Java 17 Compatible | Notes |
|------------|-----------------|-------------------|-------|
| Spring Boot | 2.7.18 | Yes | Official Java 17 support since 2.5.0 |
| Spring Framework | 5.3.31 | Yes | Full Java 17 support |
| Hibernate ORM | 5.6.15.Final | Yes | Java 17 compatible |
| H2 Database | 2.1.214 | Yes | Java 17 compatible |
| Lombok | 1.18.30 | Yes | Full Java 17 support |
| Tomcat Embedded | 9.0.83 | Yes | Java 17 compatible |
| Jackson | 2.13.5 | Yes | Java 17 compatible |
| Logback | 1.2.12 | Yes | Java 17 compatible |
| HikariCP | 4.0.3 | Yes | Java 17 compatible |
| Micrometer | 1.9.17 | Yes | Java 17 compatible |

### 2.2 Explicit Dependencies

| Dependency | Current Version | Java 17 Compatible | Notes |
|------------|-----------------|-------------------|-------|
| springdoc-openapi-ui | 1.6.15 | Yes | Supports Java 17; consider upgrading to 1.7.x for better Java 17 optimization |
| jaxb-runtime | 2.3.8 | Yes | Compatible with Java 17; JAXB was removed from JDK in Java 11, this dependency provides it |
| JUnit Jupiter | 5.8.2 | Yes | Full Java 17 support |
| Mockito | 4.5.1 | Yes | Java 17 compatible |
| AssertJ | 3.22.0 | Yes | Java 17 compatible |

### 2.3 Build Plugins

| Plugin | Current Version | Java 17 Compatible | Notes |
|--------|-----------------|-------------------|-------|
| maven-compiler-plugin | 3.11.0 | Yes | Supports Java 17 compilation |
| maven-surefire-plugin | 3.2.5 | Yes | Java 17 compatible |
| maven-failsafe-plugin | 3.2.5 | Yes | Java 17 compatible |
| maven-enforcer-plugin | 3.5.0 | Yes | Java 17 compatible |
| spring-boot-maven-plugin | 2.7.18 | Yes | Java 17 compatible |

### 2.4 Spring Boot 2.7.18 Java 17 Support Verification

According to the Spring Boot documentation (referenced in `docs/spring-boot-compatibility.md`):

| Java Version | Minimum Spring Boot Version | Recommended Version |
|--------------|----------------------------|---------------------|
| Java 11 | 2.1.0.RELEASE | 2.7.18 |
| **Java 17** | **2.5.0.RELEASE** | **2.7.18** |
| Java 21 | 3.1.0 | 3.4.x |

**Conclusion**: Spring Boot 2.7.18 officially supports Java 17.

## 3. Code Issues Found

### 3.1 Deprecated API Usage

#### No JDK Deprecated APIs Found

A comprehensive search of the codebase found:
- **No `sun.*` imports**: 0 occurrences
- **No `com.sun.*` imports**: 0 occurrences
- **No SecurityManager usage**: 0 occurrences
- **No AccessController usage**: 0 occurrences
- **No @Deprecated annotations**: 0 occurrences in application code

#### Spring Security Deprecation (Non-Blocking)

**File**: `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // ...
    }
}
```

**Issue**: `WebSecurityConfigurerAdapter` is deprecated in Spring Security 5.7+ (part of Spring Boot 2.7.x). This is a Spring deprecation, not a Java 17 issue.

**Impact**: The code will continue to work on Java 17 with Spring Boot 2.7.18. However, this should be addressed before upgrading to Spring Boot 3.x.

### 3.2 JPA/Hibernate Annotations

The codebase uses `javax.persistence.*` annotations (e.g., in `Account.java`, `Customer.java`):

```java
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
// etc.
```

**Impact for Java 17**: No impact. These annotations work correctly with Java 17 and Spring Boot 2.7.18.

**Future Consideration**: When migrating to Spring Boot 3.x, these will need to change to `jakarta.persistence.*`.

### 3.3 Source Files Reviewed

All 26 source files were analyzed:

| Package | Files | Issues Found |
|---------|-------|--------------|
| `config/` | 2 | WebSecurityConfigurerAdapter deprecation (Spring, not JDK) |
| `controller/` | 2 | None |
| `domain/` | 7 | None |
| `model/` | 6 | None |
| `repository/` | 4 | None |
| `service/` | 3 | None |
| Root | 1 | None |
| **Test** | 1 | None |

## 4. Risk Assessment

### 4.1 Risk Summary

| Risk Category | Level | Description |
|---------------|-------|-------------|
| JDK API Compatibility | **LOW** | No JDK internal or deprecated APIs used |
| Dependency Compatibility | **LOW** | All dependencies support Java 17 |
| Build Tool Compatibility | **LOW** | Maven plugins support Java 17 |
| Runtime Compatibility | **LOW** | Spring Boot 2.7.18 officially supports Java 17 |
| Code Changes Required | **NONE** | No code changes needed for Java 17 |

### 4.2 Potential Blockers

**None identified.** The migration can proceed without any blocking issues.

### 4.3 Items Requiring Attention (Non-Blocking)

1. **WebSecurityConfigurerAdapter Deprecation**
   - Severity: Low (warning only)
   - Impact: Compilation warning, no runtime impact
   - Recommendation: Plan migration to component-based security configuration for Spring Boot 3.x

2. **Strong Encapsulation**
   - Java 17 enforces strong encapsulation of JDK internals by default
   - Impact: None for this codebase (no internal API usage detected)
   - No `--add-opens` or `--add-exports` JVM flags needed

## 5. Recommendations

### 5.1 Pre-Migration Checklist

Before proceeding with the Java 17 migration:

- [x] Verify all dependencies support Java 17 (Completed - all compatible)
- [x] Check for JDK internal API usage (Completed - none found)
- [x] Check for deprecated JDK API usage (Completed - none found)
- [x] Verify build tools support Java 17 (Completed - all compatible)
- [ ] Run full test suite on Java 17 (Recommended before migration)

### 5.2 Configuration Changes for Migration

The following changes will be needed in `pom.xml` during Phase 2:

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

And update the enforcer plugin:

```xml
<requireJavaVersion>
    <version>[17,)</version>
</requireJavaVersion>
```

### 5.3 Optional Dependency Updates

While not required, consider these updates for better Java 17 optimization:

| Dependency | Current | Recommended | Reason |
|------------|---------|-------------|--------|
| springdoc-openapi-ui | 1.6.15 | 1.7.0+ | Better Java 17 support |
| jaxb-runtime | 2.3.8 | 4.0.x | Latest version (optional, 2.3.8 works fine) |

**Note**: These updates are optional and can be done as part of the migration or separately.

### 5.4 Future Considerations (Spring Boot 3.x Migration)

When planning the eventual migration to Spring Boot 3.x (which requires Java 17 minimum):

1. **SecurityConfig Migration**: Replace `WebSecurityConfigurerAdapter` with `SecurityFilterChain` bean:

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}
```

2. **JPA Namespace Migration**: Change `javax.persistence.*` to `jakarta.persistence.*`

3. **Validation Namespace Migration**: Change `javax.validation.*` to `jakarta.validation.*`

## 6. Conclusion

The BankApp codebase is **ready for Java 17 migration** with Spring Boot 2.7.18. The analysis found:

- **No blocking issues** that would prevent migration
- **No JDK internal API usage** that would require `--add-opens` flags
- **No deprecated JDK API usage** that would cause runtime issues
- **All dependencies are compatible** with Java 17
- **All build tools support** Java 17

The migration can proceed to Phase 2 (actual configuration changes) with confidence. The only item requiring future attention is the deprecated `WebSecurityConfigurerAdapter`, which should be addressed when planning the Spring Boot 3.x migration.

---

**Analysis Date**: January 30, 2026  
**Analyzed By**: Devin (Automated Analysis)  
**Repository**: COG-GTM/Aplicacion-de-Banca-Spring-Boot  
**Current Java Version**: 11  
**Target Java Version**: 17  
**Spring Boot Version**: 2.7.18
