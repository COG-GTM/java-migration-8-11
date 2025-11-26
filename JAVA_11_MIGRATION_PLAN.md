# Java 8 to Java 11 Migration Plan

## Executive Summary

This document outlines a comprehensive migration plan for upgrading the BankApp Spring Boot application from Java 8 to Java 11. The migration involves updating the Java version, upgrading Spring Boot from 2.1.4.RELEASE to 2.7.x, migrating from Springfox to SpringDoc OpenAPI, updating security configurations, and refreshing all documentation.

## Current State Analysis

### Technology Stack (Before Migration)

| Component | Current Version | Status |
|-----------|-----------------|--------|
| Java | 1.8 | End of public updates |
| Spring Boot | 2.1.4.RELEASE (April 2019) | No longer supported |
| Springfox Swagger2 | 2.9.2 | Deprecated, compatibility issues |
| Springfox Swagger UI | 2.10.0 | Deprecated, compatibility issues |
| H2 Database | Inherited from parent | Runtime scope |
| Lombok | Inherited from parent | Optional |

### Key Files Inventory

The following files will require modifications during this migration:

| File | Type | Changes Required |
|------|------|------------------|
| `pom.xml` | Build Config | Java version, Spring Boot version, Swagger dependencies |
| `SecurityConfig.java` | Java Source | Replace deprecated `WebSecurityConfigurerAdapter` |
| `ApplicationConfig.java` | Java Source | Migrate from Springfox to SpringDoc |
| `application.yml` | Config | Add SpringDoc configuration |
| `README.md` | Documentation | Update Java version prerequisite |
| `README_NEW.md` | Documentation | Update Java version prerequisite |

---

## Phase 1: Dependency Upgrades

### 1.1 Java Version Update

**File**: `pom.xml` (line 18)

**Current Configuration**:
```xml
<properties>
    <java.version>1.8</java.version>
</properties>
```

**Target Configuration**:
```xml
<properties>
    <java.version>11</java.version>
</properties>
```

**Rationale**: Java 11 is an LTS (Long-Term Support) release with significant improvements including the HTTP Client API, local variable type inference (`var`), and better performance through the G1 garbage collector as default.

### 1.2 Spring Boot Version Upgrade

**File**: `pom.xml` (lines 5-10)

**Current Configuration**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
    <relativePath/>
</parent>
```

**Target Configuration**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
    <relativePath/>
</parent>
```

**Rationale**: Spring Boot 2.7.18 is the final release of the 2.7.x line (released November 2023) and provides full Java 11 support while maintaining backward compatibility. It includes security patches, bug fixes, and dependency updates. Spring Boot 2.1.4.RELEASE from April 2019 is no longer receiving updates.

**Note**: All Spring Boot starter dependencies (actuator, data-jpa, security, web, devtools, test) inherit their versions from the parent POM and will automatically upgrade.

### 1.3 Swagger Migration (Springfox to SpringDoc)

**File**: `pom.xml` (lines 54-63)

**Current Configuration** (Remove):
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.10.0</version>
</dependency>
```

**Target Configuration** (Add):
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version>
</dependency>
```

**Rationale**: Springfox has known compatibility issues with Spring Boot 2.6+ due to changes in Spring MVC's path matching strategy. SpringDoc OpenAPI is actively maintained, fully compatible with modern Spring Boot versions, and provides equivalent functionality with a simpler configuration model.

---

## Phase 2: Code Changes

### 2.1 Security Configuration Update

**File**: `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`

**Issue**: `WebSecurityConfigurerAdapter` is deprecated in Spring Security 5.7+ (included in Spring Boot 2.7.x).

**Current Implementation**:
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

