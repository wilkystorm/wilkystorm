package com.example.wilkystorm.client;

import com.example.wilkystorm.model.FredRogersQuote;
import com.example.wilkystorm.config.XaiApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class GrokApiClient {

    private static final Logger log = LoggerFactory.getLogger(GrokApiClient.class);
    private static final String FALLBACK_QUOTE = "Anyone who does anything to help a child is a hero to me.";
    private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GrokApiClient(XaiApiProperties properties) {
        this.apiKey = properties.apiKey();
        this.model = properties.model();
        this.restClient = RestClient.builder()
                .baseUrl(properties.apiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    public FredRogersQuote getFredRogersQuote() {
        if (apiKey == null || apiKey.isBlank()) {
            log.debug("XAI_API_KEY and GROK_API_KEY are not configured; returning fallback quote.");
            return fallbackQuote();
        }

        String systemPrompt = """
                You return clean, concise quote text for display in a personal website.
                Return exactly one meaningful and uplifting quote from Fred Rogers.
                Return only the quote text. Do not include an introduction, explanation, quotation marks, attribution, markdown, or additional commentary.
                """;

        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", "Provide the Fred Rogers quote for today's Wilkystorm page.")
                ),
                "temperature", 0.4,
                "max_tokens", 120
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(RESPONSE_TYPE);

            String content = extractContent(response);
            if (content.isBlank()) {
                log.warn("Grok returned an empty quote; returning fallback quote.");
                return fallbackQuote();
            }
            return new FredRogersQuote(LocalDate.now(), cleanQuote(content), false);
        } catch (RestClientResponseException ex) {
            log.warn("Grok quote request failed; returning fallback quote. status={} error={}",
                    ex.getStatusCode().value(), ex.getClass().getSimpleName());
            return fallbackQuote();
        } catch (RuntimeException ex) {
            log.warn("Grok quote request failed; returning fallback quote. error={}", ex.getClass().getSimpleName());
            return fallbackQuote();
        }
    }

    private String extractContent(Map<String, Object> response) {
        if (response == null) {
            return "";
        }
        Object choicesValue = response.get("choices");
        if (!(choicesValue instanceof List<?> choices) || choices.isEmpty()) {
            return "";
        }
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?> choice)) {
            return "";
        }
        Object messageValue = choice.get("message");
        if (!(messageValue instanceof Map<?, ?> message)) {
            return "";
        }
        Object content = message.get("content");
        return content instanceof String value ? value : "";
    }

    public FredRogersQuote fallbackQuote() {
        return new FredRogersQuote(LocalDate.now(), FALLBACK_QUOTE, true);
    }

    private String cleanQuote(String content) {
        String cleaned = content.trim();
        while (cleaned.startsWith("\"") || cleaned.startsWith("'")) {
            cleaned = cleaned.substring(1).trim();
        }
        while (cleaned.endsWith("\"") || cleaned.endsWith("'")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }
        return cleaned;
    }
}
