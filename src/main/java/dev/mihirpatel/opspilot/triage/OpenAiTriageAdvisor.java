package dev.mihirpatel.opspilot.triage;

import dev.mihirpatel.opspilot.incident.CreateIncidentRequest;
import dev.mihirpatel.opspilot.incident.TriageRecommendation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("ai")
public class OpenAiTriageAdvisor implements TriageAdvisor {

    private final ChatClient chatClient;

    public OpenAiTriageAdvisor(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        You are an enterprise incident triage agent. Analyze only the supplied incident.
                        Choose priority P1, P2, P3, or P4. Keep actions concrete, reversible, and safe.
                        Flag P1/P2 and uncertain security incidents for human review. Never claim an action was executed.
                        """)
                .build();
    }

    @Override
    public TriageRecommendation analyze(CreateIncidentRequest incident) {
        AiRecommendation result = chatClient.prompt()
                .user("""
                        Title: %s
                        Description: %s
                        Source: %s
                        Reported severity: %s
                        """.formatted(
                        incident.title(),
                        incident.description(),
                        incident.source(),
                        incident.reportedSeverity()))
                .call()
                .entity(AiRecommendation.class, spec -> spec.validateSchema());

        return new TriageRecommendation(
                result.priority(),
                result.category(),
                result.summary(),
                result.recommendedActions(),
                result.requiresHumanReview(),
                result.confidence(),
                "spring-ai-openai");
    }
}
