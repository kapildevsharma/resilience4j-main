package com.cosume.cosumeRest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

// RESTS API with Swagger UI
// // Swagger API ::  Doc: http://localhost:8087/swagger-ui.html
@SpringBootApplication
public class CosumeRestApplication {

	private static final Logger logger = LogManager.getLogger(CosumeRestApplication.class);
		
	public static void main(String[] args) {
		logger.info("Application started");
		SpringApplication.run(CosumeRestApplication.class, args);
	}

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
