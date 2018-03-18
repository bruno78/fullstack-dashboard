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

