package com.one.gdvftp.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.one.gdvftp.repository")
@EntityScan("com.one.gdvftp.entity")

@SpringBootApplication(scanBasePackages = "com.one.gdvftp")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
