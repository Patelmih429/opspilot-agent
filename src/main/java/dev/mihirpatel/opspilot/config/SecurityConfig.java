package dev.mihirpatel.opspilot.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(OpsPilotSecurityProperties.class)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OpsPilotSecurityProperties properties) throws Exception {
        ApiKeyAuthenticationFilter apiKeyFilter = new ApiKeyAuthenticationFilter(properties.apiKey());

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/health/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Valid X-API-Key required")))
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
