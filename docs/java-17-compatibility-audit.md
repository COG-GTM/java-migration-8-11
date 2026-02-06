# Java 17 Migration - Compatibility Audit Report

**Project:** BankApp (Aplicacion-de-Banca-Spring-Boot)  
**Audit Date:** February 6, 2026  
**Current Java Version:** 11 (LTS)  
**Target Java Version:** 17 (LTS)  
**Current Spring Boot Version:** 2.7.18

## Executive Summary

This audit identifies deprecated APIs, removed JDK modules, and JDK internal API usage that may impact the migration from Java 11 to Java 17. The analysis was performed using `jdeps` and `jdeprscan` tools, along with manual code inspection.

**Key Findings:**

- No application code directly uses deprecated or removed Java 17 APIs
- Several third-party dependencies use JDK internal APIs that generate warnings
- One dependency (logback-classic 1.2.12) uses a removed JDK internal API (`sun.reflect.Reflection`)
- The application uses `javax.persistence.*` imports which will need migration to `jakarta.persistence.*` for Spring Boot 3.x
- `WebSecurityConfigurerAdapter` is deprecated in Spring Security 5.7+ and removed in 6.x

**Overall Migration Risk: LOW to MEDIUM**

The codebase is well-positioned for Java 17 migration with minimal changes required for the Java version itself. However, the recommended path to Spring Boot 3.x (which requires Java 17) involves more significant changes.

## 1. JDK Internal API Analysis (jdeps Results)

### 1.1 Critical - Removed Internal API

| Dependency | Internal API | Status | Risk |
|------------|--------------|--------|------|
| logback-classic-1.2.12.jar | sun.reflect.Reflection | REMOVED in Java 11+ | HIGH |

**Details:**
```
logback-classic-1.2.12.jar -> JDK removed internal API
   ch.qos.logback.classic.spi.PackagingDataCalculator -> sun.reflect.Reflection
```

**Remediation:** The current logback version (1.2.12) is managed by Spring Boot 2.7.18. While this API was removed in Java 11, logback has workarounds. For Java 17, upgrade to logback 1.4.x+ (requires Spring Boot 3.x) or ensure the current version handles the fallback gracefully.

### 1.2 JDK Unsupported APIs (sun.misc.Unsafe)

These dependencies use `sun.misc.Unsafe` which is in the `jdk.unsupported` module:

| Dependency | Classes Using Unsafe | Risk |
|------------|---------------------|------|
| aspectjweaver-1.9.7.jar | ClassLoaderWeavingAdaptor | MEDIUM |
| lombok-1.18.30.jar | AnnotationProcessorHider$AnnotationProcessor | MEDIUM |
| objenesis-3.2.jar | UnsafeFactoryInstantiator, UnsafeUtils | LOW |
| spring-core-5.3.31.jar | UnsafeFactoryInstantiator, UnsafeUtils | LOW |

**Impact:** These will generate warnings but continue to work on Java 17. The `jdk.unsupported` module is still available.

**Full jdeps Output:**
```
aspectjweaver-1.9.7.jar -> jdk.unsupported
   org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor -> sun.misc.Unsafe

lombok-1.18.30.jar -> jdk.compiler
lombok-1.18.30.jar -> jdk.unsupported
   lombok.javac.apt.Processor -> com.sun.tools.javac.processing.JavacFiler
   lombok.javac.apt.Processor -> com.sun.tools.javac.processing.JavacProcessingEnvironment
   lombok.javac.apt.Processor -> com.sun.tools.javac.util.Context
   lombok.javac.apt.Processor -> com.sun.tools.javac.util.Options
   lombok.launch.AnnotationProcessorHider$AnnotationProcessor -> sun.misc.Unsafe

objenesis-3.2.jar -> jdk.unsupported
   org.objenesis.instantiator.sun.UnsafeFactoryInstantiator -> sun.misc.Unsafe
   org.objenesis.instantiator.util.UnsafeUtils -> sun.misc.Unsafe

spring-core-5.3.31.jar -> jdk.unsupported
   org.springframework.objenesis.instantiator.sun.UnsafeFactoryInstantiator -> sun.misc.Unsafe
   org.springframework.objenesis.instantiator.util.UnsafeUtils -> sun.misc.Unsafe
```

### 1.3 JDK Compiler Internal APIs

