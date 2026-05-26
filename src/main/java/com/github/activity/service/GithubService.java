package com.github.activity.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.activity.model.GithubEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class GithubService {

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final String USER_AGENT      = "github-activity-cli/1.0";

    private final HttpClient   httpClient;
    private final ObjectMapper objectMapper;

    public GithubService() {
        // Uses Java's built-in HttpClient — NO external HTTP library
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches the public events for a GitHub user.
     *
     * @param username GitHub username
     * @return list of GithubEvent
     * @throws UserNotFoundException if the user does not exist (404)
     * @throws RateLimitException    if GitHub rate limit is hit (403/429)
     * @throws ApiException          for any other API/network error
     */
    public List<GithubEvent> fetchUserEvents(String username) {
        String url = GITHUB_API_BASE + "/users/" + username + "/events";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", USER_AGENT)
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", e);
        }

        return switch (response.statusCode()) {
            case 200 -> parseEvents(response.body());
            case 404 -> throw new UserNotFoundException(username);
            case 403, 429 -> throw new RateLimitException();
            default -> throw new ApiException("Unexpected HTTP status: " + response.statusCode());
        };
    }

    private List<GithubEvent> parseEvents(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new ApiException("Failed to parse GitHub API response.", e);
        }
    }

    // ---- Custom Exceptions ----

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String username) {
            super("User not found: " + username);
        }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException() {
            super("GitHub API rate limit exceeded.");
        }
    }

    public static class ApiException extends RuntimeException {
        public ApiException(String message) {
            super(message);
        }
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}