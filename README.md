# poclog

- Add on dependency:

```
<dependency>
    <groupId>com.poczinha</groupId>
    <artifactId>log</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- DDL MySQL:

```
CREATE TABLE tb_log_column (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    includedAt DATETIME,
    table_id INT
);

CREATE TABLE tb_log_correlation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    correlation VARCHAR(255),
    identifier VARCHAR(255),
    date DATETIME
);

CREATE TABLE tb_log_register (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lastValue VARCHAR(255),
    newValue VARCHAR(255),
    type ENUM('C','U','D'),
    correlation_id BIGINT,
    table_id INT,
    column_id INT
);

CREATE TABLE tb_log_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    includedAt DATETIME
);

ALTER TABLE tb_log_column ADD FOREIGN KEY (table_id) REFERENCES tb_log_table(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (correlation_id) REFERENCES tb_log_correlation(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (table_id) REFERENCES tb_log_table(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (column_id) REFERENCES tb_log_column(id);
```

DDL PostgreSQL:

```
CREATE TABLE tb_log_column (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    includedAt TIMESTAMP,
    table_id INT
);

CREATE TABLE tb_log_correlation (
    id BIGSERIAL PRIMARY KEY,
    correlation VARCHAR(255),
    identifier VARCHAR(255),
    date TIMESTAMP
);

CREATE TABLE tb_log_register (
    id BIGSERIAL PRIMARY KEY,
    lastValue VARCHAR(255),
    newValue VARCHAR(255),
    type VARCHAR(1) CHECK (type IN ('C', 'U', 'D')),
    correlation_id BIGINT,
    table_id INT,
    column_id INT
);

CREATE TABLE tb_log_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    includedAt TIMESTAMP
);

ALTER TABLE tb_log_column ADD FOREIGN KEY (table_id) REFERENCES tb_log_table(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (correlation_id) REFERENCES tb_log_correlation(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (table_id) REFERENCES tb_log_table(id);
ALTER TABLE tb_log_register ADD FOREIGN KEY (column_id) REFERENCES tb_log_column(id);
```