| Dependency | Internal API | Suggested Replacement |
|------------|--------------|----------------------|
| lombok-1.18.30.jar | com.sun.tools.javac.processing.JavacFiler | javax.tools and javax.lang.model |
| lombok-1.18.30.jar | com.sun.tools.javac.processing.JavacProcessingEnvironment | javax.tools and javax.lang.model |
| lombok-1.18.30.jar | com.sun.tools.javac.util.Context | javax.tools and javax.lang.model |
| lombok-1.18.30.jar | com.sun.tools.javac.util.Options | javax.tools and javax.lang.model |

**Risk Level:** MEDIUM  
**Remediation:** Lombok 1.18.30 is already Java 17 compatible. These internal API usages are handled by Lombok's implementation.

## 2. Deprecated API Usage in Application Code

### 2.1 jdeprscan Results

**Result:** No deprecated-for-removal APIs found in application code (`target/classes`).

The application code does not directly use any APIs deprecated for removal in Java 17.

### 2.2 Compiler Warnings

During compilation with Java 11, the following deprecation warning was observed:

```
[WARNING] SecurityConfig.java:[16,37] org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter has been deprecated
```

**File:** `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`  
**Line:** 16

**Status:** Deprecated in Spring Security 5.7+, removed in Spring Security 6.x (Spring Boot 3.x)  
**Risk Level:** HIGH (for Spring Boot 3.x migration)  
**See:** `docs/spring-boot-3-breaking-changes.md` for detailed migration guidance

## 3. Dependency Inventory and Java 17 Compatibility

### 3.1 Core Dependencies

| Dependency | Current Version | Java 17 Compatible | Notes |
|------------|-----------------|-------------------|-------|
| Spring Boot | 2.7.18 | Yes | Supports Java 17 |
| Spring Framework | 5.3.31 | Yes | Full Java 17 support |
| Hibernate | 5.6.15.Final | Yes | Java 17 compatible |
| H2 Database | 2.1.214 | Yes | Java 17 compatible |
| Lombok | 1.18.30 | Yes | Full Java 17 support |
| Logback | 1.2.12 | Partial | Works with warnings |
| Jackson | 2.13.5 | Yes | Java 17 compatible |
| Tomcat Embed | 9.0.83 | Yes | Java 17 compatible |
| AspectJ | 1.9.7 | Yes | Java 17 compatible |
| SpringDoc OpenAPI | 1.6.15 | Yes | Java 17 compatible |

### 3.2 Test Dependencies

| Dependency | Current Version | Java 17 Compatible |
|------------|-----------------|-------------------|
| JUnit Jupiter | 5.8.2 | Yes |
| Mockito | 4.5.1 | Yes |
| AssertJ | 3.22.0 | Yes |
| Spring Security Test | 5.7.11 | Yes |

### 3.3 Jakarta EE Dependencies (Transitive)

| Module | Dependency | Version | Java 17 Notes |
|--------|------------|---------|---------------|
| Annotation | jakarta.annotation:jakarta.annotation-api | 1.3.5 | Compatible |
| Persistence | jakarta.persistence:jakarta.persistence-api | 2.2.3 | Compatible |
| Transaction | jakarta.transaction:jakarta.transaction-api | 1.3.3 | Compatible |
| Validation | jakarta.validation:jakarta.validation-api | 2.0.2 | Compatible |
| XML Bind | jakarta.xml.bind:jakarta.xml.bind-api | 2.3.3 | Compatible |
| Activation | jakarta.activation:jakarta.activation-api | 1.2.2 | Compatible |

**Note:** These are the `jakarta.*` namespace versions but still use `javax.*` package names. Spring Boot 3.x requires the newer Jakarta EE 9+ versions with `jakarta.*` package names.

## 4. Java 17 New Features Impact

### 4.1 Sealed Classes (JEP 409)

No impact - application does not use sealed classes.

### 4.2 Pattern Matching for instanceof (JEP 394)

No impact - can be adopted for cleaner code but not required.

### 4.3 Records (JEP 395)

No impact - DTOs could optionally be converted to records for immutability, but not required.

### 4.4 Text Blocks (JEP 378)

No impact - can be adopted for multi-line strings but not required.

### 4.5 Strong Encapsulation of JDK Internals (JEP 403)

**Impact:** MEDIUM

