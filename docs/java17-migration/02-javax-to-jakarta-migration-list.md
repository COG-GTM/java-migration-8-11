# javax.* to jakarta.* Migration List

This document provides a comprehensive list of all `javax.*` imports that must be migrated to `jakarta.*` as part of the Java 17 and Spring Boot 3.x migration.

## Summary

The BankApp project contains **47 total javax.persistence imports** across **7 entity classes** that require migration to the jakarta namespace.

## Migration Overview

| Package | Files Affected | Total Imports |
|---------|----------------|---------------|
| javax.persistence | 7 | 47 |

## Detailed File-by-File Analysis

### 1. Account.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/Account.java`

**Lines 6-14**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 6 | `import javax.persistence.CascadeType;` | `import jakarta.persistence.CascadeType;` |
| 7 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 8 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 9 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 10 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 11 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |
| 12 | `import javax.persistence.OneToOne;` | `import jakarta.persistence.OneToOne;` |
| 13 | `import javax.persistence.Temporal;` | `import jakarta.persistence.Temporal;` |
| 14 | `import javax.persistence.TemporalType;` | `import jakarta.persistence.TemporalType;` |

**Total imports to migrate**: 9

### 2. Address.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/Address.java`

**Lines 5-9**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 5 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 6 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 7 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 8 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 9 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |

**Total imports to migrate**: 5

### 3. BankInfo.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/BankInfo.java`

**Lines 5-11**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 5 | `import javax.persistence.CascadeType;` | `import jakarta.persistence.CascadeType;` |
| 6 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 7 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 8 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 9 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 10 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |
| 11 | `import javax.persistence.OneToOne;` | `import jakarta.persistence.OneToOne;` |

**Total imports to migrate**: 7

### 4. Contact.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/Contact.java`

**Lines 5-9**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 5 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 6 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 7 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 8 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 9 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |

**Total imports to migrate**: 5

### 5. Customer.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/Customer.java`

**Lines 6-14**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 6 | `import javax.persistence.CascadeType;` | `import jakarta.persistence.CascadeType;` |
| 7 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 8 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 9 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 10 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |
| 11 | `import javax.persistence.ManyToOne;` | `import jakarta.persistence.ManyToOne;` |
| 12 | `import javax.persistence.OneToOne;` | `import jakarta.persistence.OneToOne;` |
| 13 | `import javax.persistence.Temporal;` | `import jakarta.persistence.Temporal;` |
| 14 | `import javax.persistence.TemporalType;` | `import jakarta.persistence.TemporalType;` |

**Total imports to migrate**: 9

### 6. CustomerAccountXRef.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java`

**Lines 5-9**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 5 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 6 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 7 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 8 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 9 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |

**Total imports to migrate**: 5

### 7. Transaction.java

**File**: `src/main/java/com/coding/exercise/bankapp/model/Transaction.java`

**Lines 6-12**: javax.persistence imports

| Line | Current Import | Target Import |
|------|----------------|---------------|
| 6 | `import javax.persistence.Column;` | `import jakarta.persistence.Column;` |
| 7 | `import javax.persistence.Entity;` | `import jakarta.persistence.Entity;` |
| 8 | `import javax.persistence.GeneratedValue;` | `import jakarta.persistence.GeneratedValue;` |
| 9 | `import javax.persistence.GenerationType;` | `import jakarta.persistence.GenerationType;` |
| 10 | `import javax.persistence.Id;` | `import jakarta.persistence.Id;` |
| 11 | `import javax.persistence.Temporal;` | `import jakarta.persistence.Temporal;` |
| 12 | `import javax.persistence.TemporalType;` | `import jakarta.persistence.TemporalType;` |

**Total imports to migrate**: 7

## Unique Import Mappings

The following table shows all unique javax.persistence imports used in the project and their jakarta equivalents:

| javax.persistence Import | jakarta.persistence Import | Usage Count |
|--------------------------|----------------------------|-------------|
| `javax.persistence.CascadeType` | `jakarta.persistence.CascadeType` | 3 |
| `javax.persistence.Column` | `jakarta.persistence.Column` | 7 |
| `javax.persistence.Entity` | `jakarta.persistence.Entity` | 7 |
| `javax.persistence.GeneratedValue` | `jakarta.persistence.GeneratedValue` | 7 |
| `javax.persistence.GenerationType` | `jakarta.persistence.GenerationType` | 7 |
| `javax.persistence.Id` | `jakarta.persistence.Id` | 7 |
| `javax.persistence.ManyToOne` | `jakarta.persistence.ManyToOne` | 1 |
| `javax.persistence.OneToOne` | `jakarta.persistence.OneToOne` | 3 |
| `javax.persistence.Temporal` | `jakarta.persistence.Temporal` | 3 |
| `javax.persistence.TemporalType` | `jakarta.persistence.TemporalType` | 3 |

## Migration Script

The following sed commands can be used to perform the migration automatically:

```bash
# Navigate to the project root
cd ~/repos/java-migration-8-11

# Replace all javax.persistence imports with jakarta.persistence
find src -name "*.java" -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} \;
```

Alternatively, using a more targeted approach for each file:

```bash
# Account.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/Account.java

# Address.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/Address.java

# BankInfo.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/BankInfo.java

# Contact.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/Contact.java

# Customer.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/Customer.java

# CustomerAccountXRef.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java

# Transaction.java
sed -i 's/import javax\.persistence\./import jakarta.persistence./g' src/main/java/com/coding/exercise/bankapp/model/Transaction.java
```

## IDE Refactoring Support

Most modern IDEs support automatic migration:

### IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Go to **Refactor** > **Migrate Packages and Classes** > **Java EE to Jakarta EE**
3. Select the scope (entire project or specific modules)
4. Review and apply the changes

### Eclipse

1. Open the project in Eclipse
2. Use **Search** > **File Search** to find all `javax.persistence` imports
3. Use **Search** > **Replace** to replace with `jakarta.persistence`

## Verification Steps

After migration, verify the changes by:

1. **Compile the project**:
   ```bash
   mvn clean compile
   ```

2. **Run the tests**:
   ```bash
   mvn test
   ```

3. **Search for remaining javax imports**:
   ```bash
   grep -r "import javax\." src/main/java/
   ```
   This should return no results after successful migration.

## Dependencies Required

Ensure the following dependency is present in pom.xml for Jakarta Persistence:

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

Note: When using Spring Boot 3.x, this dependency is managed automatically through `spring-boot-starter-data-jpa`.

## Additional javax.* Packages to Watch

While the current codebase only uses `javax.persistence`, be aware of other common javax packages that may need migration in future development:

| javax Package | jakarta Package | Common Usage |
|---------------|-----------------|--------------|
| javax.servlet | jakarta.servlet | Servlet filters, HTTP handling |
| javax.validation | jakarta.validation | Bean validation annotations |
| javax.annotation | jakarta.annotation | @PostConstruct, @PreDestroy |
| javax.transaction | jakarta.transaction | @Transactional (JTA) |
| javax.inject | jakarta.inject | Dependency injection |
| javax.ws.rs | jakarta.ws.rs | JAX-RS REST services |

## References

- [Jakarta EE 9 Namespace Change](https://jakarta.ee/specifications/)
- [Spring Boot 3.0 Migration Guide - Jakarta EE](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#jakarta-ee)
- [Hibernate 6 Migration Guide](https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc)
