# Phase 3: Core Version Upgrade — Compilation Status

## Changes Applied (pom.xml)

| Setting | Before | After |
|---------|--------|-------|
| `spring-boot-starter-parent` version | 2.7.18 | **3.5.5** |
| `java.version` | 11 | **21** |
| `maven.compiler.release` | 11 | **21** |
| maven-compiler-plugin `<release>` | 11 | **21** |
| maven-enforcer `requireJavaVersion` | `[11,)` | `[21,)` |

Build JDK switched to **JDK 21** (`/usr/lib/jvm/java-21-openjdk-amd64`). Maven wrapper 3.8.8 resolves and runs the Spring Boot 3.5.5 BOM under JDK 21 successfully (dependency resolution + compiler invocation both work; failures below are source-level only).

### BOM-managed explicit versions (conditional sub-task)

Reviewed explicit dependency versions against the 3.5.5 BOM:
- `springdoc-openapi-ui:1.6.15` — NOT managed by the Spring Boot BOM and is a Spring Boot 2–only artifact. It must be **replaced** (not just un-pinned) with `springdoc-openapi-starter-webmvc-ui` in Phase 4. Left unchanged in Phase 3.
- `jaxb-runtime:2.3.8` — pinned to a `javax`-era (JAXB 2.x) version. Handled in Phase 4 (raise to Jakarta-era 4.x, BOM-managed). Left unchanged in Phase 3.

### JVM module system (conditional sub-task)

No reflection-heavy libraries requiring `--add-opens`/`--add-exports` are present (no explicit CGLIB, no legacy bytecode manipulation libs beyond what Spring/Hibernate manage internally). **NOT APPLICABLE.** No `<argLine>`/`jvm.config` module flags added.

## Compilation Result

Command: `./mvnw clean compile` (JDK 21). Full log: `phase-3-compile.log`.

**Status: FAILED — EXPECTED at this phase.** Compilation errors are the anticipated consequence of the Jakarta EE namespace change (Spring Boot 3 replaces `javax.*` with `jakarta.*`) and Spring Security 6 API removals. These are resolved in Phases 5 and 6.

### Error categorization

| Category | Symbols | Files | Resolved in |
|----------|---------|-------|-------------|
| Jakarta persistence namespace | `Entity`, `Id`, `Column`, `GeneratedValue`, `GenerationType`, `OneToOne`, `ManyToOne`, `CascadeType`, `Temporal`, `TemporalType` | 7 model classes (`Account`, `Address`, `BankInfo`, `Contact`, `Customer`, `CustomerAccountXRef`, `Transaction`) | Phase 6 (javax→jakarta) |
| Spring Security 6 removal | `WebSecurityConfigurerAdapter` | `config/SecurityConfig.java` | Phase 5 (framework upgrade) |

`javax.*` import inventory (from `grep -rn "import javax\." src`): **47 imports, all `javax.persistence.*`** across the 7 model classes. No `javax.servlet`, `javax.validation`, `javax.annotation`, or other javax families are used in source → namespace migration scope is confined to JPA.

Proceed to Phase 4 (Dependency Compatibility).
