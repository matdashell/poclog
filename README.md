# poclog

- Add on dependency:

```
<dependency>
    <groupId>com.poczinha</groupId>
    <artifactId>log</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- Add on build

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
    <resources>
        <resource>
            <directory>src/main/resources/services</directory>
            <targetPath>META-INF/services</targetPath>
        </resource>
    </resources>
</build>
```
