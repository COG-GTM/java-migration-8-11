# Phase 8: API Adaptation — Applicability

Every detection command was run. Evidence recorded for both APPLICABLE and NOT APPLICABLE outcomes.

| Sub-task | Detection command | Result | Applicable? | Action |
|----------|-------------------|--------|-------------|--------|
| Spring Data: deprecated repo methods | `grep -rn "getOne\|deleteInBatch\|findAllInBatch\|getById" src` | none | No | none |
| Spring Data: repository API | reviewed all 4 repos — `CrudRepository` + derived queries returning `Optional`/`Optional<List>` | compatible with Spring Data 3.x | No | none |
| JPA: ID generation (Hibernate 6) | `@GeneratedValue`/`GenerationType.AUTO` on 7 entities | Hibernate 6.6.26 initialized + created schema at startup with no error (`phase-7-startup.log`) | No change needed | verified only |
| JPA: Criteria API | `grep -rn "CriteriaBuilder\|CriteriaQuery"` | none | No | none |
| JPA: collection initialization | no `Hibernate.initialize`/`PersistentBag` usage | none | No | none |
| Hibernate: custom `@Type`/`UserType` | `grep -rn "UserType\|@Type("` | none | No | none |
| Security: OAuth2 | `grep -rn "OAuth2" src` | none | No | none |
| Security: CORS | `grep -rn "addCorsMappings\|@CrossOrigin\|CorsConfiguration"` | none | No | none |
| Web: trailing slash / PathPattern | `grep -rn "setUseTrailingSlashMatch\|setUseSuffixPatternMatch\|AntPathMatcher"` | none | No | none |
| Web: RestTemplate/WebClient/HttpMethod | `grep -rn "RestTemplate\|WebClient\|HttpMethod.resolve"` | none | No | none |
| Web: CommonsMultipartResolver | `grep -rn "MultipartResolver"` | none | No | none |
| ProblemDetail / ResponseEntityExceptionHandler | `grep -rn "ProblemDetail\|ResponseEntityExceptionHandler"` | none | No | none |
| `@ConstructorBinding` relocation | `grep -rn "@ConstructorBinding"` | none | No | none |
| Elasticsearch | `grep -rni "elasticsearch"` | none | No | none |
| Redis | `grep -rni "redis"` | none | No | none |
| MongoDB | `grep -rni "mongo"` | none | No | none |
| WebSocket / STOMP | `grep -rni "websocket\|stomp\|@EnableWebSocket"` | none | No | none |
| Tests: JUnit 4 → 5 migration | `grep -rn "org.junit" src/test` → only `org.junit.jupiter.api.Test` | already JUnit 5 | No | none |
| Third-party libs needing adaptation | dependency set is Spring Boot BOM-managed + springdoc 2.x (handled Phase 4) | no other third-party libs | No | none (no third-party test log required) |

## JPA / Hibernate 6 note

The 7 entities use `@GeneratedValue(strategy = GenerationType.AUTO)` (Customer uses bare `@GeneratedValue`, equivalent to AUTO). Hibernate 6 maps `AUTO` to a sequence-style generator; H2 supports sequences and the application uses embedded-H2 schema auto-generation (create-drop), so the schema is generated to match the mapping at runtime. Startup under Spring Boot 3.5.5 (Hibernate ORM **6.6.26.Final**) created the persistence unit and schema with no mapping errors or exceptions. No entity/ID-generation code changes are required.

## Result

No source changes were required in Phase 8; the codebase's JPA, Spring Data, Web, and Security API usage is already compatible with the Spring Boot 3.5.5 / Hibernate 6 / Spring Security 6 API surface (the only breaking API — `WebSecurityConfigurerAdapter` — was handled in Phase 5). Proceed to Phase 9.
