package crypto.investments.api.controller;

import crypto.investments.api.it.AbstractIntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CurrenciesControllerV1IT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testGetCurrencies() {
        mockMvc.perform(get("/api/v1/currency")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$[0].symbol").value("ETH"))
            .andExpect(jsonPath("$[1].symbol").value("XRP"))
            .andExpect(jsonPath("$[2].symbol").value("DOGE"))
            .andExpect(jsonPath("$[3].symbol").value("LTC"))
            .andExpect(jsonPath("$[4].symbol").value("BTC"));
    }

    @Test
    @SneakyThrows
    void testGetTopCurrency() {
        mockMvc.perform(get("/api/v1/currency/top?date=2022-01-01")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.oldestPrice").value(46813.2100))
            .andExpect(jsonPath("$.newestPrice").value(47722.6600))
            .andExpect(jsonPath("$.minPrice").value(46813.2100))
            .andExpect(jsonPath("$.maxPrice").value(47722.6600));
    }

    @Test
    @SneakyThrows
    void testGetCurrency() {
        mockMvc.perform(get("/api/v1/currency/BTC")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.oldestPrice").value(46813.2100))
            .andExpect(jsonPath("$.newestPrice").value(38415.7900))
            .andExpect(jsonPath("$.minPrice").value(33276.5900))
            .andExpect(jsonPath("$.maxPrice").value(47722.6600));
    }
}