package com.github.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GithubUserActivityApplication {

	public static void main(String[] args) {
		// Disable Spring Boot banner and logs for clean CLI output
		System.setProperty("spring.main.banner-mode", "off");
		System.setProperty("logging.level.root", "OFF");
		SpringApplication.run(GithubUserActivityApplication.class, args);
	}
}