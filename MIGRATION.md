# Migration for Java 8 to Java 11

## Summary
This migration updates the BankApp from Java 8 to Java 11, which is a breaking change that requires Java 11+ runtime environment and may affect Spring Boot 2.1.4 module compatibility.

## Affected APIs/Configs
- **Maven Configuration**: `pom.xml` now requires Java 11+ for compilation and runtime
- **Build Environment**: Maven wrapper updated to 3.9.6, compiler plugin to 3.11.0
- **Runtime Environment**: Application will not start on Java 8 JVMs
- **Compilation**: Strict linting enabled (`-Xlint:all -Werror`) may fail on previously ignored warnings

## Step-by-step Migration
1. **Update Java Runtime Environment**:
   ```bash
   # Install Java 11 (Ubuntu/Debian)
   sudo apt update && sudo apt install openjdk-11-jdk
   
   # Set JAVA_HOME to Java 11
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
   
   # Verify Java version
   java -version  # Should show Java 11
   ```

2. **Update Build Environment**:
   ```bash
   # Clean previous builds
   mvn clean
   
   # Compile with Java 11
   mvn compile
   
   # Run tests
   mvn test
   ```

3. **Verify Application Startup**:
   ```bash
   # Start application
   mvn spring-boot:run
   
   # Check application is accessible
   curl http://localhost:8989/bank-api/swagger-ui.html
   ```

4. **Check for Module Issues**:
   ```bash
   # Build JAR and check for JDK internal dependencies
   mvn package
   jdeps --jdk-internals target/bank-app-1.0.0.jar
   ```

## Rollback
If issues occur, revert to Java 8 configuration:

1. **Revert Git Changes**:
   ```bash
   git revert 5807472ba9a2ecb29b5f21bbdf3d0b94c1b6440e
   ```

2. **Reset Java Environment**:
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
   java -version  # Should show Java 8
   ```

3. **Clean and Rebuild**:
   ```bash
   mvn clean compile test
   ```

## Known Issues
- **Spring Boot 2.1.4**: May require additional dependencies for JAXB modules removed in Java 11
- **Strict Compilation**: `-Werror` flag will fail builds on any compiler warnings
- **Maven Wrapper**: Completely replaced - may affect existing build scripts
