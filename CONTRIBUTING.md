# Contributing to BankApp

Thank you for your interest in contributing to BankApp! This guide will help you get started
with development and contribution workflows.

## Development Setup

### Prerequisites

- Java 8 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Any modern IDE (Spring Tool Suite, IntelliJ IDEA, Eclipse)
- Git for version control

### Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:

   ```bash
   $ git clone https://github.com/YOUR_USERNAME/java-migration-8-11.git
   $ cd java-migration-8-11
   ```

3. **Follow README Quickstart** to set up the development environment:

   ```bash
   $ mvn clean install
   $ mvn spring-boot:run
   ```

4. **Verify setup** by accessing:
   - Swagger UI: <http://localhost:8989/bank-api/swagger-ui.html>
   - H2 Console: <http://localhost:8989/bank-api/h2-console/>

### Branch Naming Convention

Create feature branches using the format: `devin/<short-purpose>`

Examples:

- `devin/add-customer-validation`
- `devin/fix-transaction-bug`
- `devin/update-swagger-docs`

## Coding Standards

### Code Style

- Follow existing Spring Boot patterns and conventions
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Leverage Lombok annotations to reduce boilerplate code

### Before Pushing

- **Format code** according to project standards
- **Run linting** to catch style issues:

  ```bash
  $ mvn compile
  ```

- **Run all tests** to ensure functionality:

  ```bash
  $ mvn test
  ```

- **Verify application starts** without errors:

  ```bash
  $ mvn spring-boot:run
  ```

### Testing Requirements

- Write unit tests for new business logic
- Include integration tests for new API endpoints
- Maintain or improve test coverage
- Test both success and error scenarios

## Pull Request Process

### Before Submitting

1. **Ensure all tests pass**: `mvn test`
2. **Verify application builds**: `mvn clean install`
3. **Test your changes** manually using Swagger UI
4. **Update documentation** if you've changed APIs or configuration

### Pull Request Guidelines

- **Small, focused changes**: One feature or fix per PR
- **Clear description**: Explain what changed and why
- **Include screenshots/logs** for documentation updates
- **Link related issues** using GitHub keywords (fixes #123)
- **Update README** if you've added new features or changed setup

### PR Description Template

```markdown
## Summary
Brief description of changes

## Changes Made
- List of specific changes
- API endpoints added/modified
- Configuration changes

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Documentation updated

## Screenshots (if applicable)
Include screenshots for UI or documentation changes
```

## Development Workflow

### Local Development

1. **Start the application**:

   ```bash
   $ mvn spring-boot:run
   ```

2. **Make changes** to code
3. **Test changes** using Swagger UI or curl commands
4. **Run tests** to verify functionality
5. **Commit changes** with descriptive messages

### Debugging

- **Enable debug mode**:

  ```bash
  $ mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
  ```

- **Use H2 Console** to inspect database state
- **Check application logs** for error details

## Code Review Process

### What We Look For

- **Functionality**: Does the code work as intended?
- **Testing**: Are there adequate tests?
- **Documentation**: Is the code well-documented?
- **Style**: Does it follow project conventions?
- **Security**: Are there any security concerns?

### Review Timeline

- Initial review within 2-3 business days
- Follow-up reviews within 1 business day
- Merge after approval and CI passes

## Getting Help

- **Documentation**: Check the [README](README.md) first
- **Issues**: Search existing GitHub issues
- **Questions**: Open a new issue with the "question" label
- **Discussions**: Use GitHub Discussions for broader topics

## License

By contributing to BankApp, you agree that your contributions will be licensed under the MIT License.
