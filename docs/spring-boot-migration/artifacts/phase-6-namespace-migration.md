# Phase 6: Namespace Migration (javax → jakarta)

## Step 1 — OpenRewrite attempt (mandated first)

Command:
```
./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:6.19.0:run \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.16.0
```

**Result: FAILED** (`BUILD FAILURE`) — see `phase-6-openrewrite.log`.

**Reason:** The `rewrite-maven-plugin` parses sources with full type attribution against the project classpath. After Phase 3 the classpath is Jakarta-only (Spring Boot 3.5.5 provides `jakarta.persistence`, not `javax.persistence`), so the pre-migration sources that still `import javax.persistence.*` do not compile, and the plugin aborts during its parse/compile step (`cannot find symbol: class Entity/Id/Column/...`). OpenRewrite cannot transform sources it cannot parse. This is the playbook's documented "OpenRewrite fails → manual find-replace" condition.

## Step 2 — Manual find-replace fallback

Scope (from `grep -rn "javax\." src`): **exclusively `javax.persistence.*`** — 47 references across 7 JPA entity classes. No `javax.servlet`, `javax.validation`, `javax.annotation`, `javax.xml`, or other javax families exist in source, so the transformation is an unambiguous 1:1 package rename.

Command:
```
sed -i 's/javax\.persistence/jakarta.persistence/g' src/main/java/com/coding/exercise/bankapp/model/*.java
```

Files changed (import count each):

| File | javax.persistence refs migrated |
|------|--------------------------------|
| model/Account.java | 9 |
| model/Customer.java | 9 |
| model/BankInfo.java | 7 |
| model/Transaction.java | 7 |
| model/Address.java | 5 |
| model/Contact.java | 5 |
| model/CustomerAccountXRef.java | 5 |

Post-check: `grep -rn "javax\." src` → **NONE** (all migrated). `grep -rc "jakarta.persistence" src` → 47 references present.

## Step 3 — XML schema updates (conditional)

| File | Present? | Action |
|------|----------|--------|
| `persistence.xml` | `find . -name persistence.xml` → none | NOT APPLICABLE |
| `web.xml` | `find . -name web.xml` → none | NOT APPLICABLE |

(Spring Boot uses JPA auto-configuration; no `persistence.xml`. Fully embedded servlet container; no `web.xml`.)

## Verification

`./mvnw clean compile` (JDK 21) → **BUILD SUCCESS** (`phase-6-compile-after.log`). All Phase 3 compilation errors are resolved: the Jakarta persistence namespace errors by this phase's rename, and the `WebSecurityConfigurerAdapter` error by the Phase 5 security migration. Main source set now compiles clean under Spring Boot 3.5.5 / JDK 21.

Proceed to Phase 7.
