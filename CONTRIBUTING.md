# Contributing to BankApp

Thank you for your interest in contributing to BankApp! This document provides guidelines
for contributing to this Spring Boot banking application.

## Development Setup

### Prerequisites

- Java 8 or higher
- Maven 3.6+
- IDE with Lombok support (IntelliJ IDEA, Eclipse with Lombok plugin)

### Getting Started

1. Follow the README Quickstart section to set up the project locally
2. Create branches using the format: `feature/short-description` or `fix/issue-description`
3. Ensure all tests pass before submitting changes

### Local Development

```bash
# Install dependencies
$ mvn clean install

# Run the application
$ mvn spring-boot:run

# Run tests
$ mvn test

# Access development tools
# Swagger UI: http://localhost:8989/bank-api/swagger-ui.html
# H2 Console: http://localhost:8989/bank-api/h2-console/
```

## Coding Standards

### Code Style

- Follow standard Java naming conventions
- Use Lombok annotations to reduce boilerplate code
- Maintain consistent indentation (tabs/spaces as per existing code)
- Add meaningful comments for complex business logic

### Architecture Guidelines

- Follow the existing layered architecture (Controller → Service → Repository)
- Keep controllers thin - business logic belongs in services
- Use DTOs (domain objects) for data transfer between layers
- Maintain proper separation of concerns

### API Design

- Follow RESTful conventions for new endpoints
- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Include proper Swagger annotations for API documentation
- Maintain consistent error response formats

## Testing

### Writing Tests

- Write unit tests for new features and bug fixes
- Use meaningful test method names that describe the scenario
- Include both positive and negative test cases
- Mock external dependencies appropriately

### Test Data

- Use the existing sample JSON files in `src/test/resources/` as templates
- Create realistic test data that represents actual banking scenarios
- Avoid hardcoded values where possible

## Pull Requests

### Before Submitting

- Ensure all tests pass: `mvn test`
- Verify the application starts successfully: `mvn spring-boot:run`
- Test your changes using Swagger UI or curl commands
- Update documentation if you've added new features

### PR Guidelines

- Keep changes focused and atomic
- Write clear, descriptive commit messages
- Include screenshots or API examples for UI/API changes
- Link to related issues if applicable
- Provide clear description of what changed and why

### PR Template

```text
## Summary
Brief description of changes

## Changes Made
- List of specific changes
- New features added
- Bugs fixed

## Testing
- How you tested the changes
- Any new test cases added

## Documentation
- Updated README/docs if needed
- Added/updated API documentation
```

## Database Changes

### Schema Modifications

- Document any JPA entity changes
- Provide migration notes if schema changes affect existing data
- Test with both empty and populated H2 database

### Sample Data

- Update test JSON files if entity structure changes
- Ensure backward compatibility where possible

## Security Considerations

- Never commit sensitive information (passwords, keys, tokens)
- Follow Spring Security best practices
- Validate all user inputs
- Use parameterized queries to prevent SQL injection

## Getting Help

- Check existing issues and documentation first
- Use descriptive titles when creating new issues
- Provide steps to reproduce for bug reports
- Include relevant logs and error messages

## Code of Conduct

- Be respectful and inclusive in all interactions
- Focus on constructive feedback
- Help maintain a welcoming environment for all contributors

Thank you for contributing to BankApp!
