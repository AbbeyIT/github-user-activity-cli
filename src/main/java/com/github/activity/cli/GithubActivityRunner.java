package com.github.activity.cli;

import com.github.activity.model.GithubEvent;
import com.github.activity.service.GithubService;
import com.github.activity.formatter.EventFormatter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubActivityRunner implements CommandLineRunner, ExitCodeGenerator {

    private final GithubService githubService;
    private final ApplicationContext context;
    private int exitCode = 0;

    public GithubActivityRunner(GithubService githubService, ApplicationContext context) {
        this.githubService = githubService;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            System.err.println("Error: Please provide a GitHub username.");
            System.err.println("Usage: github-activity <username>");
            exitCode = 1;
            SpringApplication.exit(context, this);
            return;
        }

        String username = args[0];

        try {
            System.out.println("Fetching activity for user: " + username);
            System.out.println();

            List<GithubEvent> events = githubService.fetchUserEvents(username);

            if (events.isEmpty()) {
                System.out.println("No recent activity found for user: " + username);
                return;
            }

            events.stream()
                    .map(EventFormatter::format)
                    .filter(msg -> !msg.isBlank())
                    .forEach(msg -> System.out.println("* " + msg));

        } catch (GithubService.UserNotFoundException e) {
            System.err.println("Error: GitHub user '" + username + "' not found.");
            exitCode = 1;
        } catch (GithubService.RateLimitException e) {
            System.err.println("Error: GitHub API rate limit exceeded. Please wait and try again.");
            exitCode = 1;
        } catch (GithubService.ApiException e) {
            System.err.println("Error: Failed to fetch GitHub activity. " + e.getMessage());
            exitCode = 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            exitCode = 1;
        }

        SpringApplication.exit(context, this);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}