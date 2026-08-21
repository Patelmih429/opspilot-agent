package dev.mihirpatel.opspilot.triage;

import dev.mihirpatel.opspilot.incident.CreateIncidentRequest;
import dev.mihirpatel.opspilot.incident.Priority;
import dev.mihirpatel.opspilot.incident.Severity;
import dev.mihirpatel.opspilot.incident.TriageRecommendation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@Profile("!ai")
public class HeuristicTriageAdvisor implements TriageAdvisor {

    @Override
    public TriageRecommendation analyze(CreateIncidentRequest incident) {
        String evidence = (incident.title() + " " + incident.description()).toLowerCase(Locale.ROOT);
        Priority priority = priorityFor(incident.reportedSeverity(), evidence);
        String category = categoryFor(evidence);
        boolean humanReview = priority == Priority.P1 || priority == Priority.P2;

        return new TriageRecommendation(
                priority,
                category,
                "Reported by %s: %s".formatted(incident.source(), incident.title()),
                actionsFor(priority, category),
                humanReview,
                incident.reportedSeverity() == Severity.UNKNOWN ? 0.62 : 0.82,
                "heuristic-v1");
    }

    private Priority priorityFor(Severity severity, String evidence) {
        if (severity == Severity.CRITICAL || containsAny(evidence,
                "data loss", "security breach", "outage", "payments failing", "all users")) {
            return Priority.P1;
        }
        if (severity == Severity.HIGH || containsAny(evidence,
                "degraded", "timeout", "login failure", "multiple users")) {
            return Priority.P2;
        }
        if (severity == Severity.MEDIUM || containsAny(evidence, "intermittent", "slow", "warning")) {
            return Priority.P3;
        }
        return Priority.P4;
    }

    private String categoryFor(String evidence) {
        if (containsAny(evidence, "unauthorized", "token", "credential", "breach", "security")) {
            return "security";
        }
        if (containsAny(evidence, "database", "sql", "postgres", "connection pool")) {
            return "database";
        }
        if (containsAny(evidence, "api", "timeout", "gateway", "http")) {
            return "integration";
        }
        if (containsAny(evidence, "kafka", "rabbitmq", "queue", "consumer")) {
            return "messaging";
        }
        return "application";
    }

    private List<String> actionsFor(Priority priority, String category) {
        List<String> baseline = switch (category) {
            case "security" -> List.of("Preserve relevant audit logs", "Revoke exposed credentials or tokens");
            case "database" -> List.of("Check connection pool saturation", "Review slow queries and database health");
            case "integration" -> List.of("Check downstream dependency health", "Inspect timeout and retry metrics");
            case "messaging" -> List.of("Inspect consumer lag and dead-letter queues", "Verify broker health");
            default -> List.of("Review recent deployments", "Inspect application errors and saturation metrics");
        };
        if (priority == Priority.P1) {
            return List.of("Page the incident commander", baseline.get(0), baseline.get(1), "Prepare rollback or containment");
        }
        if (priority == Priority.P2) {
            return List.of("Assign an on-call owner", baseline.get(0), baseline.get(1));
        }
        return baseline;
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
