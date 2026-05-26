package com.github.activity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubEvent {

    private String type;

    @JsonProperty("repo")
    private Repo repo;

    @JsonProperty("payload")
    private Map<String, Object> payload;

    // --- Getters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    // --- Nested Repo class ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}