package com.example.github_user_activity;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service

public class GithubActivityService {
    public void fetchAndDisplayActivity(String username) {
        String url = "https://api.github.com/users/" + username + "/events";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                System.out.println("User not found: " + username);
                return;
            }

            if (response.statusCode() != 200) {
                System.out.println("Error: " + response.statusCode());
                return;
            }

            String body = response.body();
            displayActivity(body);

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void displayActivity(String json) {
        // Very basic extraction using regex (since no external libs allowed)
        Pattern eventPattern = Pattern.compile("\"type\":\"(\\w+)\".*?\"repo\":\\{\"name\":\"(.*?)\"");
        Matcher matcher = eventPattern.matcher(json);

        int count = 0;

        while (matcher.find() && count < 10) { // show only first 10 events
            String type = matcher.group(1);
            String repo = matcher.group(2);

            switch (type) {
                case "PushEvent" -> System.out.println("Pushed commits to " + repo);
                case "IssuesEvent" -> System.out.println("Opened a new issue in " + repo);
                case "WatchEvent" -> System.out.println("Starred " + repo);
                case "CreateEvent" -> System.out.println("Created something in " + repo);
                default -> System.out.println(type + " in " + repo);
            }

            count++;
        }

        if (count == 0) {
            System.out.println("No recent public activity found.");
        }
    }
}