**Target Implementation**:
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
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf().disable()
            .headers().frameOptions().disable();
        
        return httpSecurity.build();
    }
}
```

**Key Changes**:
- Remove inheritance from `WebSecurityConfigurerAdapter`
- Add `@EnableWebSecurity` annotation
- Replace `configure(HttpSecurity)` method with `SecurityFilterChain` bean
- Use lambda-based configuration with `authorizeHttpRequests()`
- Add SpringDoc endpoints to permitted paths

### 2.2 Swagger/OpenAPI Configuration Update

**File**: `src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java`

**Current Implementation**:
```java
package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("BANKING APPLICATION REST API")
                .description("API for Banking Application.")
                .version("1.0.0").build();
    }
}
```

**Target Implementation**:
```java
package com.coding.exercise.bankapp.config;

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

**Key Changes**:
- Remove `@EnableSwagger2` annotation (not needed with SpringDoc)
- Replace Springfox imports with SpringDoc/OpenAPI imports
- Replace `Docket` bean with `OpenAPI` bean
- Simplified configuration model

---

## Phase 3: Configuration Updates

### 3.1 Application Configuration

**File**: `src/main/resources/application.yml`

**Current Configuration**:
```yaml
server.port: 8989
server.servlet.context-path: /bank-api

spring.security.user.name: bankapp
spring.security.user.password: changeit

spring:
  h2:
    console:
      enabled: true
```

**Target Configuration** (Add SpringDoc settings):
```yaml
server:
  port: 8989
  servlet:
    context-path: /bank-api

spring:
  security:
    user:
      name: bankapp
      password: changeit
  h2:
    console:
      enabled: true
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

**Key Changes**:
- Restructured YAML for consistency
- Added `spring.mvc.pathmatch.matching-strategy` for backward compatibility
- Added SpringDoc configuration to maintain the same Swagger UI URL path

### 3.2 Swagger UI URL Change

**Important**: The Swagger UI URL will change with SpringDoc:

| Component | Old URL (Springfox) | New URL (SpringDoc) |
|-----------|---------------------|---------------------|
| Swagger UI | `/bank-api/swagger-ui.html` | `/bank-api/swagger-ui.html` (configured) |
| API Docs | `/bank-api/v2/api-docs` | `/bank-api/v3/api-docs` |

The SpringDoc configuration above maintains backward compatibility for the Swagger UI URL.

---

## Phase 4: Documentation Updates

### 4.1 README.md Updates

**File**: `README.md`

**Changes Required**:

1. **Title** (line 1): Update from "Java8" to "Java 11"
   - Current: `# Banking Application using Java8, Spring Boot, Spring Security and H2 DB`
   - Target: `# Banking Application using Java 11, Spring Boot, Spring Security and H2 DB`

2. **Prerequisites** (line 41): Update Java version
   - Current: `* Java 8`
   - Target: `* Java 11`

3. **Maven Dependencies** (lines 55-56): Update Swagger references
   - Current: `springfox-swagger2` and `springfox-swagger-ui`
   - Target: `springdoc-openapi-ui`

### 4.2 README_NEW.md Updates

**File**: `README_NEW.md`

**Changes Required**:

1. **Prerequisites** (line 40): Update Java version
   - Current: `- Java 8 or higher`
   - Target: `- Java 11 or higher`

2. **Troubleshooting Table** (line 314): Update Java reference
   - Current: `Missing Java 8`
   - Target: `Missing Java 11`

---

## Phase 5: Build Verification

### 5.1 Pre-Migration Verification

Before starting the migration, verify the current build works:

```bash
cd ~/repos/java-migration-8-11

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package

# Verify JAR execution
java -jar target/bank-app-1.0.0.jar
```

### 5.2 Post-Migration Verification

After completing all migration steps:

```bash
# Clean build with new Java version
mvn clean compile

# Run all tests
mvn test

# Package the application
mvn clean package

# Start the application
mvn spring-boot:run
```

### 5.3 Functional Verification Checklist

