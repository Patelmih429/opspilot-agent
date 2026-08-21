package dev.mihirpatel.opspilot.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIncidentRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotBlank @Size(max = 100) String source,
        @NotNull Severity reportedSeverity) {
}
