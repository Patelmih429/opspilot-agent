package dev.mihirpatel.opspilot.triage;

import dev.mihirpatel.opspilot.incident.CreateIncidentRequest;
import dev.mihirpatel.opspilot.incident.TriageRecommendation;

public interface TriageAdvisor {
    TriageRecommendation analyze(CreateIncidentRequest incident);
}
