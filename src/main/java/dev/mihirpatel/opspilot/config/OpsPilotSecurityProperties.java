package dev.mihirpatel.opspilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("opspilot.security")
public record OpsPilotSecurityProperties(String apiKey) {
}
