# Contributing

## Development Setup

- Follow README Quickstart.
- Create branches: `devin/<short-purpose>`.

## IDE Configuration for Java 11

After updating to Java 11, developers need to configure their IDE to use Java 11 for this project.

### Eclipse / Spring Tool Suite (STS)

1. **Install Java 11 JDK**
   - Download and install Java 11 JDK from
     [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
     or [AdoptOpenJDK](https://adoptopenjdk.net/)
   - Set JAVA_HOME environment variable to Java 11 installation path

2. **Configure Eclipse JDK**
   - Navigate to: `Window > Preferences > Java > Installed JREs`
   - Click `Add...` and select `Standard VM`
   - Browse to your Java 11 installation directory
   - Click `Finish` and ensure Java 11 is checked as the default

3. **Configure Project Settings**
   - Right-click project in Project Explorer > `Properties`
   - Navigate to `Java Build Path > Libraries`
   - Remove old JRE System Library
   - Click `Add Library > JRE System Library > Next`
   - Select `Workspace default JRE (JavaSE-11)` or `Alternate JRE` and choose Java 11
   - Click `Finish`

4. **Configure Compiler Settings**
   - Navigate to `Java Compiler` in project properties
   - Set `Compiler compliance level` to `11`
   - Ensure `Use default compliance settings` is checked
   - Click `Apply and Close`

5. **Enable Lombok**
   - Download lombok.jar from <https://projectlombok.org/download>
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation directory and click `Install/Update`
   - Restart Eclipse

6. **Refresh and Rebuild**
   - Run: `mvn clean install` from terminal or Maven view
   - Project > Clean > Clean all projects
   - Project > Build Project

### IntelliJ IDEA

1. **Install Java 11 JDK**
   - Download and install Java 11 JDK
   - IDEA can auto-download JDKs: `File > Project Structure > SDKs > + > Download JDK`

2. **Configure Project SDK**
   - Navigate to: `File > Project Structure > Project`
   - Set `Project SDK` to Java 11
   - Set `Project language level` to `11 - Local variable syntax for lambda parameters`
   - Click `Apply`

3. **Configure Module Settings**
   - In `Project Structure > Modules`
   - Select your module and go to `Sources` tab
   - Set `Language level` to `11`
   - Click `Apply`

4. **Configure Maven**
   - Navigate to: `File > Settings > Build, Execution, Deployment > Build Tools > Maven > Runner`
   - Set `JRE` to Java 11
   - Click `Apply`

5. **Enable Lombok Plugin**
   - Navigate to: `File > Settings > Plugins`
   - Search for "Lombok" and install the official plugin
   - Restart IDEA
   - Enable annotation processing: `Settings > Build, Execution, Deployment > Compiler > Annotation Processors`
   - Check `Enable annotation processing`

6. **Reimport Maven Project**
   - Right-click on `pom.xml` > `Maven > Reload Project`
   - Or use Maven tool window: `Reload All Maven Projects` button

### Visual Studio Code

1. **Install Java 11 JDK**
   - Download and install Java 11 JDK
   - Set JAVA_HOME environment variable

2. **Install Extensions**
   - Install "Extension Pack for Java" (includes Language Support, Debugger, Maven, etc.)
   - Install "Lombok Annotations Support for VS Code"

3. **Configure Java Home**
   - Open Settings (Ctrl+,) or `File > Preferences > Settings`
   - Search for "java.configuration.runtimes"
   - Edit in settings.json:

     ```json
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-11",
         "path": "/path/to/java-11-jdk",
         "default": true
       }
     ]
     ```

4. **Configure Maven**
   - Ensure Maven is installed (or use wrapper: `./mvnw`)
   - VS Code will automatically detect Maven projects

5. **Reload Project**
   - Command Palette (Ctrl+Shift+P): `Java: Clean Java Language Server Workspace`
   - Restart VS Code
   - Run: `mvn clean install` in terminal

### Verification

After configuring your IDE, verify the setup:

```bash
# Compile project
mvn clean compile

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

Access the application at: <http://localhost:8989/bank-api/swagger-ui.html>

## Coding Standards

- Format + lint before pushing.
- Write tests for new features.

## Pull Requests

- Small, focused changes.
- Include screenshots/logs for docs updates.
- Link related issues.
