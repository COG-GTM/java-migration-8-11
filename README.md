# Banking Application using Java 11, Spring Boot, Spring Security and MongoDB

RESTful API to simulate simple banking operations. 

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

* Java 11 (LTS) - OpenJDK 11 or later
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management
* A running [MongoDB](https://www.mongodb.com/) instance (defaults to `mongodb://localhost:27017/bankapp`)

The quickest way to start MongoDB locally is with Docker:

```
docker run -d --name bankapp-mongo -p 27017:27017 mongo:6
```

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-mongodb
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
lombok - to reduce boilerplate code
springdoc-openapi-ui - API documentation (OpenAPI 3.0)
spring-boot-starter-test
spring-security-test

```

## API Documentation

Please find the Rest API documentation (OpenAPI 3.0) in the below url

```
http://localhost:8989/bank-api/swagger-ui.html

```

## MongoDB

The application connects to MongoDB using the `spring.data.mongodb.uri` property defined in `src/main/resources/application.yml`
(defaults to `mongodb://localhost:27017/bankapp`). Override it via an environment variable or command line argument to point
at a different host or database name:

```
java -jar target/bank-app-1.0.0.jar --spring.data.mongodb.uri=mongodb://<host>:<port>/<database>
```

## Testing the Bank APP Rest Api

1. Please use the Swagger url to perform CRUD operations. 

2. Browse to <project-root>/src/test/resources to find sample requests to add customer and accounts.


## Migration Notes

This application has been migrated from Java 8 to Java 11 (LTS). See `MIGRATION_NOTES.md` for detailed information about the migration process and changes made.

## Authors

* **Shyam Bathina**

