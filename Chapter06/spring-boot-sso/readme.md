1. create Mysql DB: test
2. run query: grant all privileges on test.* to 'root'@'localhost' identified by '123456';
3. mvn clean package
4. run com.test.login.LoginApplication
5. run com.test.resource.ResourceApplication
6. run com.test.web1.Web1Application
7. run com.test.web2.Web2Application
8. http://localhost
9. login username and password: user



```
keytool -genkey -keystore keystore.jks -alias tycoonclient -keyalg RSA
```



```
Warning:
JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.jks -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
coderdream@MacBook-Pro ~ %
```





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

  

