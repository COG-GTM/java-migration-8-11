# Phase 2: Intermediate 2.7.x Upgrade (CONDITIONAL)

## Detection

Command: `grep -A1 "spring-boot-starter-parent" pom.xml | grep version`

Result: current Spring Boot version = **2.7.18**.

## Decision

Condition for this phase: *"If current Spring Boot version < 2.7.0, upgrade to 2.7.18 first."*

Detected version **2.7.18 ≥ 2.7.0** (in fact it is already the latest 2.7.x patch, 2.7.18).

**Decision: BYPASS (NOT APPLICABLE).** No intermediate upgrade is required. The project is already on the recommended intermediate release (2.7.18), which is the ideal launch point for the Spring Boot 3.x upgrade in Phase 3.

## Version Baseline (pre–3.x)

| Module | Spring Boot Version | Java (pom) |
|--------|---------------------|------------|
| bank-app | 2.7.18 | 11 |

## Upgrade Report

- No POM version change performed in this phase.
- No new deprecation warnings to address at this step (deprecations relevant to 3.x are handled in Phases 5–8).
- Test baseline remains: 1 test, 100% pass (established in Phase 1). Not re-run here because no code/dependency change was made in this phase.

Proceed to Phase 3 (Core Version Upgrade to Spring Boot 3.5.5 + JDK 21).
