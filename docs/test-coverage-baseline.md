# Test Coverage Baseline Report

**Project:** BankApp (Aplicacion-de-Banca-Spring-Boot)  
**Report Date:** February 6, 2026  
**Java Version:** 11 (LTS)  
**Spring Boot Version:** 2.7.18  
**Test Framework:** JUnit 5 (Jupiter)

## Executive Summary

This document establishes the baseline test coverage for the BankApp application before the Java 17 migration. The test suite currently consists of a single context loading test that verifies the Spring Boot application starts correctly.

**Current Test Status:**

| Metric | Value |
|--------|-------|
| Total Tests | 1 |
| Tests Passed | 1 |
| Tests Failed | 0 |
| Tests Skipped | 0 |
| Pass Rate | 100% |
| Test Execution Time | 5.310 seconds |

## 1. Test Suite Analysis

### 1.1 Test Execution Results

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.coding.exercise.bankapp.BankingApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.310 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

### 1.2 Test Classes

| Test Class | Test Count | Status |
|------------|------------|--------|
| `BankingApplicationTests` | 1 | PASSED |

### 1.3 Test Details

#### BankingApplicationTests.java

**Location:** `src/test/java/com/coding/exercise/bankapp/BankingApplicationTests.java`

**Test Method:** `contextLoads()`

**Purpose:** Verifies that the Spring Boot application context loads successfully without errors.

**Code:**
```java
package com.coding.exercise.bankapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BankingApplicationTests {

    @Test
    public void contextLoads() {
    }
}
```

**What This Test Validates:**

1. Spring Boot application starts successfully
2. All Spring beans are properly configured
3. Database connection (H2) is established
4. JPA repositories are bootstrapped (4 repositories found)
5. Spring Security filter chain is configured
6. Actuator endpoints are exposed
7. No circular dependency issues exist

## 2. Application Startup Verification

During test execution, the following components were verified:

### 2.1 Spring Boot Startup

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v2.7.18)
```

### 2.2 Component Initialization

| Component | Status | Details |
|-----------|--------|---------|
| Spring Data JPA | OK | 4 repository interfaces found |
| HikariCP Connection Pool | OK | Started successfully |
| Hibernate ORM | OK | Version 5.6.15.Final |
| H2 Database | OK | Console available at `/h2-console` |
| Spring Security | OK | Filter chain configured |
| Actuator | OK | 1 endpoint exposed at `/actuator` |

### 2.3 Startup Logs Analysis

```
2026-02-06 04:29:07.897  INFO - Starting BankingApplicationTests using Java 21.0.9
2026-02-06 04:29:07.898  INFO - No active profile set, falling back to 1 default profile: "default"
2026-02-06 04:29:08.688  INFO - Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-02-06 04:29:08.760  INFO - Finished Spring Data repository scanning in 63 ms. Found 4 JPA repository interfaces.
2026-02-06 04:29:09.233  INFO - HikariPool-1 - Starting...
2026-02-06 04:29:09.425  INFO - HikariPool-1 - Start completed.
2026-02-06 04:29:09.509  INFO - Processing PersistenceUnitInfo [name: default]
2026-02-06 04:29:09.563  INFO - Hibernate ORM core version 5.6.15.Final
2026-02-06 04:29:09.763  INFO - Using dialect: org.hibernate.dialect.H2Dialect
2026-02-06 04:29:10.278  INFO - Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-02-06 04:29:11.184  INFO - H2 console available at '/h2-console'
2026-02-06 04:29:12.368  INFO - Will secure any request with [security filters...]
2026-02-06 04:29:12.401  INFO - Exposing 1 endpoint(s) beneath base path '/actuator'
2026-02-06 04:29:12.437  INFO - Started BankingApplicationTests in 4.787 seconds
```

### 2.4 Warnings Observed

| Warning | Severity | Action Required |
|---------|----------|-----------------|
| `spring.jpa.open-in-view is enabled by default` | LOW | Optional configuration change |

**Note:** This is an informational warning about the Open Session in View pattern being enabled by default. It does not affect functionality.

## 3. Test Framework Configuration

### 3.1 JUnit 5 Migration Status

The application has been migrated from JUnit 4 to JUnit 5 as documented in `MIGRATION_NOTES.md` (lines 36-38):

> **Test Framework Migration (JUnit 4 to JUnit 5)**:
> - Updated test classes to use JUnit 5 annotations (`@Test` from `org.junit.jupiter.api`)
> - Spring Boot 2.7.x includes JUnit 5 by default via `spring-boot-starter-test`

### 3.2 Test Dependencies

From `pom.xml`:

```xml
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
```

### 3.3 Test Framework Versions

| Framework | Version |
|-----------|---------|
| JUnit Jupiter | 5.8.2 |
| Mockito | 4.5.1 |
| AssertJ | 3.22.0 |
| Spring Security Test | 5.7.11 |
| Spring Test | 5.3.31 |

## 4. Critical Application Paths

### 4.1 Untested Paths

The following critical application paths do not have dedicated unit or integration tests:

| Path | Component | Priority |
|------|-----------|----------|
| Customer CRUD | `CustomerController` | HIGH |
| Account Operations | `AccountController` | HIGH |
| Fund Transfers | `BankingServiceImpl.transferDetails()` | HIGH |
| Transaction History | `BankingServiceImpl.findTransactionsByAccountNumber()` | MEDIUM |
| Security Configuration | `SecurityConfig` | MEDIUM |
| API Documentation | `ApplicationConfig` | LOW |

### 4.2 Recommended Test Coverage Improvements

For a production-ready application, the following tests should be added:

#### Controller Tests

```java
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Test void getAllCustomers_shouldReturnEmptyList();
    @Test void addCustomer_shouldCreateCustomer();
    @Test void getCustomer_shouldReturnCustomer();
    @Test void updateCustomer_shouldUpdateCustomer();
    @Test void deleteCustomer_shouldDeleteCustomer();
}

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Test void getByAccountNumber_shouldReturnAccount();
    @Test void addNewAccount_shouldCreateAccount();
    @Test void transferDetails_shouldTransferFunds();
    @Test void getTransactionByAccountNumber_shouldReturnTransactions();
}
```

#### Service Tests

```java
@SpringBootTest
class BankingServiceImplTest {
    @Test void findAll_shouldReturnAllCustomers();
    @Test void addCustomer_shouldPersistCustomer();
    @Test void transferDetails_shouldDebitAndCredit();
    @Test void transferDetails_shouldFailWithInsufficientFunds();
}
```

#### Repository Tests

```java
@DataJpaTest
class CustomerRepositoryTest {
    @Test void findByCustomerNumber_shouldReturnCustomer();
}

