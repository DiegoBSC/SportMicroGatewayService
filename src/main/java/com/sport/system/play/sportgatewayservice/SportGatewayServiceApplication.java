package com.sport.system.play.sportgatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SportGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportGatewayServiceApplication.class, args);
	}

}
