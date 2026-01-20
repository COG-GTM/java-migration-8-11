# Java 17 Migration Plan

This document provides a detailed, phased migration plan for upgrading BankApp from Java 11 to Java 17 with Spring Boot 3.x.

## Migration Overview

| Aspect | Current State | Target State |
|--------|---------------|--------------|
| Java Version | 11 | 17 |
| Spring Boot | 2.1.4.RELEASE | 3.2.5 |
| API Documentation | Springfox 2.9.2 | SpringDoc OpenAPI 2.3.0 |
| JPA Namespace | javax.persistence | jakarta.persistence |
| Security Config | WebSecurityConfigurerAdapter | SecurityFilterChain |

## Pre-Migration Checklist

Before starting the migration, ensure the following:

- [ ] All current tests pass on Java 11
- [ ] Code is committed and pushed to version control
- [ ] Create a dedicated migration branch
- [ ] Backup any production data (if applicable)
- [ ] Review and understand all breaking changes
- [ ] Ensure development environment has Java 17 installed

## Phase 1: Environment Preparation

**Duration**: 1-2 hours
**Risk Level**: Low

### 1.1 Install Java 17

Ensure Java 17 is available in the development environment:

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Verify installation
java -version
# Expected: openjdk version "17.x.x"
```

### 1.2 Update Maven Wrapper (Optional)

Update the Maven wrapper to the latest version:

```bash
mvn wrapper:wrapper -Dmaven=3.9.6
```

### 1.3 Create Migration Branch

```bash
git checkout develop
git pull origin develop
git checkout -b feature/java-17-migration
```

## Phase 2: Update pom.xml

**Duration**: 30 minutes
**Risk Level**: Medium

### 2.1 Update Java Version Properties

Update the Java version in pom.xml:

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

### 2.2 Update Spring Boot Parent

Update the Spring Boot parent version:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
    <relativePath/>
</parent>
```

### 2.3 Remove Springfox Dependencies

Remove the deprecated Springfox dependencies:

```xml
<!-- REMOVE THESE -->
<!--
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
-->
```

### 2.4 Add SpringDoc OpenAPI Dependency

Add the SpringDoc OpenAPI dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2.5 Update JAXB Dependencies

Update JAXB dependencies to Jakarta versions:

```xml
<!-- REMOVE OLD JAXB -->
<!--
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>2.3.3</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.8</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
-->

<!-- ADD NEW JAKARTA JAXB (if needed) -->
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>4.0.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>4.0.4</version>
</dependency>
```

Note: Spring Boot 3.x may manage these dependencies automatically. Test without explicit versions first.

### 2.6 Verification

After updating pom.xml, verify the changes:

```bash
mvn dependency:tree
mvn validate
```

## Phase 3: Migrate javax.persistence to jakarta.persistence

**Duration**: 1 hour
**Risk Level**: Medium

### 3.1 Update Entity Classes

Update all 7 entity classes to use jakarta.persistence:

**Files to update:**
1. `src/main/java/com/coding/exercise/bankapp/model/Account.java`
2. `src/main/java/com/coding/exercise/bankapp/model/Address.java`
3. `src/main/java/com/coding/exercise/bankapp/model/BankInfo.java`
4. `src/main/java/com/coding/exercise/bankapp/model/Contact.java`
5. `src/main/java/com/coding/exercise/bankapp/model/Customer.java`
6. `src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java`
7. `src/main/java/com/coding/exercise/bankapp/model/Transaction.java`

**Automated Migration:**

```bash
# Run from project root
find src -name "*.java" -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} \;
```

### 3.2 Verification

Verify no javax.persistence imports remain:

```bash
grep -r "import javax\.persistence" src/
# Should return no results
```

## Phase 4: Migrate Spring Security Configuration

**Duration**: 1-2 hours
**Risk Level**: High

### 4.1 Current Security Configuration

The current `SecurityConfig.java` uses the deprecated `WebSecurityConfigurerAdapter`:

```java
// CURRENT (to be replaced)
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

### 4.2 New Security Configuration

Replace with the new component-based approach:

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
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        return http.build();
    }
}
```

### 4.3 Key Changes

| Old API | New API |
|---------|---------|
| `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` |
| `configure(HttpSecurity)` | `securityFilterChain(HttpSecurity)` |
| `authorizeRequests()` | `authorizeHttpRequests()` |
| `antMatchers()` | `requestMatchers()` |
| `.and()` chaining | Lambda-based configuration |

## Phase 5: Migrate Swagger to SpringDoc OpenAPI

**Duration**: 2-3 hours
**Risk Level**: Medium

### 5.1 Remove Old Configuration

Delete or replace `ApplicationConfig.java`:

```java
// REMOVE THIS ENTIRE CLASS
// @Configuration
// @EnableSwagger2
// public class ApplicationConfig { ... }
```

### 5.2 Create New OpenAPI Configuration

Create a new OpenAPI configuration:

```java
package com.coding.exercise.bankapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("BANKING APPLICATION REST API")
                .description("API for Banking Application.")
                .version("1.0.0"));
    }
}
```

### 5.3 Update Controller Annotations

Update annotations in both controllers:

**AccountController.java:**

```java
// OLD
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = { "Accounts and Transactions REST endpoints" })
@ApiOperation(value = "Get account details", notes = "Find account details by account number")
@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), ... })

// NEW
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accounts and Transactions REST endpoints")
@Operation(summary = "Get account details", description = "Find account details by account number")
@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"), ... })
```