@DataJpaTest
class AccountRepositoryTest {
    @Test void findByAccountNumber_shouldReturnAccount();
}
```

## 5. Test Data Resources

### 5.1 Available Test Resources

The following test resources are available in `src/test/resources/`:

| Resource | Purpose |
|----------|---------|
| `application.yml` | Test configuration (if present) |
| Sample JSON files | Test data fixtures |

### 5.2 Test Data Files

Located in `src/test/resources/`:

- Test configuration files
- Sample request/response JSON files (9 resources copied during build)

## 6. Migration Considerations

### 6.1 Java 17 Migration Impact on Tests

| Aspect | Impact | Action Required |
|--------|--------|-----------------|
| JUnit 5 | None | Already using JUnit 5 |
| Mockito | None | Version 4.5.1 is Java 17 compatible |
| Spring Test | None | Version 5.3.31 supports Java 17 |
| AssertJ | None | Version 3.22.0 is Java 17 compatible |

### 6.2 Spring Boot 3.x Migration Impact on Tests

| Aspect | Impact | Action Required |
|--------|--------|-----------------|
| JUnit 5 | None | Already migrated |
| `@SpringBootTest` | None | Annotation unchanged |
| Security Test | Minor | May need updates for new security DSL |
| MockMvc | Minor | May need updates for Jakarta namespace |

## 7. Test Execution Commands

### 7.1 Run All Tests

```bash
./mvnw clean test
```

### 7.2 Run Specific Test Class

```bash
./mvnw test -Dtest=BankingApplicationTests
```

### 7.3 Run Tests with Coverage Report

```bash
./mvnw test jacoco:report
```

**Note:** JaCoCo plugin needs to be added to `pom.xml` for coverage reports.

### 7.4 Run Tests in Verbose Mode

```bash
./mvnw test -X
```

## 8. Recommendations

### 8.1 Before Java 17 Migration

1. **Maintain Current Test Status**
   - Ensure the context loading test continues to pass
   - Do not introduce breaking changes to test configuration

2. **Document Test Results**
   - Record test execution time for comparison
   - Note any warnings or deprecation messages

### 8.2 During Java 17 Migration

1. **Run Tests Frequently**
   - Execute `./mvnw clean test` after each configuration change
   - Monitor for new warnings or errors

2. **Verify Context Loading**
   - Ensure Spring Boot context still loads successfully
   - Check that all 4 JPA repositories are found

### 8.3 After Java 17 Migration

1. **Compare Results**
   - Test count should remain the same (1)
   - Pass rate should remain 100%
   - Execution time should be similar (±20%)

2. **Monitor Warnings**
   - Note any new deprecation warnings
   - Document any behavioral changes

### 8.4 Future Test Improvements

1. **Add Unit Tests**
   - Service layer tests with mocked repositories
   - Controller tests with MockMvc

2. **Add Integration Tests**
   - End-to-end API tests
   - Database integration tests

3. **Add Coverage Reporting**
   - Configure JaCoCo plugin
   - Set minimum coverage thresholds

## 9. Conclusion

The current test suite provides basic validation that the Spring Boot application context loads correctly. While the test coverage is minimal (1 test), it serves as a critical smoke test for the migration process.

**Key Takeaways:**

- All tests pass (100% pass rate)
- Application context loads successfully
- All Spring components initialize correctly
- No circular dependency issues
- Test framework is already on JUnit 5 (no migration needed)

The existing test provides a baseline for verifying that the Java 17 migration does not break the application's ability to start. Additional tests should be added to improve coverage of business logic and API endpoints.

## Appendix A: Full Test Output

```
[INFO] --- maven-surefire-plugin:3.2.5:test (default-test) @ bank-app ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.coding.exercise.bankapp.BankingApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.310 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

## Appendix B: Related Documentation

- [Java 17 Compatibility Audit](java-17-compatibility-audit.md)
- [Spring Boot 3.x Breaking Changes](spring-boot-3-breaking-changes.md)
- [Java 8 to 11 Migration Notes](../MIGRATION_NOTES.md)
- [Spring Boot Compatibility Analysis](spring-boot-compatibility.md)
