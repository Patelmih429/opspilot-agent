package dev.mihirpatel.opspilot.incident;

import dev.mihirpatel.opspilot.triage.TriageAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentService {

    private static final TypeReference<List<String>> ACTION_LIST = new TypeReference<>() { };

    private final IncidentRepository repository;
    private final TriageAdvisor triageAdvisor;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Autowired
    public IncidentService(IncidentRepository repository, TriageAdvisor triageAdvisor, ObjectMapper objectMapper) {
        this(repository, triageAdvisor, objectMapper, Clock.systemUTC());
    }

    IncidentService(IncidentRepository repository, TriageAdvisor triageAdvisor, ObjectMapper objectMapper, Clock clock) {
        this.repository = repository;
        this.triageAdvisor = triageAdvisor;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request) {
        return toResponse(repository.save(Incident.create(request, clock.instant())));
    }

    @Transactional(readOnly = true)
    public IncidentResponse get(UUID id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> list(int limit) {
        int boundedLimit = Math.max(1, Math.min(limit, 100));
        return repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, boundedLimit)).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IncidentResponse triage(UUID id) {
        Incident incident = find(id);
        CreateIncidentRequest input = new CreateIncidentRequest(
                incident.title(), incident.description(), incident.source(), incident.reportedSeverity());
        TriageRecommendation recommendation = triageAdvisor.analyze(input);
        incident.apply(recommendation, writeActions(recommendation.recommendedActions()), clock.instant());
        return toResponse(incident);
    }

    private Incident find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }

    private IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.id(),
                incident.title(),
                incident.description(),
                incident.source(),
                incident.reportedSeverity(),
                incident.status(),
                incident.priority(),
                incident.category(),
                incident.summary(),
                readActions(incident.recommendedActions()),
                incident.requiresHumanReview(),
                incident.confidence(),
                incident.triageEngine(),
                incident.createdAt(),
                incident.updatedAt());
    }

    private String writeActions(List<String> actions) {
        try {
            return objectMapper.writeValueAsString(actions);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize triage actions", exception);
        }
    }

    private List<String> readActions(String actions) {
        if (actions == null || actions.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(actions, ACTION_LIST);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not deserialize triage actions", exception);
        }
    }
}
