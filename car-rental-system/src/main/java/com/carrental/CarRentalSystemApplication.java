package com.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableAsync
public class CarRentalSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalSystemApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.web.client.RestTemplate restTemplate() {
		return new org.springframework.web.client.RestTemplate();
	}

}