**CustomerController.java:**

Apply the same annotation changes as AccountController.

### 5.4 Annotation Mapping Reference

| Springfox (Old) | SpringDoc (New) |
|-----------------|-----------------|
| `@Api(tags = {...})` | `@Tag(name = "...")` |
| `@ApiOperation(value = "...", notes = "...")` | `@Operation(summary = "...", description = "...")` |
| `@ApiResponse(code = 200, message = "...")` | `@ApiResponse(responseCode = "200", description = "...")` |
| `@ApiResponses` | `@ApiResponses` (same name, different package) |

### 5.5 Update application.yml

Add SpringDoc configuration:

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
```

### 5.6 New Swagger UI URL

After migration, Swagger UI will be available at:
- Old: `http://localhost:8989/bank-api/swagger-ui.html`
- New: `http://localhost:8989/bank-api/swagger-ui/index.html`

## Phase 6: Update Test Configuration

**Duration**: 1 hour
**Risk Level**: Low

### 6.1 Update Test Annotations

The test class uses JUnit 4 annotations. Update to JUnit 5:

```java
// OLD (JUnit 4)
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankingApplicationTests {
    @Test
    public void contextLoads() {
    }
}

// NEW (JUnit 5)
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankingApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

### 6.2 Key Test Changes

| JUnit 4 | JUnit 5 |
|---------|---------|
| `@RunWith(SpringRunner.class)` | Not needed |
| `@Test` (org.junit) | `@Test` (org.junit.jupiter.api) |
| `@Before` | `@BeforeEach` |
| `@After` | `@AfterEach` |
| `@BeforeClass` | `@BeforeAll` |
| `@AfterClass` | `@AfterAll` |
| `@Ignore` | `@Disabled` |

## Phase 7: Compile and Test

**Duration**: 1-2 hours
**Risk Level**: Medium

### 7.1 Clean and Compile

```bash
# Set Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Clean and compile
mvn clean compile
```

### 7.2 Run Tests

```bash
mvn test
```

### 7.3 Package Application

```bash
mvn package -DskipTests
```

### 7.4 Run Application

```bash
mvn spring-boot:run
```

### 7.5 Verification Checklist

- [ ] Application starts without errors
- [ ] H2 Console accessible at `/bank-api/h2-console/`
- [ ] Swagger UI accessible at `/bank-api/swagger-ui/index.html`
- [ ] All REST endpoints respond correctly
- [ ] Authentication works as expected

## Phase 8: Java 17 Feature Opportunities

**Duration**: Optional, 2-4 hours
**Risk Level**: Low

While not required for the migration, consider adopting these Java 17 features:

### 8.1 Text Blocks (Java 15+)

For multi-line strings in error messages or SQL queries:

```java
// Before
String message = "Customer Number " + customerNumber + " not found.";

// After (optional enhancement)
String message = """
    Customer Number %d not found.
    Please verify the customer number and try again.
    """.formatted(customerNumber);
```

### 8.2 Records (Java 16+)

DTOs could potentially be converted to records:

```java
// Current DTO
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransferDetails {
    private Long fromAccountNumber;
    private Long toAccountNumber;
    private Double transferAmount;
}

// Potential Record (if immutability is acceptable)
public record TransferDetails(
    Long fromAccountNumber,
    Long toAccountNumber,
    Double transferAmount
) {}
```

Note: Records are immutable and may not be suitable for all DTOs, especially those used with frameworks expecting setters.

### 8.3 Sealed Classes (Java 17)

For type hierarchies that should be restricted:

```java
// Example (if applicable)
public sealed interface BankingException permits
    AccountNotFoundException,
    InsufficientFundsException,
    CustomerNotFoundException {}
```

### 8.4 Pattern Matching for instanceof (Java 16+)

```java
// Before
if (obj instanceof String) {
    String s = (String) obj;
    // use s
}

// After
if (obj instanceof String s) {
    // use s directly
}
```

## Post-Migration Checklist

After completing all phases:

- [ ] All tests pass
- [ ] Application starts successfully
- [ ] All endpoints work correctly
- [ ] Swagger UI is accessible
- [ ] H2 Console is accessible
- [ ] No deprecation warnings in logs
- [ ] Code review completed
- [ ] Documentation updated
- [ ] CI/CD pipeline updated for Java 17

## Rollback Plan

If critical issues are encountered:

1. **Immediate Rollback**: Revert to the previous branch
   ```bash
   git checkout develop
   ```

2. **Partial Rollback**: Revert specific commits
   ```bash
   git revert <commit-hash>
   ```

3. **Environment Rollback**: Switch back to Java 11
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
   ```

## Timeline Summary

| Phase | Duration | Risk |
|-------|----------|------|
| Phase 1: Environment Preparation | 1-2 hours | Low |
| Phase 2: Update pom.xml | 30 minutes | Medium |
| Phase 3: javax to jakarta Migration | 1 hour | Medium |
| Phase 4: Spring Security Migration | 1-2 hours | High |
| Phase 5: Swagger to SpringDoc Migration | 2-3 hours | Medium |
| Phase 6: Update Test Configuration | 1 hour | Low |
| Phase 7: Compile and Test | 1-2 hours | Medium |
| Phase 8: Java 17 Features (Optional) | 2-4 hours | Low |
| **Total Estimated Time** | **8-15 hours** | - |

## References

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Java 17 Features](https://openjdk.org/projects/jdk/17/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
