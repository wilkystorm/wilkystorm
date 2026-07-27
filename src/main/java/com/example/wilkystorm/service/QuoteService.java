package com.example.wilkystorm.service;

import com.example.wilkystorm.client.GrokApiClient;
import com.example.wilkystorm.model.FredRogersQuote;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final GrokApiClient grokApiClient;

    public QuoteService(GrokApiClient grokApiClient) {
        this.grokApiClient = grokApiClient;
    }

    @Cacheable(value = "fredRogersQuote", key = "T(java.time.LocalDate).now().toString()")
    public FredRogersQuote getToday() {
        return grokApiClient.getFredRogersQuote();
    }

    @CacheEvict(value = "fredRogersQuote", allEntries = true)
    public void clearCache() {
    }
}
