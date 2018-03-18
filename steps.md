# Steps to execute the program

1. mkdir microservices-project
create project folder to host microservices

2. Use spring command to create eureka-server "app"
`spring init eureka-server --build=gradle`

3. go to eureka-server cd eureka-server

3.1 open intellij, open project from existing source
3.2 allow gradle to be imported to project

4. go to build.gradle and add eureka depency:
` compile 'org.springframework.cloud:spring-cloud-starter-eureka-server' `

The result of the dependency is:

```
dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-netflix:1.4.0.RELEASE'
    }
}

dependencies {
    compile 'org.springframework.cloud:spring-cloud-starter-eureka-server'
    testCompile('org.springframework.boot:spring-boot-starter-test')
    compile 'org.springframework.boot:spring-boot-starter'
}
```
NOTE: Don't forget to add the depencency management otherwise you won't be
able to add @EnableEurekaServer in the EurkeServerApplication.java

You will receive a notice on the bottom of the screen to import gradle or
to auto import dependencies, choose 'auto import'

5. Change the name of DemoApplication on Java main folder to EurekaServerApplication
and apply these changes:

```
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

```

6. Create a file application-dev.properties

```
server.port=8761

eureka.client.registerWithEureka=false
eureka.client.fetchRegistry=false
eureka.server.waitTimeInMsWhenSyncEmpty=0
```
This configures the port of the app in the dev environment

7. Test the application by running the command
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

A Eureka page should load up at localhost:8761

8. Git add and commit

===DONE WITH FIRST SERVICE REGISTRY EUREKA SERVER APP!!!===

1. Make sure you are in the root directory microservices-project NOT in the
eureka-server project

2. use the command to create API gateway:
`spring init api-gateway --build=gradle`

3. open intellij and open the project by importing from existing source.

4. allow gradle to be imported into the project

5. go to buid.gradle and add these dependencies:
```
dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-netflix:1.4.0.RELEASE'
    }
}

dependencies {
    compile 'org.springframework.cloud:spring-cloud-starter-zuul'
    compile 'org.springframework.cloud:spring-cloud-starter-eureka'
}

```
it will ask to either to import changes or auto import changes, select "auto
import changes"

6. At java/com.example.apigateway rename DemoApplication file to
ZuulGatewayApplicaiton and add these:

```
package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ZuulGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
```

7. In the application-dev.properties set up the configuration

```
spring.application.name=api-gateway

management.security.enabled=false

eureka.client.serviceUrl.defaultZone=http://eureka:8761/eureka/

# Add color to log output
spring.output.ansi.enabled=ALWAYS
```
This config gives the application a name, which will be passed to Eureka.
It also tells Spring to turn off security in the dev profile. We need this when we check our routes later on.

