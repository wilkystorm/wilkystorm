package com.example.wilkystorm.service;

import com.example.wilkystorm.client.GrokApiClient;
import com.example.wilkystorm.model.FredRogersQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteService.class);

    private final GrokApiClient grokApiClient;
    private volatile FredRogersQuote currentQuote;

    public QuoteService(GrokApiClient grokApiClient) {
        this.grokApiClient = grokApiClient;
        this.currentQuote = grokApiClient.fallbackQuote();
    }

    public FredRogersQuote getToday() {
        return currentQuote;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialQuote() {
        refreshDailyQuote();
    }

    @Scheduled(cron = "0 0 6 * * *", zone = "America/New_York")
    public void refreshDailyQuote() {
        try {
            FredRogersQuote quote = grokApiClient.getFredRogersQuote();
            if (!quote.fallback()) {
                currentQuote = quote;
                log.info("Loaded Fred Rogers quote for {}", quote.date());
                return;
            }

            if (currentQuote == null || currentQuote.fallback()) {
                currentQuote = quote;
                log.info("Using fallback Fred Rogers quote for {}", quote.date());
            } else {
                log.warn("Keeping last successful Fred Rogers quote after fallback response.");
            }
        } catch (RuntimeException ex) {
            if (currentQuote == null) {
                currentQuote = grokApiClient.fallbackQuote();
            }
            log.warn("Keeping current Fred Rogers quote after refresh failure. error={}", ex.getClass().getSimpleName());
        }
    }
}
