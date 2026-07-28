package com.example.wilkystorm.controller;

import com.example.wilkystorm.model.FredRogersQuote;
import com.example.wilkystorm.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WilkystormController.class)
class WilkystormControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuoteService quoteService;

    @Test
    void apiQuoteReturnsQuoteJson() throws Exception {
        when(quoteService.getToday()).thenReturn(new FredRogersQuote(LocalDate.of(2026, 7, 27), "Look for the helpers.", false));

        mockMvc.perform(get("/api/quote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is("2026-07-27")))
                .andExpect(jsonPath("$.quote", is("Look for the helpers.")))
                .andExpect(jsonPath("$.fallback", is(false)));
    }
}
