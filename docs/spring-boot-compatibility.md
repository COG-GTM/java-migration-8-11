# Spring Boot Version Compatibility Evaluation for Java 11

**Ticket:** MBA-208 (Phase 2: Dependency Evaluation)
**Repository:** COG-GTM/java-migration-8-11
**Scope:** Evaluate whether the current Spring Boot version is compatible with
Java 11 and determine whether a Spring Boot upgrade should be recommended as
part of the Java 8 to Java 11 migration.

This document is an **evaluation and recommendation** only. No dependency
versions are changed here; any actual upgrade is a Phase 3 activity.

## 1. Current Configuration

Values below are read from the project `pom.xml`.

| Component | Current Value |
|-----------|---------------|
| Spring Boot (parent) | `2.1.4.RELEASE` |
| `java.version` property | `1.8` |
| Spring Framework (managed) | 5.1.x |
| API docs | Springfox Swagger (`springfox-swagger2` / `springfox-swagger-ui`) |
| Database | H2 (managed by Spring Boot) |

The migration target for the wider epic (MBA-205) is Java 11.

## 2. Is Spring Boot 2.1.4.RELEASE Compatible with Java 11?

**Yes.** Spring Boot 2.1 (released October 2018) was the first Spring Boot
line to add official Java 11 support, and it remained compatible with Java 8.
`2.1.4.RELEASE` (April 2019) inherits that support.

> "Spring Boot 2.1 remains compatible with Java 8 but now also supports
> Java 11." — Spring Boot 2.1 Release Notes

**Conclusion for the migration:** the current Spring Boot version does **not
block** the Java 11 migration. Moving the build to Java 11 does not require a
Spring Boot upgrade first.

Practical caveats to validate during the Java 11 build (these are Java-platform
concerns, not Spring Boot version incompatibilities):

- **JAXB / `javax.xml.bind`** was removed from the JDK in Java 11. Any code or
  transitive dependency relying on it must add explicit JAXB dependencies.
- Other Java EE modules removed in Java 11 (JAF, JTA, JAX-WS) follow the same
  pattern if used.

## 3. Recommended Spring Boot Versions for Java 11

| Java Version | Minimum Spring Boot | Recommended Spring Boot |
|--------------|---------------------|-------------------------|
| Java 8 | 2.0.x | 2.7.x |
| **Java 11** | **2.1.0** | **2.7.18** (latest 2.x) |
| Java 17 | 2.5.x (runtime) / 3.0.x | 3.x |
| Java 21 | 3.1.x | 3.2.x+ |

Notes:

- **Spring Boot 2.7.x** is the final 2.x feature line (released May 2022). It
  supports Java 8 through Java 19 and is the recommended target for a Java 11
  application. `2.7.18` (November 2023) is the last 2.7 patch release on open
  source.
- **Spring Boot 3.x requires Java 17 as a minimum** and will not run on
  Java 11. It is therefore **out of scope** for a Java 11 migration and should
  only be considered if/when the target moves to Java 17+.

### Support status

| Line | OSS support | Notes |
|------|-------------|-------|
| 2.1.x | Ended 2019 | No further OSS bug fixes or security patches |
| 2.7.x | OSS ended Nov 2023 | Commercial/extended support available via subscription |
| 3.x | Active | Requires Java 17+ |

Spring Boot 2.1.x is well past end of life and receives **no security
patches**, which is the primary argument for not staying on it.

## 4. Recommendation

**Keep Java 11 migration and Spring Boot upgrade as separate, sequenced steps:**

1. **Phase 2/3 (Java 11 build):** The migration to Java 11 can proceed on the
   current `2.1.4.RELEASE` because it is Java 11 compatible. This is the
   lowest-risk path to reach a building, Java 11 application.
2. **Phase 3 (recommended follow-up):** Upgrade Spring Boot to **2.7.18**.
   This is *recommended* but not strictly *required* for Java 11. The driver is
   security and maintenance, not raw Java 11 compatibility, since 2.1.x is EOL.

**Do not** upgrade to Spring Boot 3.x as part of this Java 11 effort — it
requires Java 17.

## 5. Impact, Risks, and Benefits of a 2.1.4 to 2.7.18 Upgrade

The following assesses the *recommended* Phase 3 upgrade so the risk is
understood ahead of time.

### Benefits

- Security patches and bug fixes (2.1.x receives none).
- Modern, maintained transitive dependencies (Spring Framework 5.3, Tomcat 9.0.x,
  Jackson 2.13, Hibernate 5.6).
- Better tooling and a smoother later path to Spring Boot 3 / Java 17.

### Risks and required attention

| Area | Risk | Notes / mitigation |
|------|------|--------------------|
| Springfox Swagger | **High** | Springfox is effectively unmaintained and breaks on Spring Boot 2.6+ default path matching. Migrate to `springdoc-openapi-ui`, or set `spring.mvc.pathmatch.matching-strategy=ant_path_matcher` as a temporary workaround. |
| H2 database | **High** | Spring Boot 2.7 upgrades H2 from 1.4.x to 2.x, which has breaking SQL/behavior changes. Review any `schema.sql`/`data.sql` and H2 console usage. |
| Bean validation starter | Medium | Since 2.3, `spring-boot-starter-validation` is no longer transitively included by the web starter. Add it explicitly if `@Valid`/`@NotNull` etc. are used. |
| Circular references | Medium | Prohibited by default since 2.6. Refactor, or temporarily set `spring.main.allow-circular-references=true`. |
| Test framework | Medium | JUnit 5 is default from 2.2; JUnit 4 tests need the Vintage engine. Migrate `@RunWith(SpringRunner.class)` to `@ExtendWith(SpringExtension.class)`. |
| Config file processing | Low | Rewritten in 2.4; legacy behavior available via `spring.config.use-legacy-processing=true`. |

### Effort estimate

Roughly **1-3 developer-days** for the 2.1.4 to 2.7.18 upgrade, dominated by the
Springfox to SpringDoc migration, H2 2.x validation, and test-framework fixes.

## 6. Acceptance Criteria Coverage

- [x] Document Spring Boot 2.1.4.RELEASE Java 11 compatibility status — Section 2.
- [x] Research and document recommended Spring Boot versions for Java 11 —
  Section 3.
- [x] Provide recommendation on whether to upgrade Spring Boot version —
  Section 4.
- [x] Document potential risks and benefits of a Spring Boot upgrade — Section 5.

## References

- [Spring Boot 2.1 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes)
- [Spring Boot 2.7 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes)
- [Spring Boot 3.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Release-Notes)
- [Spring Boot Support Policy](https://spring.io/projects/spring-boot#support)
- [SpringDoc: Migrating from Springfox](https://springdoc.org/#migrating-from-springfox)
- [H2 Migration to Version 2](https://www.h2database.com/html/migration-to-v2.html)
