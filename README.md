# Banking Application using Java 17, Spring Boot 3.2.x, Spring Security 6.x and H2 DB

RESTful API to simulate simple banking operations.

## Requirements

* CRUD operations for customers and accounts.
* Support deposits and withdrawals on accounts.
* Internal transfer support (i.e. a customer may transfer funds from one account to another).

## Getting Started

1. Checkout the project from GitHub

```bash
git clone https://github.com/COG-GTM/java-migration-8-11
```

2. Enable Lombok support on your IDE

Refer to the following link for instructions:

```
https://projectlombok.org/setup/eclipse
```

3. Open IDE of your choice and Import as existing maven project in your workspace

```bash
# Import existing maven project
mvn clean install

# If using STS, Run As Spring Boot App
```

4. Default port for the api is **8989**

### Prerequisites

* **Java 17 (LTS)** - OpenJDK 17 or later
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

### Technology Stack

| Component | Version |
|---|---|
| Java | 17 (LTS) |
| Spring Boot | 3.2.5 |
| Spring Security | 6.x |
| Hibernate | 6.x (via Spring Boot 3.2.x) |
| Jakarta EE | 9+ (jakarta.* namespace) |
| SpringDoc OpenAPI | 2.3.0 |
| H2 Database | Latest (in-memory) |
| Lombok | Latest |
| Maven | 3.8.8+ |

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - In-memory database
lombok - to reduce boilerplate code
springdoc-openapi-starter-webmvc-ui - API documentation (OpenAPI 3.0)
spring-boot-starter-test
spring-security-test
```

## Building and Running

```bash
# Build the JAR
mvn clean package

# Run the application
java -jar target/bank-app-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

### Environment Verification

```bash
# Ensure Java 17 is installed
java -version
# Should output: openjdk version "17.x.x"

# Ensure JAVA_HOME points to JDK 17
echo $JAVA_HOME
```

## API Documentation

The REST API documentation (OpenAPI 3.0) is available at:

* **Swagger UI**: [http://localhost:8989/bank-api/swagger-ui/index.html](http://localhost:8989/bank-api/swagger-ui/index.html)
* **OpenAPI JSON**: [http://localhost:8989/bank-api/v3/api-docs](http://localhost:8989/bank-api/v3/api-docs)

## H2 In-Memory Database

Make sure to use `jdbc:h2:mem:testdb` as your JDBC URL. If you intend to use a custom database name, please
define datasource properties in `application.yml`.

* **H2 Console**: [http://localhost:8989/bank-api/h2-console/](http://localhost:8989/bank-api/h2-console/)

## Monitoring

* **Actuator Health**: [http://localhost:8989/bank-api/actuator/health](http://localhost:8989/bank-api/actuator/health)

## Testing the Bank APP REST API

1. Please use the Swagger UI URL to perform CRUD operations.

2. Browse to `<project-root>/src/test/resources` to find sample requests to add customer and accounts.

## Migration Notes

This application has been migrated from Java 8 to Java 11 (LTS) and subsequently to **Java 17 (LTS)** with **Spring Boot 3.2.5**. See `RELEASE_NOTES.md` for detailed information about the Java 17 migration, including:

* javax.* to jakarta.* namespace migration
* Spring Security 6.x SecurityFilterChain configuration
* SpringDoc OpenAPI 2.x migration
* CI/CD pipeline updates

## Authors

* **Shyam Bathina**

