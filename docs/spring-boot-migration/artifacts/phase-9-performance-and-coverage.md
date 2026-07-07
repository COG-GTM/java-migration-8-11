# Phase 9: Performance Baseline & Coverage

## Performance baseline (post-migration, Spring Boot 3.5.5 / JDK 21)

| Metric | Value |
|--------|-------|
| Full build (`./mvnw clean verify`) | ~5.5 s |
| Application startup (`java -jar`) | ~3.2–3.6 s (`Started BankingApplication in 3.631 seconds`) |
| Tomcat | 10.1.44, port 8989, context `/bank-api` |
| Hibernate | 6.6.26.Final |

For reference, the pre-migration Phase 1 `@SpringBootTest` context load (Spring Boot 2.7.18 / JDK 17) completed in ~2.9 s. Startup time is comparable; the small increase is consistent with the larger Spring Framework 6 / Hibernate 6 baseline and is well within normal range for this application.

## Code coverage comparison (conditional)

**NOT APPLICABLE.** The project has no coverage tooling configured — `grep -ri "jacoco" pom.xml` → none; no coverage plugin in the build. There is therefore no coverage report to diff before/after.

Instead, the migration guarantees **test-count parity** (playbook G18): baseline = 1 test (Phase 0/1), final = 1 test (Phase 9), 100% passing, with no test disabled, ignored, or deleted.

## API contract compatibility (conditional)

**APPLICABLE — verified.** The application exposes an OpenAPI 3.1.0 document via springdoc 2.8.9. After migration, `GET /bank-api/v3/api-docs` returns HTTP 200 and lists the identical set of 9 REST operations that existed before migration (customers + accounts CRUD, account transactions, transfer). Saved contract: `phase-9-openapi-after.json`. No endpoint paths, verbs, or path variables changed.
