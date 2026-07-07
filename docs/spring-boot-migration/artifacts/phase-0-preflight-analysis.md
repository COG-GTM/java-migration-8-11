# Phase 0: Pre-Flight Analysis

Migration target: **Spring Boot 3.5.5**, **JDK 21**. Source: Spring Boot 2.7.18 / Java 11.

## REQ-P0-1.1: Build Tool Configuration

1. **Build Tool:** Apache Maven. System `mvn` = 3.6.3; project Maven Wrapper (`./mvnw`) = **3.8.8** (from `.mvn/wrapper/maven-wrapper.properties`). Note: JDK 21 builds require Maven 3.9+ — the wrapper 3.8.8 works for JDK 21 but a newer Maven will be used/verified in Phase 3.
2. **Root configuration file:** `pom.xml` (project root).
3. **Multi-module status:** NO — single-module project (`grep -c "<module>" pom.xml` = 0).
4. **Spring Boot parent inheritance:** inherits from `org.springframework.boot:spring-boot-starter-parent:2.7.18` (Pattern A). Child modules: none.

## REQ-P0-1.2: Custom Build Settings

- Project root `*settings*.xml`: none found (`find . -maxdepth 1 -name "*settings*.xml"` empty).
- User `~/.m2/settings.xml`: not present.
- `MAVEN_SETTINGS` env var: empty.
- `.mvn/` contents: only `wrapper/maven-wrapper.properties` (no `jvm.config`, no `maven.config`, no custom settings).

**Conclusion:** No custom Maven settings file exists. Build commands run WITHOUT a `-s <settings-file>` flag. (Wherever the playbook shows `mvn -s maven-settings.xml ...`, the `-s` flag is omitted for this project because no such file exists.)

## REQ-P0-2.1: Dependency Snapshot Summary

Raw output files (kept for Phase 9 diff comparison):
- `phase-0-effective-pom-before.xml`
- `phase-0-dependency-tree-before.txt`
- `phase-0-dependency-list-before.txt`

Total resolved dependency entries: **111** (`grep -c ":" phase-0-dependency-list-before.txt`).

| Module | Key Dependency | Version | Scope | Managed by BOM? |
|--------|----------------|---------|-------|-----------------|
| bank-app | spring-boot-starter-actuator | 2.7.18 | compile | Yes |
| bank-app | spring-boot-starter-data-jpa | 2.7.18 | compile | Yes |
| bank-app | spring-boot-starter-security | 2.7.18 | compile | Yes |
| bank-app | spring-boot-starter-web | 2.7.18 | compile | Yes |
| bank-app | spring-boot-devtools | 2.7.18 | runtime | Yes |
| bank-app | com.h2database:h2 | 2.1.214 | runtime | Yes |
| bank-app | org.projectlombok:lombok | 1.18.30 | compile (optional) | Yes |
| bank-app | org.springdoc:springdoc-openapi-ui | **1.6.15** | compile | No (explicit) |
| bank-app | org.glassfish.jaxb:jaxb-runtime | **2.3.8** | compile | No (explicit) |
| bank-app | spring-boot-starter-test | 2.7.18 | test | Yes |
| bank-app | spring-security-test | (BOM) | test | Yes |
| bank-app | org.junit.jupiter:junit-jupiter | 5.8.2 | test | Yes |
| bank-app | org.mockito:mockito-junit-jupiter | 4.5.1 | test | Yes |

**Migration-relevant callouts:**
- `springdoc-openapi-ui:1.6.15` (SB2-only) → must move to `springdoc-openapi-starter-webmvc-ui` 2.x for Spring Boot 3.x (Phase 4).
- `jaxb-runtime:2.3.8` (jakarta.xml.bind 2.x / `javax` era) → must move to 4.x for Jakarta EE 9+ (Phase 4).
- Test framework is already **JUnit 5 (Jupiter)** — no JUnit 4 → 5 migration needed.
- Transitive `jakarta.*` APIs are currently at EE8 levels (persistence 2.2, validation 2.0, xml.bind 2.3) and will be raised to EE10 by the Spring Boot 3.x BOM.

## REQ-P0-3.1: Version Baseline

| Module | Spring Boot Version | Parent POM | Inherits From |
|--------|---------------------|------------|---------------|
| bank-app (root) | 2.7.18 | spring-boot-starter-parent | - |

- **Current JDK version (target in pom):** Java 11 (`java.version=11`, `maven.compiler.release=11`).
- **Build JDK available in environment:** 17 (default), 21 available at `/usr/lib/jvm/java-21-openjdk-amd64`.
- **Target JDK version:** 21.

**Major dependency table:**

| Name | Current Version | Managed by BOM? |
|------|-----------------|-----------------|
| spring-boot-starter-* | 2.7.18 | Yes |
| springdoc-openapi-ui | 1.6.15 | No |
| jaxb-runtime | 2.3.8 | No |
| h2 | 2.1.214 | Yes |
| lombok | 1.18.30 | Yes |
| junit-jupiter | 5.8.2 | Yes |

**Upgrade path assessment:** All Spring Boot artifacts are uniformly on 2.7.18 (no outliers). Current version 2.7.18 ≥ 2.7.0, so the Phase 2 intermediate 2.7.x upgrade will be BYPASSED (decision artifact still produced in Phase 2). Ready to proceed directly to Spring Boot 3.x core upgrade after test-baseline stabilization.
