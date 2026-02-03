package com.carrental.client;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditClient {

    private final RestTemplate restTemplate;
    // Using HTTP port from launchSettings.json
    // Use Env Var for Docker compatibility, default to local for dev
    @org.springframework.beans.factory.annotation.Value("${audit.service.url:http://audit-service:8080/api/audits}")
    private String auditServiceUrl;

    @Async
    public void logActivity(String action, String userEmail, String details) {
        try {
            log.info("Sending audit log: {} - {}", action, userEmail);
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("userEmail", userEmail);
            payload.put("details", details);

            restTemplate.postForEntity(auditServiceUrl, payload, Void.class);
        } catch (Exception e) {
            log.error("Failed to log audit activity", e);
        }
    }
}
