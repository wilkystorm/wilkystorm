package com.example.wilkystorm.controller;

import com.example.wilkystorm.model.FredRogersQuote;
import com.example.wilkystorm.service.QuoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;

@Controller
public class WilkystormController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private final QuoteService quoteService;

    public WilkystormController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/")
    public String home(Model model) {
        FredRogersQuote quote = quoteService.getToday();
        model.addAttribute("date", quote.date().format(DATE_FORMATTER));
        model.addAttribute("quote", quote.quote());
        model.addAttribute("fallback", quote.fallback());
        return "index";
    }

    @GetMapping("/api/quote")
    @ResponseBody
    public FredRogersQuote apiQuote() {
        return quoteService.getToday();
    }

    @PostMapping("/api/quote/refresh")
    @ResponseBody
    public FredRogersQuote refreshQuote() {
        quoteService.clearCache();
        return quoteService.getToday();
    }
}