Java 17 strongly encapsulates JDK internals by default. The following JVM arguments may be needed if issues arise:

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
```

However, the current dependency versions should handle this gracefully.

## 5. Risk Assessment Summary

### 5.1 Risk Matrix

| Risk Category | Level | Count | Action Required |
|---------------|-------|-------|-----------------|
| Removed JDK Internal APIs | HIGH | 1 | Monitor logback behavior |
| Unsupported JDK Internal APIs | MEDIUM | 4 | No action (warnings only) |
| Deprecated Application Code | LOW | 1 | Plan for Spring Boot 3.x |
| Dependency Compatibility | LOW | 0 | All compatible |

### 5.2 Overall Migration Risk: LOW

For Java 11 to Java 17 migration alone, the risk is LOW. All dependencies are compatible with Java 17.

## 6. Remediation Plan

### 6.1 Required Changes (Before Java 17 Migration)

1. **Update pom.xml** (REQUIRED)
   ```xml
   <properties>
       <java.version>17</java.version>
       <maven.compiler.release>17</maven.compiler.release>
   </properties>
   ```

2. **Update Maven Enforcer Plugin** (REQUIRED)
   ```xml
   <requireJavaVersion>
       <version>[17,)</version>
   </requireJavaVersion>
   ```

3. **Update CI/CD** (REQUIRED)
   - Update GitHub Actions workflow to use JDK 17

### 6.2 Recommended Changes (During Migration)

1. **Monitor Logback Warnings** (MEDIUM Priority)
   - Current version works but may log warnings
   - Consider upgrading to Spring Boot 3.x for logback 1.4.x

2. **Test Thoroughly** (HIGH Priority)
   - Run full test suite on Java 17
   - Verify H2 console access
   - Test all REST endpoints
   - Verify Swagger UI functionality

### 6.3 Optional Changes (Post-Migration)

1. **Adopt Java 17 Language Features**
   - Use `var` for local variables where appropriate
   - Consider records for immutable DTOs
   - Use text blocks for multi-line strings
   - Use pattern matching for instanceof

## 7. Build Configuration Changes Required

### 7.1 pom.xml Updates

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### 7.2 Maven Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <release>17</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### 7.3 Maven Enforcer Plugin

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
                        <version>[17,)</version>
                    </requireJavaVersion>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 8. Testing Recommendations

1. Run full test suite on Java 17 JDK
2. Monitor for illegal reflective access warnings in logs
3. Verify H2 console access works correctly
4. Test Swagger UI functionality at `/swagger-ui.html`
5. Validate all REST endpoints with authentication
6. Test application startup and shutdown
7. Verify actuator endpoints

## 9. Conclusion

The BankApp codebase is well-positioned for Java 17 migration. The application code does not use any deprecated or removed APIs directly. The main concerns are related to third-party dependencies using JDK internal APIs, which generate warnings but do not prevent execution.

**Recommended Migration Approach:**

1. Update `pom.xml` to target Java 17
2. Update CI/CD workflows to use JDK 17
3. Run comprehensive tests
4. Monitor for warnings and address as needed
5. Plan for Spring Boot 3.x migration (separate effort)

## Appendix A: Tool Versions Used

- jdeps: OpenJDK 21.0.9 (with --multi-release 17 flag)
- jdeprscan: OpenJDK 21.0.9 (with --release 17 flag)
- Maven: 3.9.6 (via Maven Wrapper)

## Appendix B: Files Analyzed

```
src/main/java/com/coding/exercise/bankapp/
├── BankingApplication.java
├── config/
│   ├── ApplicationConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AccountController.java
│   └── CustomerController.java
├── domain/
│   ├── AccountInformation.java
│   ├── AddressDetails.java
│   ├── BankInformation.java
│   ├── ContactDetails.java
│   ├── CustomerDetails.java
│   ├── TransactionDetails.java
│   └── TransferDetails.java
├── model/
│   ├── Account.java
│   ├── Address.java
│   ├── BankInfo.java
│   ├── Contact.java
│   ├── Customer.java
│   ├── CustomerAccountXRef.java
│   └── Transaction.java
├── repository/
│   ├── AccountRepository.java
│   ├── CustomerAccountXRefRepository.java
│   ├── CustomerRepository.java
│   └── TransactionRepository.java
└── service/
    ├── BankingService.java
    ├── BankingServiceImpl.java
    └── helper/
        └── BankingServiceHelper.java
```

## Appendix C: Related Documentation

- [Java 8 to 11 Migration Notes](../MIGRATION_NOTES.md)
- [Java 11 Deprecated API Audit](deprecated-api-audit.md)
- [Spring Boot Compatibility Analysis](spring-boot-compatibility.md)
- [Spring Boot 3.x Breaking Changes](spring-boot-3-breaking-changes.md)
