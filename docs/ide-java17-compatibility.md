# IDE Compatibility for Java 17

This document outlines IDE compatibility requirements and configuration for developing the BankApp with Java 17.

## Supported IDEs

### IntelliJ IDEA

**Minimum Version**: 2021.2 or later (full Java 17 support)

**Recommended Version**: 2023.x or later

**Configuration Steps**:
1. Go to File > Project Structure > Project
2. Set Project SDK to Java 17 (temurin-17 recommended)
3. Set Project language level to "17 - Sealed types, always-strict floating-point semantics"
4. Go to File > Settings > Build, Execution, Deployment > Compiler > Java Compiler
5. Set Target bytecode version to 17

### Eclipse

**Minimum Version**: Eclipse 2021-09 (4.21) or later

**Recommended Version**: Eclipse 2023-x or later

**Configuration Steps**:
1. Go to Window > Preferences > Java > Installed JREs
2. Add JDK 17 installation
3. Go to Window > Preferences > Java > Compiler
4. Set Compiler compliance level to 17
5. For Maven projects, right-click project > Maven > Update Project

### Spring Tool Suite (STS)

**Minimum Version**: STS 4.12.0 or later (based on Eclipse 2021-09)

**Recommended Version**: STS 4.20.x or later

**Configuration Steps**:
1. Same as Eclipse configuration above
2. STS 4.x is built on Eclipse and inherits Java 17 support from Eclipse 2021-09+

### Visual Studio Code

**Minimum Version**: Latest VS Code with Java Extension Pack

**Required Extensions**:
- Extension Pack for Java (vscjava.vscode-java-pack)
- Language Support for Java by Red Hat (redhat.java)
- Debugger for Java (vscjava.vscode-java-debug)
- Maven for Java (vscjava.vscode-maven)

**Configuration Steps**:
1. Install JDK 17 on your system
2. Set `java.configuration.runtimes` in settings.json:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/path/to/jdk-17",
      "default": true
    }
  ]
}
```

### Apache NetBeans

**Minimum Version**: NetBeans 12.5 or later

**Recommended Version**: NetBeans 19 or later

**Configuration Steps**:
1. Go to Tools > Java Platforms
2. Add JDK 17 platform
3. Right-click project > Properties > Sources
4. Set Source/Binary Format to 17

## Java 17 Features Available

With Java 17, developers can use the following language features:

- **Sealed Classes** (JEP 409): Restrict which classes can extend or implement a class/interface
- **Pattern Matching for instanceof** (JEP 394): Simplified type checking and casting
- **Records** (JEP 395): Compact syntax for data carrier classes
- **Text Blocks** (JEP 378): Multi-line string literals
- **Switch Expressions** (JEP 361): Enhanced switch with expression form
- **Helpful NullPointerExceptions** (JEP 358): More informative NPE messages

## Lombok Compatibility

Lombok is compatible with Java 17. Ensure you have:
- Lombok version 1.18.22 or later (recommended: 1.18.30+)
- IDE Lombok plugin installed and enabled

### IntelliJ IDEA
- Install Lombok plugin from Marketplace
- Enable annotation processing in Settings > Build > Compiler > Annotation Processors

### Eclipse/STS
- Run lombok.jar installer or manually add to eclipse.ini
- Restart IDE after installation

### VS Code
- Lombok support is included in the Java Extension Pack

## Troubleshooting

### Common Issues

1. **"Invalid target release: 17"**
   - Ensure JDK 17 is installed and configured as the project SDK
   - Verify JAVA_HOME environment variable points to JDK 17

2. **Lombok annotations not recognized**
   - Update Lombok to version 1.18.22+
   - Reinstall Lombok IDE plugin
   - Enable annotation processing

3. **Maven build fails with Java version mismatch**
   - Ensure Maven is using JDK 17: `mvn -version`
   - Set JAVA_HOME before running Maven

## Verification

To verify your IDE is correctly configured for Java 17:

1. Create a test file with Java 17 syntax:
```java
public class Java17Test {
    public static void main(String[] args) {
        // Text block (Java 15+)
        String json = """
            {
                "name": "test"
            }
            """;
        
        // Pattern matching for instanceof (Java 16+)
        Object obj = "Hello";
        if (obj instanceof String s) {
            System.out.println(s.toUpperCase());
        }
        
        System.out.println("Java 17 features work!");
    }
}
```

2. The file should compile and run without errors if Java 17 is properly configured.
