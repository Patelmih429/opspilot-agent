package dev.mihirpatel.opspilot.incident;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String title,
        String description,
        String source,
        Severity reportedSeverity,
        IncidentStatus status,
        Priority priority,
        String category,
        String summary,
        List<String> recommendedActions,
        Boolean requiresHumanReview,
        Double confidence,
        String triageEngine,
        Instant createdAt,
        Instant updatedAt) {
}
