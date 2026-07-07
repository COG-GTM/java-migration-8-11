# Phase 4: Dependency Compatibility

## REQ: JDK 21 / Jakarta-incompatible dependency scan

Detection: reviewed `phase-0-dependency-list-before.txt` and the current resolved tree for known JDK 21– or Jakarta-incompatible artifacts (log4j 1.x, junit 4.x, old bytecode libs, javax-era APIs pinned explicitly).

| Concern | Present? | Action |
|---------|----------|--------|
| log4j 1.x | No | none |
| JUnit 4 (`junit:junit`) | No — project uses JUnit 5 Jupiter 5.8.2 (BOM will raise to 5.12.x under SB 3.5.5) | none (BOM-managed) |
| Old Mockito / byte-buddy / asm | Only BOM-managed transitive; SB 3.5.5 BOM provides JDK 21–compatible versions | none (BOM-managed) |
| `springdoc-openapi-ui:1.6.15` (Spring Boot 2 only) | **Yes** | **Replaced** (see below) |
| `jaxb-runtime:2.3.8` (javax/JAXB 2.x pinned) | **Yes** | **Un-pinned** → BOM-managed 4.x (Jakarta) |

## Explicit dependency changes

### springdoc (OpenAPI/Swagger)

- **Before:** `org.springdoc:springdoc-openapi-ui:1.6.15`
- **After:** `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9`
- **Rationale:** springdoc 1.x targets Spring Boot 2 (`javax`, WebMvc integration via old auto-config) and is incompatible with Spring Boot 3. springdoc 2.x is the Jakarta-based line for Spring Boot 3.x; the `-starter-webmvc-ui` artifact is the direct replacement for `springdoc-openapi-ui`. 2.8.9 is a stable release compatible with Spring Boot 3.4/3.5 and published well over 7 days ago.
- **Source compatibility:** the codebase imports `io.swagger.v3.oas.annotations.*` and `io.swagger.v3.oas.models.OpenAPI` (in `AccountController`, `CustomerController`, `ApplicationConfig`). springdoc 2.8.9 pulls `io.swagger.core.v3:swagger-*-jakarta:2.2.30`, which provides these exact packages → no source import changes required for Swagger.

Resolved transitively:
```
org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9
org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.9
org.springdoc:springdoc-openapi-starter-common:2.8.9
io.swagger.core.v3:swagger-core-jakarta:2.2.30
io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
io.swagger.core.v3:swagger-models-jakarta:2.2.30
org.webjars:swagger-ui:5.21.0
```

### jaxb-runtime

- **Before:** `org.glassfish.jaxb:jaxb-runtime:2.3.8` (explicit, javax/JAXB 2.x)
- **After:** `org.glassfish.jaxb:jaxb-runtime` (no explicit version → BOM-managed **4.0.5**, with `jakarta.xml.bind-api:4.0.2`)
- **Rationale:** The pinned 2.3.8 is a `javax.xml.bind` era runtime incompatible with the Jakarta stack. Spring Boot 3.5.5 BOM manages `jaxb-runtime` at 4.x (Jakarta). Removing the explicit version lets the BOM govern it (playbook: trust the BOM; never override without rationale). No source uses JAXB directly (`grep "javax.xml|jaxb|@Xml" src` → none), so this is purely a runtime/transitive concern.

## Conditional sub-tasks (detection proof)

| Sub-task | Applicable? | Evidence |
|----------|-------------|----------|
| Upgrade internal/company Spring Boot libraries | No | No internal/company-namespaced Spring Boot libs in dependency list |
| Bytecode check of other internal deps | No | No internal/company artifacts present (only public OSS deps) |
| QueryDSL migration | No | `grep -r "querydsl" pom.xml src` → none |
| MapStruct migration | No | `grep -r "mapstruct" pom.xml src` → none |

## Result

`./mvnw dependency:list` resolves successfully under JDK 21 with the Spring Boot 3.5.5 BOM. Remaining compilation errors are namespace/framework only (Phases 5 & 6), not dependency-resolution errors. Proceed to Phase 5.
