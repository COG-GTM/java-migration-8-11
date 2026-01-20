# Dependency Compatibility Analysis for Java 17 Migration

This document provides a comprehensive analysis of dependency compatibility for migrating BankApp from Java 11 to Java 17.

## Executive Summary

The migration from Java 11 to Java 17 requires significant dependency updates, primarily due to Spring Boot 3.x requirements. Spring Boot 3.x is the minimum version that fully supports Java 17 and requires the migration from `javax.*` to `jakarta.*` namespaces.

## Current Dependency Stack

| Dependency | Current Version | Notes |
|------------|-----------------|-------|
| Spring Boot | 2.1.4.RELEASE | Very outdated, released April 2019 |
| Java | 11 | Current target |
| Springfox Swagger | 2.9.2 | Deprecated, no longer maintained |
| H2 Database | Managed by Spring Boot | ~1.4.199 |
| Lombok | Managed by Spring Boot | ~1.18.6 |
| Jakarta XML Bind API | 2.3.3 | Already added for Java 11 |
| JAXB Runtime | 2.3.8 | Already added for Java 11 |

## Target Dependency Stack for Java 17

| Dependency | Target Version | Rationale |
|------------|----------------|-----------|
| Spring Boot | 3.2.x | Latest stable LTS-compatible version, requires Java 17+ |
| Java | 17 | LTS version with long-term support until 2029 |
| SpringDoc OpenAPI | 2.3.x | Replacement for Springfox, compatible with Spring Boot 3 |
| H2 Database | 2.2.x | Managed by Spring Boot 3.2.x |
| Lombok | 1.18.30+ | Latest version with Java 17+ support |
| Jakarta Persistence API | 3.1.x | Replaces javax.persistence |

## Detailed Dependency Analysis

### 1. Spring Boot 2.1.4 to 3.2.x

**Compatibility Status**: REQUIRES MIGRATION

**Key Changes**:

Spring Boot 3.x introduces several breaking changes that must be addressed:

1. **Java Version Requirement**: Spring Boot 3.x requires Java 17 as the minimum version. This is a hard requirement and cannot be bypassed.

2. **Jakarta EE 9+ Migration**: Spring Boot 3.x is built on Jakarta EE 9+, which means all `javax.*` packages have been renamed to `jakarta.*`. This affects:
   - `javax.persistence.*` → `jakarta.persistence.*`
   - `javax.servlet.*` → `jakarta.servlet.*`
   - `javax.validation.*` → `jakarta.validation.*`
   - `javax.annotation.*` → `jakarta.annotation.*`

3. **Spring Security Changes**: `WebSecurityConfigurerAdapter` has been deprecated and removed. Security configuration must use the new component-based approach with `SecurityFilterChain` beans.

4. **Hibernate 6.x**: Spring Boot 3.x uses Hibernate 6.x by default, which includes some behavioral changes in entity mapping and query handling.

**Migration Path**:

We recommend a two-step upgrade approach:
1. First upgrade to Spring Boot 2.7.18 (latest 2.x) to benefit from deprecation warnings
2. Then upgrade to Spring Boot 3.2.x

**pom.xml Changes Required**:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

### 2. Springfox Swagger 2.9.2 to SpringDoc OpenAPI 2.x

**Compatibility Status**: REQUIRES REPLACEMENT

**Rationale**:

Springfox is no longer maintained and is incompatible with Spring Boot 3.x. SpringDoc OpenAPI is the recommended replacement and is actively maintained.

**Key Changes**:

1. **Dependency Replacement**:
   - Remove: `springfox-swagger2`, `springfox-swagger-ui`
   - Add: `springdoc-openapi-starter-webmvc-ui`

2. **Annotation Changes**:
   - `@EnableSwagger2` → No longer needed (auto-configured)
   - `@Api` → `@Tag`
   - `@ApiOperation` → `@Operation`
   - `@ApiResponse` → `@ApiResponse` (different package)
   - `@ApiResponses` → `@ApiResponses` (different package)

3. **Configuration Changes**:
   - `Docket` bean → `OpenAPI` bean or application.properties configuration
   - Swagger UI path changes from `/swagger-ui.html` to `/swagger-ui/index.html`

**pom.xml Changes Required**:

