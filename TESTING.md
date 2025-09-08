# Testing

## Quickstart

```bash
# Install dependencies and compile
mvn clean compile

# Run unit tests
mvn test

# Run tests with coverage report
mvn clean verify

# View coverage report
open target/site/jacoco/index.html
```

## Test Structure

### Layout
- Unit tests: `src/test/java/com/coding/exercise/bankapp/`
- Test classes follow pattern: `*Test.java`
- Mirror main source structure in test directory

### Test Classes
- `BankingServiceImplTest` - Core business logic tests (7 tests)
- `BankingServiceHelperTest` - Data conversion utility tests (3 tests)  
- `AccountControllerTest` - REST controller tests (2 tests)
- `BankingApplicationTests` - Spring Boot context loading test (1 test)

## Conventions

### Naming
- Test methods: `methodName_ShouldExpectedBehavior_WhenCondition`
- Example: `transferDetails_ShouldReturnSuccess_WhenValidTransfer`

### Pattern
- **Arrange**: Set up test data and mocks
- **Act**: Execute the method under test
- **Assert**: Verify expected outcomes

### Framework
- **JUnit 5** (Jupiter) for test structure and assertions
- **Mockito 2.28.2** for mocking dependencies
- **JaCoCo 0.8.12** for coverage reporting

## Isolation Strategy

### Mocking
- All external dependencies are mocked using `@Mock` annotations
- Repository interactions are stubbed with `when().thenReturn()`
- No real database connections or network calls

### Dependency Injection
- Manual dependency injection via reflection for complex scenarios
- `MockitoAnnotations.initMocks(this)` in `@BeforeEach` setup

### Time & Randomness
- Tests use fixed test data (no random values)
- No time-dependent logic in current test suite
- Future tests should inject clock/time sources for deterministic behavior

## Coverage Targets

### Current Thresholds
- **Lines**: 20% (baseline - enforced in CI)
- **Branches**: 2% (baseline - enforced in CI)

### Target Goals
- **Lines**: 80% (long-term goal)
- **Branches**: 70% (long-term goal)

### Current Coverage (as of latest run)
- **Overall**: 22% instructions, 2% branches
- **Service Layer**: 41% (highest priority for improvement)
- **Domain Objects**: 32%
- **Model Entities**: 10%

### Coverage Exclusions
- Generated code and boilerplate (Lombok builders, getters/setters)
- Configuration classes (already at 100%)

## Running Tests

### Local Development
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BankingServiceImplTest

# Run with coverage
mvn clean verify

# Skip tests during build
mvn compile -DskipTests
```

### CI/CD
- Tests run automatically on pull requests
- Coverage reports uploaded to Codecov
- Build fails if coverage drops below thresholds

## Performance

### Current Metrics
- **Total runtime**: ~9-10 seconds
- **Unit tests only**: ~0.8 seconds
- **Spring context loading**: ~6.5 seconds (BankingApplicationTests)

### Optimization Tips
- Unit tests are fast due to mocking
- Integration test (BankingApplicationTests) takes longer due to Spring context
- Consider `@MockBean` for faster Spring test context if needed

## Troubleshooting

### Common Issues

#### NullPointerException in Tests
- **Cause**: Missing mock setup or dependency injection
- **Solution**: Verify all `@Mock` dependencies are properly injected
- **Example**: Use reflection for complex dependency injection scenarios

#### Test Compilation Errors
- **Cause**: Version conflicts between JUnit 4/5 or Mockito versions
- **Solution**: Check `pom.xml` excludes JUnit 4 and uses compatible versions

#### Flaky Tests
- **Prevention**: All tests use deterministic data and mocked dependencies
- **If encountered**: Check for shared state, async operations, or time dependencies

#### Slow Test Suite
- **Current**: Tests run in <10 seconds (well within acceptable range)
- **If degraded**: Profile with `mvn test -X` and check for I/O operations

### Coverage Issues

#### Low Coverage Warnings
- **Expected**: Current baseline is intentionally low (20%/2%)
- **Action**: Incrementally add tests for high-risk modules first

#### Coverage Report Not Generated
```bash
# Ensure JaCoCo plugin is active
mvn clean verify
# Check target/site/jacoco/index.html exists
```

#### Thresholds Too Strict
- Adjust in `pom.xml` under `jacoco-maven-plugin` configuration
- Update `docs/unit-test-intake.yaml` to match

## Test Development Guidelines

### Adding New Tests
1. Identify the class/method to test
2. Create test class following naming convention
3. Set up mocks for all dependencies
4. Write tests covering happy path, edge cases, and error conditions
5. Verify coverage improvement with `mvn verify`

### High-Priority Areas for Testing
1. `BankingServiceImpl.transferDetails` - Fund transfer logic
2. `BankingServiceImpl.updateCustomer` - Customer management
3. `BankingServiceImpl.addNewAccount` - Account creation
4. `BankingServiceHelper` conversion methods
5. Controller error handling and validation

### Test Data Strategy
- Use builder patterns for complex objects
- Create reusable test fixtures for common scenarios
- Avoid hardcoded values; use meaningful constants
- Mock external dependencies consistently

## Integration with Development Workflow

### Pre-commit
- Run `mvn test` before committing changes
- Verify coverage hasn't decreased significantly

### Pull Requests
- CI automatically runs full test suite
- Coverage reports available in PR comments
- All tests must pass before merge

### Continuous Improvement
- Monitor coverage trends over time
- Prioritize testing for bug-prone areas
- Refactor tests as code evolves
- Update thresholds incrementally toward targets
