# Java 11 to Java 17 Migration Analysis

## Epic: MBA-1031 - Preparation and Analysis for Java 11 to 17 Migration

This document provides a comprehensive analysis of the BankApp codebase for migration from Java 11 to Java 17, including the required Spring Boot 2.7.18 to 3.2.x upgrade.

## Executive Summary

The BankApp project currently uses Java 8 (configured as 1.8 in pom.xml) with Spring Boot 2.1.4.RELEASE. The migration to Java 17 requires a significant upgrade path that includes updating to Spring Boot 3.2.x, migrating from javax.* to jakarta.* namespaces, replacing Springfox Swagger with SpringDoc OpenAPI, and updating the Spring Security configuration to use the new SecurityFilterChain API.

## Current Project State

### Build Configuration

| Component | Current Version | Target Version |
|-----------|-----------------|----------------|
| Java | 1.8 | 17 |
| Spring Boot | 2.1.4.RELEASE | 3.2.5 |
| Maven Compiler | Default | 3.12.x |

### Current pom.xml Configuration

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
</parent>

<properties>
    <java.version>1.8</java.version>
</properties>
```

### Current Dependencies

| Dependency | Current Version | Notes |
|------------|-----------------|-------|
| spring-boot-starter-actuator | 2.1.4 (managed) | Needs upgrade |
| spring-boot-starter-data-jpa | 2.1.4 (managed) | Needs upgrade |
| spring-boot-starter-security | 2.1.4 (managed) | Needs upgrade |
| spring-boot-starter-web | 2.1.4 (managed) | Needs upgrade |
| spring-boot-devtools | 2.1.4 (managed) | Needs upgrade |
| H2 Database | Managed | Compatible with Java 17 |
| Lombok | Managed | Needs 1.18.30+ for Java 17 |
| Springfox Swagger | 2.9.2 | Must be replaced with SpringDoc |

## javax.* to jakarta.* Migration Analysis

### Files Requiring Migration

The following 7 entity classes contain javax.persistence.* imports that must be migrated to jakarta.persistence.*:

#### 1. Account.java (src/main/java/com/coding/exercise/bankapp/model/Account.java)

Current imports requiring migration:
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

#### 2. Address.java (src/main/java/com/coding/exercise/bankapp/model/Address.java)

Current imports requiring migration:
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

#### 3. BankInfo.java (src/main/java/com/coding/exercise/bankapp/model/BankInfo.java)

Current imports requiring migration:
```java
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
```

#### 4. Contact.java (src/main/java/com/coding/exercise/bankapp/model/Contact.java)

Current imports requiring migration:
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

#### 5. Customer.java (src/main/java/com/coding/exercise/bankapp/model/Customer.java)

Current imports requiring migration:
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

#### 6. CustomerAccountXRef.java (src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java)

Current imports requiring migration:
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
```

#### 7. Transaction.java (src/main/java/com/coding/exercise/bankapp/model/Transaction.java)

Current imports requiring migration:
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
```

### Summary of javax.* Usage

| Package | Occurrences | Files Affected |
|---------|-------------|----------------|
| javax.persistence.* | 47 imports | 7 files |
| javax.validation.* | 0 imports | 0 files |
| javax.servlet.* | 0 imports | 0 files |
| javax.annotation.* | 0 imports | 0 files |

## Spring Security Migration Analysis

### Current Configuration (SecurityConfig.java)

The current security configuration uses the deprecated `WebSecurityConfigurerAdapter`:

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

### Required Migration

`WebSecurityConfigurerAdapter` has been removed in Spring Security 6.x (used by Spring Boot 3.x). The configuration must be migrated to use `SecurityFilterChain` bean:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}
```

### Key Changes

| Old API | New API |
|---------|---------|
| `WebSecurityConfigurerAdapter` | `SecurityFilterChain` bean |
| `authorizeRequests()` | `authorizeHttpRequests()` |
| `antMatchers()` | `requestMatchers()` |
| `csrf().disable()` | `csrf(csrf -> csrf.disable())` |
| `headers().frameOptions().disable()` | `headers(headers -> headers.frameOptions(frame -> frame.disable()))` |

