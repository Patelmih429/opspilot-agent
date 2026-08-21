package dev.mihirpatel.opspilot.triage;

import dev.mihirpatel.opspilot.incident.CreateIncidentRequest;
import dev.mihirpatel.opspilot.incident.Priority;
import dev.mihirpatel.opspilot.incident.Severity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeuristicTriageAdvisorTest {

    private final HeuristicTriageAdvisor advisor = new HeuristicTriageAdvisor();

    @Test
    void escalatesSecurityOutageForHumanReview() {
        var result = advisor.analyze(new CreateIncidentRequest(
                "Authentication outage",
                "All users receive unauthorized errors after a token rotation.",
                "grafana",
                Severity.CRITICAL));

        assertThat(result.priority()).isEqualTo(Priority.P1);
        assertThat(result.category()).isEqualTo("security");
        assertThat(result.requiresHumanReview()).isTrue();
        assertThat(result.recommendedActions()).contains("Page the incident commander");
    }

    @Test
    void classifiesDatabaseWarningsWithoutOverEscalating() {
        var result = advisor.analyze(new CreateIncidentRequest(
                "Connection pool warning",
                "Postgres connection pool usage is intermittently high.",
                "prometheus",
                Severity.MEDIUM));

        assertThat(result.priority()).isEqualTo(Priority.P3);
        assertThat(result.category()).isEqualTo("database");
        assertThat(result.requiresHumanReview()).isFalse();
    }
}
