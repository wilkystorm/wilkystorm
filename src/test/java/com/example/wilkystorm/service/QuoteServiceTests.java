package com.example.wilkystorm.service;

import com.example.wilkystorm.client.GrokApiClient;
import com.example.wilkystorm.model.FredRogersQuote;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuoteServiceTests {

    private static final FredRogersQuote FALLBACK_QUOTE = new FredRogersQuote(
            LocalDate.of(2026, 7, 28),
            "Anyone who does anything to help a child is a hero to me.",
            true
    );

    @Test
    void getTodayReturnsStoredQuoteWithoutCallingGrok() {
        GrokApiClient grokApiClient = mock(GrokApiClient.class);
        FredRogersQuote quote = new FredRogersQuote(LocalDate.of(2026, 7, 28), "Look for the helpers.", false);
        when(grokApiClient.fallbackQuote()).thenReturn(FALLBACK_QUOTE);
        when(grokApiClient.getFredRogersQuote()).thenReturn(quote);
        QuoteService quoteService = new QuoteService(grokApiClient);
        quoteService.refreshDailyQuote();

        FredRogersQuote result = quoteService.getToday();

        assertThat(result).isEqualTo(quote);
        verify(grokApiClient).getFredRogersQuote();
    }

    @Test
    void successfulRefreshReplacesStoredQuote() {
        GrokApiClient grokApiClient = mock(GrokApiClient.class);
        FredRogersQuote firstQuote = new FredRogersQuote(LocalDate.of(2026, 7, 28), "There are three ways to ultimate success.", false);
        FredRogersQuote secondQuote = new FredRogersQuote(LocalDate.of(2026, 7, 29), "Listening is where love begins.", false);
        when(grokApiClient.fallbackQuote()).thenReturn(FALLBACK_QUOTE);
        when(grokApiClient.getFredRogersQuote()).thenReturn(firstQuote, secondQuote);
        QuoteService quoteService = new QuoteService(grokApiClient);

        quoteService.refreshDailyQuote();
        quoteService.refreshDailyQuote();

        assertThat(quoteService.getToday()).isEqualTo(secondQuote);
    }

    @Test
    void fallbackOrFailedRefreshRetainsLastSuccessfulQuote() {
        GrokApiClient grokApiClient = mock(GrokApiClient.class);
        FredRogersQuote successfulQuote = new FredRogersQuote(LocalDate.of(2026, 7, 28), "Look for the helpers.", false);
        when(grokApiClient.fallbackQuote()).thenReturn(FALLBACK_QUOTE);
        when(grokApiClient.getFredRogersQuote())
                .thenReturn(successfulQuote, FALLBACK_QUOTE)
                .thenThrow(new IllegalStateException("xAI unavailable"));
        QuoteService quoteService = new QuoteService(grokApiClient);

        quoteService.refreshDailyQuote();
        quoteService.refreshDailyQuote();
        assertThat(quoteService.getToday()).isEqualTo(successfulQuote);

        quoteService.refreshDailyQuote();

        assertThat(quoteService.getToday()).isEqualTo(successfulQuote);
    }

    @Test
    void fallbackQuoteIsReturnedWhenNoSuccessfulQuoteExists() {
        GrokApiClient grokApiClient = mock(GrokApiClient.class);
        when(grokApiClient.fallbackQuote()).thenReturn(FALLBACK_QUOTE);
        QuoteService quoteService = new QuoteService(grokApiClient);

        FredRogersQuote result = quoteService.getToday();

        assertThat(result).isEqualTo(FALLBACK_QUOTE);
        verify(grokApiClient, never()).getFredRogersQuote();
    }
}
