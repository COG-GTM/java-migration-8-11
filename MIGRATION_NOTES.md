# Java 8 to Java 11 Migration Notes

This document summarizes the changes made to migrate the Banking Application from Java 8 to Java 11.

## Overview

The migration updates the project from Java 8 (1.8) to Java 11 (LTS), following a phased approach to ensure build and test compatibility while modernizing the toolchain.

## Build Configuration Changes

### Maven Properties

Updated `pom.xml` properties to target Java 11:

```xml
<properties>
    <java.version>11</java.version>
    <maven.compiler.release>11</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Maven Plugins

Added and upgraded the following plugins for Java 11 compatibility:

| Plugin | Version | Purpose |
|--------|---------|---------|
| maven-compiler-plugin | 3.11.0 | Compile with `<release>11</release>` |
| maven-surefire-plugin | 3.2.5 | Run unit tests |
| maven-failsafe-plugin | 3.2.5 | Run integration tests |
| maven-javadoc-plugin | 3.6.3 | Generate Javadoc |
| maven-enforcer-plugin | 3.5.0 | Enforce Java 11+ requirement |

The enforcer plugin ensures builds fail if attempted with Java versions below 11.

## Removed JDK Modules

Java 11 removed several Java EE modules from the JDK. After analysis, this project does not use any of the removed modules:

- JAXB (javax.xml.bind): Not used
- JAX-WS (javax.xml.ws): Not used
- JavaFX: Not used
- CORBA: Not used
- Nashorn: Not used

No additional dependencies were required for removed modules.

## Encapsulation and Reflection

No illegal reflective access warnings were encountered during testing. The Spring Boot 2.1.4 and related dependencies are compatible with Java 11's module system when running on the classpath (non-modular mode).

## CI/CD Updates

Added GitHub Actions workflow (`.github/workflows/java11-build.yml`) that:

- Uses Temurin JDK 11 distribution
- Caches Maven dependencies
- Runs build, test, and package phases
- Triggers on push to master/main and devin branches, and on pull requests

## Runtime Considerations

### JVM Options

Java 11 uses G1 as the default garbage collector. If you were using CMS or Parallel GC with Java 8, review your JVM options.

For GC logging, use Unified Logging syntax:
```bash
-Xlog:gc*:file=gc.log:time,uptime,level,tags
```

### TLS/Security

Java 11 enables TLS 1.3 by default. If connecting to legacy endpoints that don't support TLS 1.3, you may need to configure:
```bash
-Djdk.tls.client.protocols=TLSv1.2
```

## Validation

The following validations were performed:

- Build compiles successfully with Java 11
- All unit tests pass (1 test, 0 failures)
- Maven enforcer plugin confirms Java 11+ requirement
- No illegal reflective access warnings in test output

## Follow-up Recommendations

1. Consider upgrading Spring Boot to a newer 2.x version for better Java 11 support and security patches
2. Upgrade Springfox Swagger to version 2.10.0+ or migrate to SpringDoc OpenAPI
3. Add more comprehensive unit tests for better coverage
4. Consider adopting Java 11 language features (var, HttpClient) in future updates
