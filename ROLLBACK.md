# BankApp Rollback Runbook

This document provides detailed procedures for rolling back the BankApp application from Java 11 to Java 8 if critical issues are encountered after deployment.

## Table of Contents

- [When to Rollback](#when-to-rollback)
- [Rollback Decision Matrix](#rollback-decision-matrix)
- [Pre-Rollback Checklist](#pre-rollback-checklist)
- [Rollback Procedures](#rollback-procedures)
- [Post-Rollback Verification](#post-rollback-verification)
- [Incident Documentation](#incident-documentation)
- [Recovery and Re-Migration](#recovery-and-re-migration)

## When to Rollback

Consider initiating a rollback if any of the following critical issues occur after deploying the Java 11 version.

### Critical Issues Requiring Immediate Rollback

Application startup failures that prevent the service from running are the most severe issues. If the application fails to start on Java 11 and cannot be quickly resolved, rollback should be initiated immediately. Similarly, data corruption or integrity issues affecting customer or transaction data require immediate rollback to prevent further damage.

### High-Priority Issues

Performance degradation exceeding 50% compared to the Java 8 baseline warrants serious consideration for rollback. Memory leaks causing OutOfMemoryError within normal operating parameters should also trigger rollback discussions. Security vulnerabilities introduced by the migration that cannot be quickly patched are another high-priority concern.

### Medium-Priority Issues

API compatibility issues affecting downstream consumers may require rollback if they cannot be resolved within the maintenance window. Integration failures with external systems that were working on Java 8 should be evaluated for rollback if quick fixes are not available.

## Rollback Decision Matrix

Use the following matrix to guide rollback decisions based on issue severity and resolution time.

| Issue Severity | Resolution Time < 1 hour | Resolution Time 1-4 hours | Resolution Time > 4 hours |
|----------------|--------------------------|---------------------------|---------------------------|
| Critical | Attempt fix, prepare rollback | Initiate rollback | Immediate rollback |
| High | Attempt fix | Evaluate rollback | Initiate rollback |
| Medium | Continue troubleshooting | Continue troubleshooting | Evaluate rollback |
| Low | Continue troubleshooting | Continue troubleshooting | Continue troubleshooting |

## Pre-Rollback Checklist

Before initiating rollback, complete the following checklist to ensure a smooth transition.

### Communication

1. Notify stakeholders of the planned rollback
2. Inform the development team of the issues encountered
3. Update the incident tracking system with rollback decision
4. Prepare customer communication if service disruption is expected

### Artifact Availability

1. Confirm the Java 8 version JAR file is available
2. Verify the Java 8 configuration files are accessible
3. Ensure the Java 8 runtime environment is still installed on target servers
4. Confirm database schema is compatible with Java 8 version (no schema changes in migration)

### Backup Current State

1. Capture current application logs for post-incident analysis
2. Document the current configuration settings
3. Take a database backup if data changes occurred since deployment
4. Record the current JVM metrics and heap dumps if memory issues occurred

## Rollback Procedures

### Procedure A: Application-Level Rollback (Recommended)

This procedure rolls back the application while keeping the Java 11 runtime available for future use.

#### Step 1: Stop the Current Application

Stop the running Java 11 application gracefully.

```bash
# If running as a systemd service
sudo systemctl stop bankapp

# If running as a standalone process
kill -SIGTERM $(pgrep -f bank-app-1.0.0.jar)
```

#### Step 2: Deploy Java 8 Version

Replace the Java 11 JAR with the Java 8 version.

```bash
# Backup the Java 11 JAR
mv /opt/bankapp/bank-app-1.0.0.jar /opt/bankapp/bank-app-1.0.0-java11.jar.bak

# Deploy the Java 8 JAR (from backup or rebuild)
cp /opt/bankapp/backup/bank-app-1.0.0-java8.jar /opt/bankapp/bank-app-1.0.0.jar
```

If the Java 8 JAR is not available, rebuild from the Java 8 branch:

```bash
git checkout master  # or the last known Java 8 branch
mvn clean package -DskipTests
cp target/bank-app-1.0.0.jar /opt/bankapp/
```

#### Step 3: Configure Java 8 Runtime

Ensure the Java 8 runtime is used for the application.

```bash
export JAVA_HOME=/path/to/jdk-8
export PATH=$JAVA_HOME/bin:$PATH
java -version  # Verify Java 8 is active
```

#### Step 4: Start the Application

Start the application with Java 8.

```bash
# If running as a systemd service (update service file if needed)
sudo systemctl start bankapp

# If running as a standalone process
java -Xms512m -Xmx1024m -jar /opt/bankapp/bank-app-1.0.0.jar
```

#### Step 5: Verify Rollback Success

Confirm the application is running correctly on Java 8.

```bash
curl -u bankapp:changeit http://localhost:8989/bank-api/actuator/health
curl -u bankapp:changeit http://localhost:8989/bank-api/customers/all
```

### Procedure B: Full Environment Rollback

This procedure reverts the entire environment including runtime and configuration.

#### Step 1: Stop All Services

Stop the application and any dependent services.

```bash
sudo systemctl stop bankapp
```

#### Step 2: Restore Java 8 Environment

If Java 8 was removed, reinstall it.

```bash
# For Ubuntu/Debian
sudo apt-get install openjdk-8-jdk

# For RHEL/CentOS
sudo yum install java-1.8.0-openjdk-devel
```

#### Step 3: Restore Configuration

Restore the Java 8 configuration files from backup.

```bash
cp /opt/bankapp/backup/application-java8.yml /opt/bankapp/application.yml
```

#### Step 4: Deploy and Start

Follow Steps 2-5 from Procedure A.

### Procedure C: Source Code Rollback

If a complete source code rollback is required, follow these steps.

#### Step 1: Identify the Rollback Commit

Find the last stable Java 8 commit.

```bash
git log --oneline master  # or the main branch
# Identify the commit before Java 11 migration
```

#### Step 2: Create Rollback Branch

Create a branch from the Java 8 commit.

```bash
git checkout -b rollback/java8-restore <commit-hash>
```

#### Step 3: Build and Deploy

Build the Java 8 version and deploy.

```bash
mvn clean package -DskipTests
# Follow deployment steps from Procedure A
```

## Post-Rollback Verification

After completing the rollback, perform the following verification steps.

### Functional Verification

Execute the standard smoke tests to verify core functionality.

1. Health check endpoint returns `{"status":"UP"}`
2. Customer CRUD operations work correctly
3. Account operations function as expected
4. Fund transfers complete successfully
5. API documentation (Swagger UI) is accessible

### Performance Verification

Compare performance metrics against the Java 8 baseline.

1. Response times are within acceptable thresholds
2. Memory usage is stable
3. CPU utilization is normal
4. No error rate increase in logs

### Integration Verification

Confirm all integrations are functioning.

1. Database connectivity is stable
2. External API calls (if any) succeed
3. Authentication and authorization work correctly

### Monitoring Verification

Ensure monitoring systems are receiving data.

1. Health check endpoints are being polled
2. Log aggregation is functioning
3. Metrics are being collected

## Incident Documentation

After completing the rollback, document the incident thoroughly.

### Required Documentation

Create an incident report including the following information:

1. **Incident Summary**: Brief description of the issue that triggered rollback
2. **Timeline**: Chronological sequence of events from issue detection to rollback completion
3. **Root Cause**: Preliminary analysis of what caused the issue
4. **Impact Assessment**: Services affected, duration of impact, customer impact
5. **Rollback Details**: Which procedure was used, any deviations from the runbook
6. **Lessons Learned**: What could be improved in the migration or rollback process

### Post-Incident Actions

1. Schedule a post-incident review meeting
2. Update the migration plan based on lessons learned
3. Create tickets for any identified improvements
4. Update this runbook if gaps were identified

## Recovery and Re-Migration

After stabilizing on Java 8, plan for re-migration to Java 11.

### Root Cause Analysis

Before attempting re-migration, complete a thorough root cause analysis of the issues encountered. Identify specific code, configuration, or environmental factors that caused the problems.

### Remediation Steps

Based on the root cause analysis, implement fixes in a development environment. Test the fixes thoroughly before attempting production deployment again.

### Re-Migration Checklist

Before re-attempting the Java 11 migration:

1. Root cause of original issues is identified and fixed
2. Fixes are tested in development and staging environments
3. Extended testing period completed without issues
4. Rollback artifacts are verified and ready
5. Stakeholders are informed of re-migration plan
6. Maintenance window is scheduled with adequate time for rollback if needed

### Gradual Rollout Strategy

Consider a gradual rollout approach for re-migration:

1. Deploy to a single instance first
2. Monitor for 24-48 hours
3. Gradually increase traffic to Java 11 instances
4. Complete migration only after stability is confirmed

## Emergency Contacts

Maintain a list of contacts for emergency situations:

| Role | Contact | Availability |
|------|---------|--------------|
| On-Call Engineer | [Team rotation] | 24/7 |
| Application Owner | [Name/Team] | Business hours |
| Database Administrator | [Name/Team] | Business hours |
| Infrastructure Team | [Name/Team] | Business hours |

## Related Documentation

- [DEPLOYMENT.md](DEPLOYMENT.md) - Deployment procedures
- [MIGRATION_NOTES.md](MIGRATION_NOTES.md) - Java 8 to 11 migration details
- [README.md](README.md) - General application documentation
