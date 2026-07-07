# Spring Boot 3.x Migration Summary

Migration of **COG-GTM/Aplicacion-de-Banca-Spring-Boot** (BankApp) from **Spring Boot 2.7.18 / Java 11** to **Spring Boot 3.5.5 / Java 21**, executed with the phased Spring Boot 3.x Migration Playbook.

## Version upgrades

| Component | Before | After |
|-----------|--------|-------|
| Spring Boot | 2.7.18 | **3.5.5** |
| Java (source/target/runtime) | 11 | **21** |
| Spring Framework | 5.3.31 | 6.2.10 |
| Spring Security | 5.7.11 | 6.5.3 |
| Hibernate ORM | 5.6.15 | 6.6.26 |
| Tomcat (embedded) | 9.0.83 (Servlet 4) | 10.1.44 (Servlet 6) |
| springdoc-openapi | `springdoc-openapi-ui` 1.6.15 | `springdoc-openapi-starter-webmvc-ui` 2.8.9 |
| jaxb-runtime | 2.3.8 (pinned) | 4.0.5 (BOM-managed) |
| Jakarta EE namespace | `javax.*` | `jakarta.*` |

## Code changes

1. **`pom.xml`** — parent 2.7.18→3.5.5; `java.version`/`maven.compiler.release`/compiler `<release>` 11→21; enforcer `requireJavaVersion` `[11,)`→`[21,)`; springdoc replaced with the Spring Boot 3 starter (2.8.9); `jaxb-runtime` un-pinned to BOM (4.x).
2. **`config/SecurityConfig.java`** — replaced the removed `WebSecurityConfigurerAdapter` with a `SecurityFilterChain` `@Bean`; `authorizeRequests`/`antMatchers` → `authorizeHttpRequests`/`requestMatchers`; lambda DSL for `csrf`/`headers`. An explicit `.anyRequest().permitAll()` preserves the original permissive behavior (Spring Security 6 denies unmatched requests by default; Spring Security 5 permitted them).
3. **7 JPA entities** (`model/*.java`) — `javax.persistence.*` → `jakarta.persistence.*` (47 import references).

No application property changes were required (Spring Boot Properties Migrator reported zero renames).

## Migration process (per phase)

| Phase | Outcome |
|-------|---------|
| 0 Pre-Flight | Maven single-module; no custom settings; dependency snapshot captured; baseline SB 2.7.18/Java 11 |
| 1 Test Stability | 1 test, 100% pass over 2 randomized runs; no transient failures |
| 2 Intermediate 2.7.x | BYPASSED — already on 2.7.18 |
| 3 Core Upgrade | SB 3.5.5 + JDK 21; compile fails as expected (javax + WebSecurityConfigurerAdapter) |
| 4 Dependency Compat | springdoc 2.8.9; jaxb 4.x; no QueryDSL/MapStruct/internal libs |
| 5 Framework | Spring Security migrated to `SecurityFilterChain`; actuator/batch/spring.factories/observability N/A |
| 6 Namespace | OpenRewrite attempted (failed on non-compiling source) → manual `javax.persistence`→`jakarta.persistence`; compile SUCCESS |
| 7 Configuration | Properties Migrator: 0 warnings; all config valid; logging/Hikari/SQL-init/banner/Flyway N/A |
| 8 API Adaptation | No changes required — JPA/Spring Data/Web/Security already SB3-compatible; tests already JUnit 5 |
| 9 Build & Test | `mvn clean verify` BUILD SUCCESS on iteration 1; endpoints verified |

## Final validation

- `./mvnw clean verify` (JDK 21) → **BUILD SUCCESS**.
- Tests: **1 run, 0 failures, 0 skipped** — matches the Phase 0/1 baseline count (no test removed or disabled).
- Compiled bytecode class file **major version 65 (Java 21)**.
- Application starts on port 8989, context path `/bank-api`, in ~3.6s under Hibernate 6.6.26.
- `GET /bank-api/actuator/health` → `{"status":"UP"}`.
- `GET /bank-api/v3/api-docs` → 200 (OpenAPI **3.1.0**); Swagger UI → 200.
- API contract preserved — all 9 endpoints present:
  `POST /customers/add`, `GET /customers/all`, `GET|PUT|DELETE /customers/{customerNumber}`,
  `POST /accounts/add/{customerNumber}`, `GET /accounts/{accountNumber}`,
  `GET /accounts/transactions/{accountNumber}`, `PUT /accounts/transfer/{customerNumber}`.

## Notes / conditional items not applicable

- **Code coverage comparison:** no coverage tooling (JaCoCo) is configured in the project, so no before/after coverage delta is produced. Test count parity (1→1) is verified instead.
- **JVM module `--add-opens` flags:** not required (no reflection-heavy libraries beyond Spring/Hibernate internals).
- Migration-only dependency `spring-boot-properties-migrator` (added in Phase 7) was removed in Phase 9. OpenRewrite was run as a transient CLI goal and was never added to `pom.xml`.

Full per-phase artifacts and verification logs: `docs/spring-boot-migration/artifacts/` and `docs/spring-boot-migration/verification-logs/`.
