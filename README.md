# JPA Entity Logger Library Documentation

## Introduction
This library is designed to log changes to JPA entities. It captures field-by-field changes and compares them with existing data in the database.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.poczinha</groupId>
    <artifactId>log</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Configuration

### Annotations

1. **@EnableLog**: Add this annotation to your Spring Boot Application class.
2. **@LogEntity**: Add this annotation to the JPA entities you want to log.

```java
@LogEntity(name = "CustomName", ignore = false)
```

- `name`: Custom name to appear in the log.
- `ignore`: Whether to ignore this entity or not.

### Example

```java
@LogEntity(name = "MyEntity")
public class MyEntity {
    // ...
}
```

### Repository and Entity Scanning

Add the library's entities and repositories to your Spring context:

```java
@EnableJpaRepositories(basePackages = {
    "your.package.repository",
    LOG_REPOSITORY_SCAN
})
```

```java
em.setPackagesToScan(
    "your.package.entity",
    LOG_ENTITY_SCAN
);
```

## Usage

After compiling, the library will generate a service class for each annotated entity. The service class will have two methods: `logCreateUpdate` and `logDelete`.

### Example Service Class

```java
public class MyEntityLogService {
    public void logCreateUpdate(MyEntity entity, String username) {
        // Implementation
    }

    public void logDelete(MyEntity entity, String username) {
        // Implementation
    }
}
```

### Using the Generated Service

Inject the generated service into your existing service or repository classes and use the `logCreateUpdate` and `logDelete` methods where needed.

```java
@Service
@RequiredArgsConstructor
public class MyService {

    private final MyRepository repository;
    private final MyEntityLogService logService;

    public MyEntity create(MyEntity entity, String username) {
        logService.logCreateUpdate(entity, username);
        return repository.save(entity);
    }
}
```

## Configuration

### MySQL Script

```sql
CREATE TABLE tb_log_column (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30),
    included_at DATETIME
);

CREATE TABLE tb_log_correlation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(30),
    date DATETIME
);

CREATE TABLE tb_log_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60),
    included_at DATETIME
);

CREATE TABLE tb_log_register (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(30),
    previus_value VARCHAR(255),
    new_value VARCHAR(255),
    type ENUM('C','U','D'),
    correlation_id BIGINT,
    table_id INT,
    column_id INT,
    FOREIGN KEY (correlation_id) REFERENCES tb_log_correlation(id),
    FOREIGN KEY (table_id) REFERENCES tb_log_table(id),
    FOREIGN KEY (column_id) REFERENCES tb_log_column(id)
);

```

### PostgreSQL Script

```sql
CREATE TABLE tb_log_column (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30),
    included_at TIMESTAMP
);

CREATE TABLE tb_log_correlation (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(30),
    date TIMESTAMP
);

CREATE TABLE tb_log_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60),
    included_at TIMESTAMP
);

CREATE TABLE tb_log_register (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(30),
    previus_value VARCHAR(255),
    new_value VARCHAR(255),
    type VARCHAR(1) CHECK (type IN ('C', 'U', 'D')),
    correlation_id BIGINT,
    table_id INT,
    column_id INT,
    FOREIGN KEY (correlation_id) REFERENCES tb_log_correlation(id),
    FOREIGN KEY (table_id) REFERENCES tb_log_table(id),
    FOREIGN KEY (column_id) REFERENCES tb_log_column(id)
);

```

That's it! Now, every time the code runs, the changes will be logged.