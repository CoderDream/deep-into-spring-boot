1. create Mysql DB: test
2. config application.yml set db url,username,password
3. run Junit: MysqlTest to create default user
4. run Spring Boot: WebApplicaton
5. http://localhost
6. login with username: user, password: user
--------------------------------------------
grant all privileges on *.* to 'root'@'localhost'  identified by '123456' with grant option;





https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-parent

- 修改M5为SR7

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-parent</artifactId>
            <version>Brixton.SR7</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

- 编译（在工程的根目录）

  ```
  coderdream@MacBook-Pro spring-boot-security % mvn clean package -D skipTests
  ```

- 启动Web：

  ```
  coderdream@MacBook-Pro spring-boot-security % java -jar web/target/web-1.0-SNAPSHOT.jar
  ```

- 登录

  ```
  http://localhost
  user/user
  ```

  



