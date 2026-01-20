# Banking Application using Java 17, Spring Boot 3.x, Spring Security and H2 DB

RESTful API to simulate simple banking operations.

## Migration to Java 17 and Spring Boot 3.x

This application has been migrated from Java 8/11 to Java 17 with Spring Boot 3.x. Key changes include:

### Java 17 Requirements
- **Minimum Java Version**: Java 17 or higher is required
- **JAVA_HOME**: Must point to JDK 17 installation
- No special JVM flags are required for production deployment

### Spring Boot 3.x Changes
- **Spring Boot Version**: Upgraded to 3.2.0
- **Spring Security 6.x**: Uses new `SecurityFilterChain` configuration (replaces deprecated `WebSecurityConfigurerAdapter`)
- **Jakarta EE 10**: All `javax.*` packages migrated to `jakarta.*` namespace

### API Documentation Migration
- **Springfox Swagger** replaced with **SpringDoc OpenAPI**
- Swagger UI available at: `/bank-api/swagger-ui/index.html`
- OpenAPI spec available at: `/bank-api/v3/api-docs`

### Dependencies Updated
- `jakarta.persistence-api` (replaces `javax.persistence`)
- `jakarta.validation-api` (replaces `javax.validation`)
- `jakarta.xml.bind-api` (replaces `javax.xml.bind`)
- `springdoc-openapi-starter-webmvc-ui` (replaces `springfox-swagger2`)

## Requirements

*	CRUD operations for customers and accounts.
*	Support deposits and withdrawals on accounts.
*	Internal transfer support (i.e. a customer may transfer funds from one account to another).


## Getting Started

1. Checkout the project from GitHub

```
git clone https://github.com/sbathina/BankApp

```
2. Enable Lombok support on your IDE

Refer to the following link for instructions:

```
https://projectlombok.org/setup/eclipse

```
3. Open IDE of your choice and Import as existing maven project in your workspace

```
- Import existing maven project
- Run mvn clean install
- If using STS, Run As Spring Boot App

```
4. Default port for the api is 8989


### Prerequisites

* Java 17 or higher
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - Inmemory database
lombok - to reduce boilerplate code
springdoc-openapi-starter-webmvc-ui - OpenAPI documentation (Spring Boot 3.x compatible)
spring-boot-starter-test
spring-security-test

```

## Swagger / OpenAPI Documentation

Please find the Rest API documentation in the below url

```
http://localhost:8989/bank-api/swagger-ui/index.html

```

## H2 In-Memory Database

Make sure to use jdbc:h2:mem:testdb as your jdbc url. If you intend to you use custom database name, please
define datasource properties in application.yml

```
http://localhost:8989/bank-api/h2-console/

```

## Testing the Bank APP Rest Api

1. Please use the Swagger url to perform CRUD operations. 

2. Browse to <project-root>/src/test/resources to find sample requests to add customer and accounts.


## Authors

* **Shyam Bathina**

