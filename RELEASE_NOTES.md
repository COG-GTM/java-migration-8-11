# Release Notes - Java 17 Migration (v1.0.0)

## Overview

This release migrates the Banking Application from **Java 11 / Spring Boot 2.7.x** to **Java 17 / Spring Boot 3.2.5**.

## What Changed

### Java Version Upgrade

- **Java 11 (LTS) -> Java 17 (LTS)**
- Maven compiler source, target, and release updated to 17
- Maven Enforcer Plugin now requires Java 17+

### Spring Boot Upgrade

- **Spring Boot 2.7.18 -> 3.2.5**
- Includes Spring Framework 6.x, Spring Security 6.x, Hibernate 6.x

### Jakarta EE Migration (javax.* -> jakarta.*)

All JPA entity classes migrated from `javax.persistence.*` to `jakarta.persistence.*`:

- `Account.java`
- `Address.java`
- `BankInfo.java`
- `Contact.java`
- `Customer.java`
- `CustomerAccountXRef.java`
- `Transaction.java`

### Spring Security 6.x Migration

- Removed deprecated `WebSecurityConfigurerAdapter`
- Migrated to component-based `SecurityFilterChain` bean configuration
- Updated to use `authorizeHttpRequests()` and `requestMatchers()` APIs

### API Documentation Migration

- **springdoc-openapi-ui 1.6.15 -> springdoc-openapi-starter-webmvc-ui 2.3.0**
- Swagger UI now available at: `/bank-api/swagger-ui/index.html`
- OpenAPI JSON available at: `/bank-api/v3/api-docs`

### Dependency Updates

| Dependency | Old Version | New Version |
|---|---|---|
| Spring Boot Parent | 2.7.18 | 3.2.5 |
| SpringDoc OpenAPI | 1.6.15 (springdoc-openapi-ui) | 2.3.0 (springdoc-openapi-starter-webmvc-ui) |
| JAXB Runtime | 2.3.8 | 4.0.4 |
| Maven Compiler Plugin | 3.11.0 | 3.12.1 |

### CI/CD Updates

- GitHub Actions CI workflow updated to use **JDK 17** (Temurin distribution)

### Documentation Updates

- README.md updated with Java 17 requirements and technology stack
- Added build/run instructions and environment verification steps
- Updated API documentation URLs

## Breaking Changes

- **Java 17 is now required** - Java 11 or earlier will not work
- **Swagger UI URL changed**: `/swagger-ui.html` -> `/swagger-ui/index.html`
- **Jakarta namespace**: Any extensions using `javax.persistence.*` must migrate to `jakarta.persistence.*`

## Verification Endpoints

After starting the application, verify the following:

| Endpoint | URL |
|---|---|
| Actuator Health | http://localhost:8989/bank-api/actuator/health |
| Swagger UI | http://localhost:8989/bank-api/swagger-ui/index.html |
| H2 Console | http://localhost:8989/bank-api/h2-console/ |

## Authentication

Default credentials remain unchanged:

- **Username**: `bankapp`
- **Password**: `changeit`
