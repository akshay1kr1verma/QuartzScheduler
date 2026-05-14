# Spring Boot + Quartz + MariaDB + Flyway Setup Guide

This repository demonstrates how to configure:

* Spring Boot
* Quartz Scheduler
* MariaDB running in Docker
* Flyway Database Migration
* Persistent Quartz Jobs using JDBC JobStore

It also includes common troubleshooting steps encountered while integrating Quartz with MariaDB and Flyway.

---

# Tech Stack

* Java 24
* Spring Boot 3.5
* Quartz Scheduler 2.5
* MariaDB 10.3
* Flyway 11
* Docker

---

# 1. Run MariaDB using Docker

## Start MariaDB Container

```bash
docker run --name mariadbtest \
-e MYSQL_ROOT_PASSWORD=mypass \
-e MYSQL_DATABASE=test \
-p 3306:3306 \
-d mariadb:10.3
```

---

# 2. Verify Container is Running

```bash
docker ps
```

Expected:

```text
CONTAINER ID   IMAGE          PORTS                    NAMES
xxxx           mariadb:10.3   0.0.0.0:3306->3306/tcp   mariadbtest
```

---

# 3. Connect to MariaDB

```bash
docker exec -it mariadbtest mysql -u root -p
```

Password:

```text
mypass
```

---

# 4. Verify Database

```sql
SHOW DATABASES;
```

Switch database:

```sql
USE test;
```

Show tables:

```sql
SHOW TABLES;
```

---

# 5. Beautify SQL Output

Use:

```sql
SELECT * FROM QRTZ_JOB_DETAILS\G
```

instead of:

```sql
SELECT * FROM QRTZ_JOB_DETAILS;
```

---

# 6. Exit MariaDB

```sql
exit
```

or

```sql
quit
```

or

```sql
\q
```

---

# 7. Required Dependencies

```xml
<dependencies>

    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Quartz -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-quartz</artifactId>
    </dependency>

    <!-- JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MariaDB -->
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
    </dependency>

    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

</dependencies>
```

---

# 8. application.properties

```properties
server.port=7612

spring.datasource.url=jdbc:mariadb://localhost:3306/test
spring.datasource.username=root
spring.datasource.password=mypass
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none

spring.flyway.enabled=true

spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=never

spring.quartz.properties.org.quartz.scheduler.instanceName=MyScheduler
spring.quartz.properties.org.quartz.threadPool.threadCount=5

spring.quartz.properties.org.quartz.jobStore.class=org.springframework.scheduling.quartz.LocalDataSourceJobStore
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.useProperties=false
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
```

---

# 9. Flyway Migration Setup

Place migration file here:

```text
src/main/resources/db/migration/V1__create_quartz_tables.sql
```

---

# 10. Important Quartz Notes

## DO NOT manually start Quartz Scheduler

Wrong:

```java
@PostConstruct
public void startSchedule() {
    scheduler.start();
}
```

Spring Boot automatically starts Quartz.

---

## DO NOT use

```properties
spring.quartz.jdbc.initialize-schema=always
```

together with Flyway.

Use:

```properties
spring.quartz.jdbc.initialize-schema=never
```

---

# 11. Common Errors & Fixes

---

## ERROR

```text
Access denied for user 'root'
```

### FIX

Wrong password was used.

Correct password:

```properties
spring.datasource.password=mypass
```

---

## ERROR

```text
Unknown database 'test'
```

### FIX

Database was not created.

Use:

```bash
-e MYSQL_DATABASE=test
```

while creating Docker container.

---

## ERROR

```text
jdbcUrl is required with driverClassName
```

### FIX

Use:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/test
```

NOT:

```properties
spring.datasource.jdbc-url
```

---

## ERROR

```text
Table 'test.QRTZ_LOCKS' doesn't exist
```

### ROOT CAUSE

Quartz started before Flyway migration OR Flyway history existed but tables were manually deleted.

---

### FIX

Open MariaDB:

```bash
docker exec -it mariadbtest mysql -u root -p
```

Run:

```sql
USE test;

DROP TABLE IF EXISTS flyway_schema_history;
```

Delete Quartz tables:

```sql
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;
```

Restart application.

Flyway will recreate tables.

---

## ERROR

```text
ObjectAlreadyExistsException
```

### ROOT CAUSE

Quartz jobs were persisted in DB.

On restart, same job already existed.

---

### FIX

```java
JobKey jobKey =
        new JobKey(className.getSimpleName(), "grp2");

if (!scheduler.checkExists(jobKey)) {
    scheduler.scheduleJob(jobDetail, triggerDetail);
}
```

---

## ERROR

```text
NotSerializableException
```

### ROOT CAUSE

Quartz persists JobDataMap into DB.

Custom object inside JobDataMap was not serializable.

---

### FIX

```java
public class TriggerInfo implements Serializable {
}
```

---

# 12. Verify Flyway Migration

Expected logs:

```text
Migrating schema `test` to version "1 - create quartz tables"
```

and:

```text
Scheduler quartzScheduler_$_NON_CLUSTERED started.
```

---

# 13. Useful Docker Commands

## Stop Container

```bash
docker stop mariadbtest
```

## Start Container

```bash
docker start mariadbtest
```

## Remove Container

```bash
docker rm -f mariadbtest
```

## View Logs

```bash
docker logs mariadbtest
```

---

# 14. Concepts Learned

* Quartz Scheduler
* Persistent JobStore
* Flyway Migration
* MariaDB
* Docker
* JDBC Locking
* Quartz Recovery
* Job Identity Management
* Scheduler Persistence
* Spring Boot Lifecycle
* Flyway Schema History

---

# Final Working Flow

1. Docker starts MariaDB
2. Flyway runs migration
3. Quartz tables are created
4. Spring Boot auto-starts Quartz
5. Scheduler persists jobs into DB
6. Jobs survive application restart
7. Duplicate jobs are avoided using JobKey checks

---
