package com.example.wilkystorm.model;

import java.time.LocalDate;

public record FredRogersQuote(
        LocalDate date,
        String quote,
        boolean fallback
) {
}
