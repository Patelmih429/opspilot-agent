# Architecture decisions

## Why n8n orchestrates and Java owns business state

n8n provides visible workflow composition and connector reach. The Spring Boot service owns validation, persistence, authorization, triage policy, and model integration. This keeps durable business rules testable in Java while letting operators extend delivery steps without recompiling the service.

## Safe model fallback

The default profile uses a deterministic engine with explicit rules. The `ai` profile replaces it with Spring AI. This profile boundary prevents a missing model key or provider outage from making the repository impossible to evaluate locally.

## Human control

Recommendations contain a `requiresHumanReview` flag. The agent does not call remediation tools, and its model instruction prohibits claiming that actions were performed. A future remediation workflow should require explicit approval and an auditable allowlist.

## Data flow

1. n8n receives a validated JSON-shaped alert payload.
2. The API stores the original incident with `RECEIVED` status.
3. n8n asks the API to triage that incident.
4. The selected advisor creates a typed recommendation.
5. The API stores the recommendation and marks the incident `TRIAGED`.
6. n8n returns the enriched incident to the caller.
