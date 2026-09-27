package com.readMe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppUrlProperties {
    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }
}