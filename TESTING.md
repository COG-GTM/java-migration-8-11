# Testing

## Quickstart
```bash
mvn -q -B -DskipITs verify   # Phase A (report-only)
ENFORCE=true mvn -q -B -DskipITs verify   # Phase B (enforce 95/90)
```

## Conventions

* Layout: `src/test/java/**`
* Naming: `should<Behavior>_when_<Condition>`
* Pattern: Arrange / Act / Assert

## Isolation

* No network/DB; use fakes/mocks
* Freeze time via clock seam
* Seed RNG in setup

## Coverage Rules (Phased)

* Global: Lines ≥95%, Branches ≥90%
* Per-file: ≥90% lines (exceptions documented)
* Diff-coverage: ≥95% on changed lines
* **Phase A:** report-only; **Phase B:** enforced

## Troubleshooting

* Flaky async → fake schedulers, teardown
* Slow → split tests, remove I/O, profile
* Plateau → see `.devin/coverage-history.json` & stop rules

## Scripts

* `scripts/timebox.sh <minutes> <command>` - Timeout wrapper
* `scripts/jacoco_gate.py [path]` - Coverage reporting
* `scripts/plateau_guard.py <coverage>` - Plateau detection

## Phase Toggle

* **Local Phase B:** `ENFORCE=true mvn verify -Pcoverage-enforce`
* **CI Phase B:** Set repository variable `ENFORCE_COVERAGE=true`
