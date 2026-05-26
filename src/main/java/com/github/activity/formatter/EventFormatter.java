package com.github.activity.formatter;

import com.github.activity.model.GithubEvent;

import java.util.List;
import java.util.Map;

public class EventFormatter {

    /**
     * Converts a GithubEvent into a human-readable activity string.
     */
    public static String format(GithubEvent event) {
        String repo    = event.getRepo() != null ? event.getRepo().getName() : "unknown";
        String type    = event.getType();
        Map<String, Object> payload = event.getPayload();

        return switch (type) {

            case "PushEvent" -> {
                int commits = getCommitCount(payload);

                yield commits > 0
                        ? "Pushed " + commits + " commit" +
                        (commits != 1 ? "s" : "") +
                        " to " + repo
                        : "Pushed commits to " + repo;
            }

            case "IssuesEvent" -> {
                String action = getStringField(payload, "action");
                yield switch (action) {
                    case "opened"  -> "Opened a new issue in " + repo;
                    case "closed"  -> "Closed an issue in " + repo;
                    case "reopened"-> "Reopened an issue in " + repo;
                    default        -> "Performed issue action '" + action + "' in " + repo;
                };
            }

            case "IssueCommentEvent" -> {
                String action = getStringField(payload, "action");
                yield "Commented on an issue in " + repo + " (" + action + ")";
            }

            case "WatchEvent" ->
                    "Starred " + repo;

            case "ForkEvent" ->
                    "Forked " + repo;

            case "CreateEvent" -> {
                String refType = getStringField(payload, "ref_type");
                String ref     = getStringField(payload, "ref");
                yield ref.isBlank()
                        ? "Created " + refType + " in " + repo
                        : "Created " + refType + " '" + ref + "' in " + repo;
            }

            case "DeleteEvent" -> {
                String refType = getStringField(payload, "ref_type");
                String ref     = getStringField(payload, "ref");
                yield "Deleted " + refType + " '" + ref + "' in " + repo;
            }

            case "PullRequestEvent" -> {
                String action = getStringField(payload, "action");
                yield switch (action) {
                    case "opened"      -> "Opened a pull request in " + repo;
                    case "closed"      -> "Closed a pull request in " + repo;
                    case "merged"      -> "Merged a pull request in " + repo;
                    case "reopened"    -> "Reopened a pull request in " + repo;
                    default            -> "Pull request action '" + action + "' in " + repo;
                };
            }

            case "PullRequestReviewEvent" ->
                    "Reviewed a pull request in " + repo;

            case "PullRequestReviewCommentEvent" ->
                    "Commented on a pull request review in " + repo;

            case "CommitCommentEvent" ->
                    "Commented on a commit in " + repo;

            case "ReleaseEvent" -> {
                String action = getStringField(payload, "action");
                yield "Published a release (" + action + ") in " + repo;
            }

            case "PublicEvent" ->
                    "Made " + repo + " public";

            case "MemberEvent" -> {
                String action = getStringField(payload, "action");
                yield "Member action '" + action + "' in " + repo;
            }

            case "GollumEvent" ->
                    "Updated the wiki in " + repo;

            default ->
                    "Performed " + type.replace("Event", "") + " in " + repo;
        };
    }

    // ---- Helpers ----

    @SuppressWarnings("unchecked")
    private static int getCommitCount(Map<String, Object> payload) {
        if (payload == null) return -1;

        Object size = payload.get("size");
        if (size instanceof Number n) {
            return n.intValue();
        }

        Object commits = payload.get("commits");
        if (commits instanceof List<?> list) {
            return list.size();
        }

        return -1;
    }

    private static String getStringField(Map<String, Object> payload, String key) {
        if (payload == null) return "";
        Object val = payload.get(key);
        return val != null ? val.toString() : "";
    }

}