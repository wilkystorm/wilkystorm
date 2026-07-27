package com.example.wilkystorm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class XaiApiProperties {

    private final String xaiApiKey;
    private final String grokApiKey;
    private final String apiUrl;
    private final String model;

    public XaiApiProperties(
            @Value("${xai.api.key:}") String xaiApiKey,
            @Value("${grok.api.key:}") String grokApiKey,
            @Value("${xai.api.url:https://api.x.ai/v1}") String apiUrl,
            @Value("${xai.model:grok-4.5}") String model
    ) {
        this.xaiApiKey = xaiApiKey;
        this.grokApiKey = grokApiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public String apiKey() {
        if (hasText(xaiApiKey)) {
            return xaiApiKey;
        }
        if (hasText(grokApiKey)) {
            return grokApiKey;
        }
        return "";
    }

    public String apiUrl() {
        return apiUrl;
    }

    public String model() {
        return model;
    }

    public boolean hasApiKey() {
        return hasText(apiKey());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