## Swagger/OpenAPI Migration Analysis

### Current Configuration (ApplicationConfig.java)

The current configuration uses Springfox Swagger 2.9.2:

```java
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

### Current Swagger Annotations in Controllers

The controllers use Springfox annotations that need to be migrated:

```java
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = { "Accounts and Transactions REST endpoints" })
@ApiOperation(value = "Get account details", notes = "Find account details by account number")
@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), ... })
```

### Required Migration

Springfox is incompatible with Spring Boot 3.x and must be replaced with SpringDoc OpenAPI 2.x.

#### Dependency Changes

Remove:
```xml
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
```

Add:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

#### Configuration Changes

New OpenAPI configuration:
```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BANKING APPLICATION REST API")
                        .description("API for Banking Application.")
                        .version("1.0.0"));
    }
}
```

#### Annotation Migration

| Springfox Annotation | SpringDoc Annotation |
|---------------------|---------------------|
| `@Api(tags = {...})` | `@Tag(name = "...")` |
| `@ApiOperation(value = "...", notes = "...")` | `@Operation(summary = "...", description = "...")` |
| `@ApiResponse(code = 200, message = "...")` | `@ApiResponse(responseCode = "200", description = "...")` |
| `@ApiResponses` | `@ApiResponses` (same name, different package) |

#### URL Changes

| Old URL | New URL |
|---------|---------|
| `/swagger-ui.html` | `/swagger-ui/index.html` |
| `/v2/api-docs` | `/v3/api-docs` |

## Dependency Compatibility Analysis

### H2 Database

H2 Database is compatible with Java 17. The managed version from Spring Boot 3.2.x will work correctly. No special migration steps required.

### Lombok

Lombok requires version 1.18.30 or higher for full Java 17 compatibility. Spring Boot 3.2.x manages Lombok 1.18.30+, so no explicit version specification is needed when using the managed dependency.

### JAXB Runtime Requirements

Spring Boot 3.x no longer includes JAXB by default. If XML processing is needed, add:

```xml
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
</dependency>
```

Note: The current BankApp does not appear to use JAXB directly, so this may not be required.

### Spring Data JPA / Hibernate

Spring Boot 3.x uses Hibernate 6.x which has some behavioral changes:

1. UUID generation strategy changes - the current `@GeneratedValue(strategy=GenerationType.AUTO)` may behave differently
2. Temporal type handling improvements
3. Query parameter binding changes

## Java 17 Features and Considerations

### New Language Features Available

After migration, the following Java 17 features can be utilized:

1. **Records** - Can be used for DTOs (domain classes like CustomerDetails, AccountInformation, etc.)
2. **Sealed Classes** - Can be used for type hierarchies
3. **Pattern Matching for instanceof** - Simplifies type checking
4. **Text Blocks** - Multi-line strings for SQL queries, JSON, etc.
5. **Switch Expressions** - Enhanced switch statements

### Security Manager Deprecation

Java 17 deprecates the Security Manager. If the application uses Security Manager features, they should be removed or replaced with alternative security mechanisms.

### Strong Encapsulation of JDK Internal APIs

Java 17 strongly encapsulates JDK internal APIs. The following may be affected:

1. Reflection on internal classes may fail
2. Some libraries may require `--add-opens` JVM arguments
3. Check for any usage of `sun.*` or `com.sun.*` packages

### Recommended JVM Arguments for Migration Testing

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

## Test Migration Analysis

### Current Test Configuration

The current test class uses JUnit 4 with Spring Runner:

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BankingApplicationTests {
    @Test
    public void contextLoads() {
    }
}
```

### Required Migration

Spring Boot 3.x uses JUnit 5 by default. The test should be migrated:

