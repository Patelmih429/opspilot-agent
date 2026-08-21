package dev.mihirpatel.opspilot.incident;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectsApiAndTriagesPersistedIncident() throws Exception {
        mockMvc.perform(get("/api/v1/incidents"))
                .andExpect(status().isUnauthorized());

        String created = mockMvc.perform(post("/api/v1/incidents")
                        .header("X-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Authentication outage",
                                  "description": "All users receive unauthorized errors after token rotation.",
                                  "source": "grafana",
                                  "reportedSeverity": "CRITICAL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = tools.jackson.databind.json.JsonMapper.builder().build()
                .readTree(created)
                .get("id")
                .asString();

        mockMvc.perform(post("/api/v1/incidents/{id}/triage", id)
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TRIAGED"))
                .andExpect(jsonPath("$.priority").value("P1"))
                .andExpect(jsonPath("$.category").value("security"))
                .andExpect(jsonPath("$.requiresHumanReview").value(true))
                .andExpect(jsonPath("$.triageEngine").value("heuristic-v1"));
    }
}