```xml
<!-- Remove these -->
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

<!-- Add this -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 3. Jakarta EE Migration (javax.* to jakarta.*)

**Compatibility Status**: REQUIRES CODE CHANGES

**Impact Analysis**:

The migration from `javax.*` to `jakarta.*` affects 7 entity classes in the project. All JPA annotations must be updated.

**Affected Files**:
- `src/main/java/com/coding/exercise/bankapp/model/Account.java`
- `src/main/java/com/coding/exercise/bankapp/model/Address.java`
- `src/main/java/com/coding/exercise/bankapp/model/BankInfo.java`
- `src/main/java/com/coding/exercise/bankapp/model/Contact.java`
- `src/main/java/com/coding/exercise/bankapp/model/Customer.java`
- `src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java`
- `src/main/java/com/coding/exercise/bankapp/model/Transaction.java`

**Import Changes Required**:

| Old Import | New Import |
|------------|------------|
| `javax.persistence.Entity` | `jakarta.persistence.Entity` |
| `javax.persistence.Id` | `jakarta.persistence.Id` |
| `javax.persistence.GeneratedValue` | `jakarta.persistence.GeneratedValue` |
| `javax.persistence.GenerationType` | `jakarta.persistence.GenerationType` |
| `javax.persistence.Column` | `jakarta.persistence.Column` |
| `javax.persistence.OneToOne` | `jakarta.persistence.OneToOne` |
| `javax.persistence.ManyToOne` | `jakarta.persistence.ManyToOne` |
| `javax.persistence.CascadeType` | `jakarta.persistence.CascadeType` |
| `javax.persistence.Temporal` | `jakarta.persistence.Temporal` |
| `javax.persistence.TemporalType` | `jakarta.persistence.TemporalType` |

### 4. H2 Database Compatibility

**Compatibility Status**: COMPATIBLE WITH UPDATES

**Current State**:

H2 Database version is managed by Spring Boot. The current Spring Boot 2.1.4 uses H2 1.4.199.

**Target State**:

Spring Boot 3.2.x manages H2 2.2.x, which is fully compatible with Java 17.

**Breaking Changes in H2 2.x**:

1. **Default Column Type Changes**: Some default column types have changed
2. **SQL Syntax Changes**: Some SQL syntax has been tightened
3. **JDBC URL Changes**: Some JDBC URL parameters have changed

**Recommended Configuration**:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=LEGACY
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```

### 5. Lombok Compatibility

**Compatibility Status**: COMPATIBLE

**Current State**:

Lombok version is managed by Spring Boot. The current version (~1.18.6) is compatible with Java 11.

**Target State**:

Spring Boot 3.2.x manages Lombok 1.18.30+, which is fully compatible with Java 17 and later.

**No Code Changes Required**:

Lombok annotations used in the project (`@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter`, `@Setter`) are all compatible with Java 17.

**Recommendation**:

Let Spring Boot manage the Lombok version. No explicit version override is needed.

### 6. JAXB Dependencies

**Compatibility Status**: REQUIRES UPDATES

**Current State**:

The project already includes JAXB dependencies for Java 11 compatibility:
- `jakarta.xml.bind:jakarta.xml.bind-api:2.3.3`
- `org.glassfish.jaxb:jaxb-runtime:2.3.8`
- `javax.activation:activation:1.1.1`

**Target State**:

For Spring Boot 3.x and Java 17, update to Jakarta XML Bind 4.x:

```xml
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

**Note**: The `javax.activation:activation` dependency should be replaced with `jakarta.activation:jakarta.activation-api`.

## Dependency Compatibility Matrix

| Dependency | Java 11 | Java 17 | Spring Boot 2.x | Spring Boot 3.x | Action Required |
|------------|---------|---------|-----------------|-----------------|-----------------|
| Spring Boot 2.1.4 | Yes | No | N/A | N/A | Upgrade to 3.2.x |
| Springfox 2.9.2 | Yes | No | Yes | No | Replace with SpringDoc |
| H2 1.4.x | Yes | Yes | Yes | No | Upgrade to 2.2.x |
| Lombok 1.18.6 | Yes | Yes | Yes | Yes | Upgrade to 1.18.30+ |
| Jakarta XML Bind 2.3.x | Yes | Yes | Yes | No | Upgrade to 4.x |

## Risk Assessment

### High Risk

1. **Spring Security Configuration**: The removal of `WebSecurityConfigurerAdapter` requires a complete rewrite of the security configuration.

2. **Jakarta EE Namespace Migration**: All 7 entity classes require import changes. This is a mechanical but error-prone process.

### Medium Risk

1. **Swagger to SpringDoc Migration**: Annotation changes are required in both controllers. The API documentation structure may change slightly.

2. **H2 Database Behavior Changes**: H2 2.x has stricter SQL parsing. Some queries may need adjustment.

### Low Risk

1. **Lombok Compatibility**: Lombok is fully compatible with Java 17. No code changes required.

2. **JAXB Dependencies**: Already partially migrated. Only version updates needed.

## Recommended Migration Order

1. **Phase 1**: Update pom.xml with Java 17 and Spring Boot 3.2.x
2. **Phase 2**: Migrate javax.* to jakarta.* in all entity classes
3. **Phase 3**: Replace Springfox with SpringDoc OpenAPI
4. **Phase 4**: Update Spring Security configuration
5. **Phase 5**: Update JAXB dependencies
6. **Phase 6**: Test and verify all functionality

## References

- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [SpringDoc OpenAPI Migration Guide](https://springdoc.org/v2/)
- [Jakarta EE 9 Release Notes](https://jakarta.ee/release/9/)
- [H2 Database Migration Guide](https://www.h2database.com/html/migration-to-v2.html)
