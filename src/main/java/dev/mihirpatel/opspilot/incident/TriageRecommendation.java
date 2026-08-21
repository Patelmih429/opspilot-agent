package dev.mihirpatel.opspilot.incident;

import java.util.List;

public record TriageRecommendation(
        Priority priority,
        String category,
        String summary,
        List<String> recommendedActions,
        boolean requiresHumanReview,
        double confidence,
        String engine) {
}
