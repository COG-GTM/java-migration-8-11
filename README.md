# Banking Application using Java 8, Spring Boot, Spring Security and H2 DB

RESTful API to simulate simple banking operations.

## Requirements

* CRUD operations for customers and accounts.
* Support deposits and withdrawals on accounts.
* Internal transfer support (i.e. a customer may transfer funds from one account to another).

## Getting Started

### Prerequisites

* **Java 8** (JDK 1.8) - OpenJDK 8 or Oracle JDK 8
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) 3.6+ - Dependency Management
* [Lombok](https://projectlombok.org/) plugin for your IDE

### Setup and Run

1. Clone the repository:

```bash
git clone https://github.com/COG-GTM/Aplicacion-de-Banca-Spring-Boot.git
cd Aplicacion-de-Banca-Spring-Boot
```

2. Enable Lombok support on your IDE. Refer to:
   [https://projectlombok.org/setup/eclipse](https://projectlombok.org/setup/eclipse)

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/bank-app-1.0.0.jar
```

5. The API will be available at `http://localhost:8989/bank-api/`

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - In-memory database
lombok - to reduce boilerplate code
springfox-swagger2 - API documentation
springfox-swagger-ui - Swagger UI
spring-boot-starter-test
spring-security-test
```

## Java 8 Features Used

This application leverages the following Java 8 features:

* **Streams API** - Used in service layer for collection processing (e.g., `findAll()`, `findTransactionsByAccountNumber()`)
* **Optional** - Used with `map()`, `orElse()`, `orElseGet()`, and `ifPresent()` for null-safe operations
* **Method References** - Used with streams for cleaner code (e.g., `bankingServiceHelper::convertToCustomerDomain`)
* **Lambda Expressions** - Used throughout the service layer for functional-style processing
* **java.time API** - `LocalDateTime` replaces legacy `java.util.Date` for date/time handling

## Swagger

Please find the REST API documentation at:

```
http://localhost:8989/bank-api/swagger-ui.html
```

## H2 In-Memory Database

Make sure to use `jdbc:h2:mem:testdb` as your JDBC URL. If you intend to use a custom database name, please
define datasource properties in `application.yml`.

```
http://localhost:8989/bank-api/h2-console/
```

## Testing

### Running Tests

```bash
mvn test
```

The project includes:
* **Context load test** - Verifies Spring Boot application context loads correctly
* **Service layer unit tests** - Comprehensive tests for all banking operations using Mockito

### Manual Testing

1. Use the Swagger UI to perform CRUD operations
2. Browse to `src/test/resources` to find sample requests to add customers and accounts

## Authors

* **Shyam Bathina**

