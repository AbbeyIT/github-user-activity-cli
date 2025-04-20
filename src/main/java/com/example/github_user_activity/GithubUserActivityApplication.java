package com.example.github_user_activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
public class GithubUserActivityApplication implements CommandLineRunner{
	private final GithubActivityService activityService;

	public GithubUserActivityApplication(GithubActivityService activityService) {
		this.activityService = activityService;
	}

	public static void main(String[] args) {
		SpringApplication.run(GithubUserActivityApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length == 0) {
			System.out.println("Usage: github-activity <username>");
			return;
		}

		String username = args[0];
		activityService.fetchAndDisplayActivity(username);
	}


}
