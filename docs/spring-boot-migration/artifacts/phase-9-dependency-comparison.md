# Phase 9: Dependency Tree Comparison (Before → After)

Sources: `phase-0-dependency-tree-before.txt` / `phase-0-dependency-list-before.txt` (Spring Boot 2.7.18, JDK 11) vs `phase-9-dependency-tree-after.txt` / `phase-9-dependency-list-after.txt` (Spring Boot 3.5.5, JDK 21).

## Key managed/explicit version deltas

| Artifact | Before | After |
|----------|--------|-------|
| org.springframework.boot:spring-boot | 2.7.18 | **3.5.5** |
| org.springframework:spring-core | 5.3.31 | **6.2.10** |
| org.springframework.security:spring-security-core | 5.7.11 | **6.5.3** |
| org.hibernate(.orm):hibernate-core | 5.6.15.Final | **6.6.26.Final** |
| org.apache.tomcat.embed:tomcat-embed-core | 9.0.83 | **10.1.44** (Jakarta Servlet 6) |
| com.h2database:h2 | 2.1.214 | **2.3.232** (BOM-managed) |
| org.junit.jupiter:junit-jupiter | 5.8.2 | **5.12.2** (BOM-managed) |
| org.mockito:mockito-junit-jupiter | 4.5.1 | **5.17.0** (BOM-managed) |
| org.glassfish.jaxb:jaxb-runtime | 2.3.8 (explicit) | **4.0.5** (BOM-managed) |
| org.springdoc:springdoc-openapi-**ui** | 1.6.15 | replaced by **springdoc-openapi-starter-webmvc-ui 2.8.9** |
| jakarta.persistence:jakarta.persistence-api | 2.2.3 | **3.2.0** |
| jakarta.xml.bind:jakarta.xml.bind-api | 2.3.3 | **4.0.2** |

## Namespace family shift

Entire `javax.*` Jakarta EE stack → `jakarta.*` (persistence, servlet, transaction, validation, annotation, xml.bind) via the Spring Boot 3.5.5 BOM. Servlet container moved Tomcat 9 (Servlet 4/`javax`) → Tomcat 10.1 (Servlet 6/`jakarta`).

## Version-decrease check (G3 — never downgrade)

`diff` of before/after version numbers shows **no dependency was downgraded**; every changed artifact moved to an equal-or-higher version. Springdoc is an artifact-ID replacement (1.x `-ui` → 2.x `-starter-webmvc-ui`), not a downgrade.

## Tree size

Before: 111 resolved dependency entries. After: comparable set (Spring Boot 3 BOM), with springdoc consolidated into the `-starter-webmvc-*` artifacts and `swagger-*-jakarta` replacing the old swagger artifacts.
