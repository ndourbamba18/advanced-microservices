package net.accel_tech.category_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CategoryServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(CategoryServiceApplication.class, args);

		System.out.println("Category Service started successfully...");
	}

}