| Test | Command/URL | Expected Result |
|------|-------------|-----------------|
| Application starts | `mvn spring-boot:run` | No errors, port 8989 bound |
| Health endpoint | `curl -u bankapp:changeit http://localhost:8989/bank-api/actuator/health` | `{"status":"UP"}` |
| Swagger UI loads | `http://localhost:8989/bank-api/swagger-ui.html` | Swagger UI page renders |
| H2 Console accessible | `http://localhost:8989/bank-api/h2-console/` | H2 login page renders |
| Customer API works | `curl -u bankapp:changeit http://localhost:8989/bank-api/customers/all` | JSON response |

### 5.4 Maven Wrapper Compatibility

The Maven wrapper scripts (`mvnw` and `mvnw.cmd`) should work without modification. However, ensure the build environment has Java 11 installed:

```bash
# Verify Java version
java -version
# Should show: openjdk version "11.x.x" or similar

# Verify Maven uses correct Java
./mvnw -version
# Should show Java version 11
```

---

## File Count Summary

### Minimum Files to Modify (Core Migration)

| File | Changes |
|------|---------|
| `pom.xml` | Java version, Spring Boot version, Swagger dependencies |

**Total**: 1 file (minimal migration)

### Comprehensive Migration (Recommended)

| File | Changes |
|------|---------|
| `pom.xml` | Java version, Spring Boot version, Swagger dependencies |
| `SecurityConfig.java` | Replace deprecated WebSecurityConfigurerAdapter |
| `ApplicationConfig.java` | Migrate from Springfox to SpringDoc |
| `application.yml` | Add SpringDoc configuration, restructure YAML |
| `README.md` | Update Java version references |
| `README_NEW.md` | Update Java version references |

**Total**: 6 files (comprehensive migration)

---

## Risk Assessment

### Low Risk
- Java version property change in `pom.xml`
- Documentation updates in README files
- Application configuration updates

### Medium Risk
- Spring Boot version upgrade (may introduce breaking changes in dependencies)
- Swagger migration (API documentation URLs may change)

### High Risk
- Security configuration changes (incorrect configuration could expose endpoints or break authentication)

### Mitigation Strategies

1. **Incremental Testing**: Test after each phase before proceeding
2. **Backup**: Create a git branch before starting migration
3. **Rollback Plan**: Keep the original `pom.xml` available for quick rollback
4. **Security Review**: Carefully test all authenticated endpoints after security config changes

---

## Migration Execution Order

For the safest migration path, execute changes in this order:

1. Create a new git branch for the migration
2. Update `pom.xml` (Java version only) and verify build
3. Update `pom.xml` (Spring Boot version) and verify build
4. Update `SecurityConfig.java` and verify security works
5. Update `pom.xml` (Swagger dependencies) and `ApplicationConfig.java`
6. Update `application.yml` with SpringDoc configuration
7. Run full test suite
8. Update documentation files
9. Perform manual functional testing
10. Create pull request for review

---

## Post-Migration Considerations

### Future Upgrades

After successfully migrating to Java 11 and Spring Boot 2.7.x, consider planning for:

- **Java 17**: The next LTS version with additional features
- **Spring Boot 3.x**: Requires Java 17 minimum, includes Jakarta EE 9+ namespace changes

### Monitoring

After deployment, monitor for:
- Application startup time changes
- Memory usage differences
- Any deprecation warnings in logs

---

## Appendix: Complete pom.xml After Migration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>
    <groupId>com.coding.exercise</groupId>
    <artifactId>bank-app</artifactId>
    <version>1.0.0</version>
    <name>BankApp</name>
    <description>Bank App Spring Boot Project</description>

    <properties>
        <java.version>11</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-ui</artifactId>
            <version>1.7.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

---

## Document Information

| Field | Value |
|-------|-------|
| Document Version | 1.0 |
| Created | November 2025 |
| Target Java Version | 11 (LTS) |
| Target Spring Boot Version | 2.7.18 |
| Repository | COG-GTM/java-migration-8-11 |
