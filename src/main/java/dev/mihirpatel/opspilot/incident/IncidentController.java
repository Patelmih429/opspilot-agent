package dev.mihirpatel.opspilot.incident;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResponse create(@Valid @RequestBody CreateIncidentRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public IncidentResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<IncidentResponse> list(@RequestParam(defaultValue = "25") int limit) {
        return service.list(limit);
    }

    @PostMapping("/{id}/triage")
    public IncidentResponse triage(@PathVariable UUID id) {
        return service.triage(id);
    }
}
