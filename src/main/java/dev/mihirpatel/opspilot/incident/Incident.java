package dev.mihirpatel.opspilot.incident;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "incidents")
class Incident {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false, length = 100)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_severity", nullable = false, length = 20)
    private Severity reportedSeverity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Priority priority;

    @Column(length = 100)
    private String category;

    @Column(length = 2000)
    private String summary;

    @Column(name = "recommended_actions", columnDefinition = "text")
    private String recommendedActions;

    @Column(name = "requires_human_review")
    private Boolean requiresHumanReview;

    private Double confidence;

    @Column(name = "triage_engine", length = 50)
    private String triageEngine;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected Incident() {
    }

    static Incident create(CreateIncidentRequest request, Instant now) {
        Incident incident = new Incident();
        incident.id = UUID.randomUUID();
        incident.title = request.title().trim();
        incident.description = request.description().trim();
        incident.source = request.source().trim();
        incident.reportedSeverity = request.reportedSeverity();
        incident.status = IncidentStatus.RECEIVED;
        incident.createdAt = now;
        incident.updatedAt = now;
        return incident;
    }

    void apply(TriageRecommendation recommendation, String actionsJson, Instant now) {
        priority = recommendation.priority();
        category = recommendation.category();
        summary = recommendation.summary();
        recommendedActions = actionsJson;
        requiresHumanReview = recommendation.requiresHumanReview();
        confidence = recommendation.confidence();
        triageEngine = recommendation.engine();
        status = IncidentStatus.TRIAGED;
        updatedAt = now;
    }

    UUID id() { return id; }
    String title() { return title; }
    String description() { return description; }
    String source() { return source; }
    Severity reportedSeverity() { return reportedSeverity; }
    IncidentStatus status() { return status; }
    Priority priority() { return priority; }
    String category() { return category; }
    String summary() { return summary; }
    String recommendedActions() { return recommendedActions; }
    Boolean requiresHumanReview() { return requiresHumanReview; }
    Double confidence() { return confidence; }
    String triageEngine() { return triageEngine; }
    Instant createdAt() { return createdAt; }
    Instant updatedAt() { return updatedAt; }
}
