package com.example.dowjonesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class DowJonesApiApplication {

//	@RequestMapping("/")
//	public String Home() {
//		return "Dow Jones Api is working!";
//	}

	public static void main(String[] args) {
		SpringApplication.run(DowJonesApiApplication.class, args);
	}
}
