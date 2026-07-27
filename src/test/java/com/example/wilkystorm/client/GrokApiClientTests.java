package com.example.wilkystorm.client;

import com.example.wilkystorm.model.FredRogersQuote;
import com.example.wilkystorm.config.XaiApiProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GrokApiClientTests {

    @Test
    void returnsFallbackWhenApiKeyIsMissing() {
        GrokApiClient client = new GrokApiClient(new XaiApiProperties("", "", "https://api.x.ai/v1", "grok-4.5"));

        FredRogersQuote quote = client.getFredRogersQuote();

        assertThat(quote.quote()).isNotBlank();
        assertThat(quote.fallback()).isTrue();
    }

    @Test
    void xaiApiKeyIsRecognized() {
        XaiApiProperties properties = new XaiApiProperties("xai-primary", "", "https://api.x.ai/v1", "grok-4.5");

        assertThat(properties.apiKey()).isEqualTo("xai-primary");
        assertThat(properties.hasApiKey()).isTrue();
    }

    @Test
    void xaiApiKeyTakesPrecedenceOverGrokApiKey() {
        XaiApiProperties properties = new XaiApiProperties("xai-primary", "grok-secondary", "https://api.x.ai/v1", "grok-4.5");

        assertThat(properties.apiKey()).isEqualTo("xai-primary");
    }

    @Test
    void grokApiKeyWorksAsFallback() {
        XaiApiProperties properties = new XaiApiProperties("", "grok-secondary", "https://api.x.ai/v1", "grok-4.5");

        assertThat(properties.apiKey()).isEqualTo("grok-secondary");
        assertThat(properties.hasApiKey()).isTrue();
    }

    @Test
    void noApiKeyIsConfiguredWhenBothValuesAreBlank() {
        XaiApiProperties properties = new XaiApiProperties("", "", "https://api.x.ai/v1", "grok-4.5");

        assertThat(properties.apiKey()).isEmpty();
        assertThat(properties.hasApiKey()).isFalse();
    }
}
