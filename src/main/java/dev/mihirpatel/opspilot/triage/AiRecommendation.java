package dev.mihirpatel.opspilot.triage;

import dev.mihirpatel.opspilot.incident.Priority;

import java.util.List;

record AiRecommendation(
        Priority priority,
        String category,
        String summary,
        List<String> recommendedActions,
        boolean requiresHumanReview,
        double confidence) {
}