```java
@SpringBootTest
class BankingApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

Key changes:
- Remove `@RunWith(SpringRunner.class)` (not needed with JUnit 5)
- Change `org.junit.Test` to `org.junit.jupiter.api.Test`
- Class and method visibility can be package-private

## Migration Risks

### High Risk

1. **javax.* to jakarta.* Migration** - All 7 entity classes require import changes. This is a breaking change that affects the entire persistence layer.

2. **Spring Security Configuration** - The `WebSecurityConfigurerAdapter` removal requires complete rewrite of security configuration.

3. **Springfox to SpringDoc Migration** - Requires changes to configuration and all controller annotations (2 controllers affected).

### Medium Risk

1. **Hibernate 6.x Behavioral Changes** - UUID generation and temporal handling may behave differently. Thorough testing required.

2. **JUnit 4 to JUnit 5 Migration** - Test framework changes may affect test execution.

3. **Strong Encapsulation** - Some reflection-based operations may fail without proper `--add-opens` arguments.

### Low Risk

1. **H2 Database Compatibility** - H2 is compatible with Java 17.

2. **Lombok Compatibility** - Lombok 1.18.30+ supports Java 17.

3. **Spring Boot Actuator** - Should work without changes after Spring Boot upgrade.

## Recommended Migration Phases

### Phase 1: Build Configuration Update

1. Update pom.xml with Java 17 configuration
2. Update Spring Boot parent to 3.2.5
3. Update Maven plugins

### Phase 2: javax.* to jakarta.* Migration

1. Update all 7 entity classes with jakarta.persistence.* imports
2. Verify JPA mappings work correctly

### Phase 3: Spring Security Migration

1. Remove WebSecurityConfigurerAdapter
2. Implement SecurityFilterChain bean
3. Update authorization rules

### Phase 4: Swagger to SpringDoc Migration

1. Remove Springfox dependencies
2. Add SpringDoc OpenAPI dependency
3. Update ApplicationConfig to OpenApiConfig
4. Update controller annotations

### Phase 5: Test Migration

1. Update test dependencies for JUnit 5
2. Migrate test classes to JUnit 5 syntax

### Phase 6: Verification and Testing

1. Run all unit tests
2. Verify application startup
3. Test all API endpoints
4. Verify Swagger UI accessibility
5. Test H2 console access

## CI/CD Considerations

The project currently does not have GitHub Actions workflows. When adding CI/CD:

1. Use Java 17 in the workflow configuration
2. Example GitHub Actions configuration:

```yaml
name: Java CI with Maven

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build with Maven
      run: mvn clean verify
```

## Conclusion

The migration from Java 11 to Java 17 with Spring Boot 3.2.x is a significant undertaking that requires careful planning and execution. The main areas of concern are:

1. The javax.* to jakarta.* namespace migration affecting all 7 entity classes
2. The Spring Security configuration rewrite
3. The Springfox to SpringDoc migration

With proper phased execution and thorough testing, the migration can be completed successfully. The recommended approach is to follow the phased migration plan outlined above, with comprehensive testing at each phase before proceeding to the next.

## Appendix: Complete File Inventory

### Files Requiring Code Changes

| File | Changes Required |
|------|------------------|
| pom.xml | Java version, Spring Boot version, dependencies |
| Account.java | javax.* to jakarta.* imports |
| Address.java | javax.* to jakarta.* imports |
| BankInfo.java | javax.* to jakarta.* imports |
| Contact.java | javax.* to jakarta.* imports |
| Customer.java | javax.* to jakarta.* imports |
| CustomerAccountXRef.java | javax.* to jakarta.* imports |
| Transaction.java | javax.* to jakarta.* imports |
| SecurityConfig.java | WebSecurityConfigurerAdapter to SecurityFilterChain |
| ApplicationConfig.java | Springfox to SpringDoc configuration |
| AccountController.java | Swagger annotations migration |
| CustomerController.java | Swagger annotations migration |
| BankingApplicationTests.java | JUnit 4 to JUnit 5 migration |

### Files Not Requiring Changes

| File | Reason |
|------|--------|
| BankingApplication.java | No deprecated APIs used |
| BankingService.java | Interface, no deprecated APIs |
| BankingServiceImpl.java | No deprecated APIs used |
| BankingServiceHelper.java | No deprecated APIs used |
| Domain classes (DTOs) | No javax.* imports |
| Repository interfaces | No deprecated APIs used |
| application.yml | Configuration compatible |
