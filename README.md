# Banking Application using Java 11, Spring Boot, Spring Security and H2 DB

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
2. Ensure Java 11 JDK is installed

Download and install Java 11 JDK from:
```
https://adoptium.net/ (recommended)
or
https://www.oracle.com/java/technologies/downloads/#java11

```

Set the JAVA_HOME environment variable:
```
# Linux/macOS
export JAVA_HOME=/path/to/java-11-jdk
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\path\to\java-11-jdk
set PATH=%JAVA_HOME%\bin;%PATH%

```

Verify Java version:
```
java -version

```

3. Enable Lombok support on your IDE

Refer to the following link for instructions:

```
https://projectlombok.org/setup/eclipse

```
4. Open IDE of your choice and Import as existing maven project in your workspace

```
- Import existing maven project
- Run mvn clean install
- If using STS, Run As Spring Boot App

```
5. Default port for the api is 8989


### Prerequisites

* Java 11 JDK
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) 3.6+ - Dependency Management

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - Inmemory database
lombok - to reduce boilerplate code
springdoc-openapi-ui - OpenAPI 3.0 documentation
spring-boot-starter-test
spring-security-test

```

## API Documentation

The application uses SpringDoc OpenAPI 3.0 for API documentation. Access the interactive API documentation at:

```
http://localhost:8989/bank-api/swagger-ui.html

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