*This also tells our app where the Eureka lives.* (look at the line that shows
eureka:8761

====DONE SETTING UP API-GATEWAY APP!!!====

## Setting up docker-compose

1. In the root directory microservices-project NOT in the api-gateway, create
a file docker-compose.yml and add this:

```
version: '3'

services:
  eureka:
    image: anapsix/alpine-java:8_jdk_unlimited
    ports:
      - '8761:8761'
    working_dir: /app
    volumes:
      - ./eureka-server:/app
    command: './gradlew bootRun'
    environment:
      - GRADLE_USER_HOME=cache
      - SPRING_PROFILES_ACTIVE=dev
  api-gateway:
    image: anapsix/alpine-java:8_jdk_unlimited
    ports:
      - '8080:8080'
    working_dir: /app
    volumes:
      - ./api-gateway:/app
    depends_on:
      - eureka
    command: './gradlew bootRun'
    environment:
      - GRADLE_USER_HOME=cache
      - SPRING_PROFILES_ACTIVE=dev

```
Note: All apps must be added here, and they need to have different ports for
each other.

Note2: If you want to add just one service: If you want to start just one service, you can run docker-compose up <app-name> (e.g. docker-compose
up api-gateway.)

2. docker-compose up
It will take a while if this loading for the first time, because gradle needs
to build its cache, but if everything runs fine, you are able to start creating
additional services

3. Once everything works without any error: ^C and docker-compose down

=======DONE WITH API-GATEWAY, SERVICE REGISTRY AND DOCKER COMPOSE!!!===

## Creating first API

1. On the root directory create spring boot app "users-api"
spring init users-api --build=gradle

2. Open the project on IntelliJ by importing as an existing source

3. Add Eureka as dependency management as well as dependency to build.gradle

```
dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-netflix:1.4.0.RELEASE'
    }
}

dependencies {
    compile 'org.springframework.cloud:spring-cloud-starter-eureka'
    compile 'org.springframework.boot:spring-boot-starter'
    testCompile 'org.springframework.boot:spring-boot-starter-test'
}
```

4. in the Java/com.example.usersapi/ rename the file DemoApplication to:
UsersApiApplication and add these modifications:

```
@SpringBootApplication
@EnableEurekaClient
@RestController
public class UsersApiApplication {

    // this is only for testing. It will be removed later 	
    @RequestMapping("/")
    public String home() {
        return "some users";
    }

    public static void main(String[] args) {
        SpringApplication.run(UsersApiApplication.class, args);
    }
}
```

5. in src/main/resources Setup the configuration for dev environment by
creating application-dev.properties and adding this:

```
server.port=8081

spring.application.name=users

eureka.client.serviceUrl.defaultZone=http://eureka:8761/eureka/

# Add color to log output
spring.output.ansi.enabled=ALWAYS
```
NOTE: Here you are setting up the port and giving the application a name.
Meaning any requests to /users will lead to the app. You are also
telling where Eureka lives so it can register on start-up.

6. Add the new api (users) to docker: docker-compose.yml

```
  users-api:
    image: anapsix/alpine-java:8_jdk_unlimited
    ports:
      - '8081:8081'
    working_dir: /app
    volumes:
      - ./users-api:/app
    depends_on:
      - eureka
    command: './gradlew bootRun'
    environment:
      - GRADLE_USER_HOME=cache
      - SPRING_PROFILES_ACTIVE=dev
```

7. Run docker-compose and check the URLs:
localhost:8761 API-Gateway
localhost:8080/routes you should see /users route
localhost:8080/users you should see "Users Api is working"
localhost:8081 you should also see "Users Api is working"

8. Git add and commit

======= DONE WITH API SETUP BASICS! =========

## Integrating Database to the Users Api

1. In the root directory not on api or app.
Setup the database on docker-compose.yml

```
 postgresdev:
    image: postgres
    ports:
     - "5432:5432"
    environment:
     - POSTGRES_PASSWORD=password
     - POSTGRES_USER=postgres
     - POSTGRES_DB=gapgdev
```

NOTE: I've changed the database so I can start a new versioning.

2. Go to users api, go to resources folder and add these lines to connect the database
to the users api in the application-dev.properties

```
 spring.datasource.url=jdbc:postgresql://postgresdev:5432/gapgdev
 spring.datasource.username=postgres
 spring.datasource.password=password

flyway.baselineOnMigrate=true
flyway.table=schema_u
```
NOTE: flyway in this case will connect different tables from different
apis to the same database. So DON'T FORGET to assign a different schema to
a different app.
`flyway.table=schema_u`

3. On the build.gradle of the users api add the postgress and the
flway dependencies.

```
dependencies {    
    compile 'org.springframework.cloud:spring-cloud-starter-eureka'
    compile('org.springframework.boot:spring-boot-starter')
    compile 'org.springframework.boot:spring-boot-starter-data-jpa'   
    testCompile('org.springframework.boot:spring-boot-starter-test')
    runtime('org.postgresql:postgresql')
    compile 'org.flywaydb:flyway-core'
}
```
    This manage the database integration with Flyway:
    compile 'org.springframework.boot:spring-boot-starter-data-jpa'
    The database:
    runtime('org.postgresql:postgresql')
    compile 'org.flywaydb:flyway-core'

4. On the resources folder create /db/migration folders
