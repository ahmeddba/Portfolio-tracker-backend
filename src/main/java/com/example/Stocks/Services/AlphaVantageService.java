package com.example.Stocks.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class AlphaVantageService {

    private final WebClient webClient;

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private static final String API_KEY = "X5H68CZJCX7FT2MM"; // Replace with your Alpha Vantage API key

    public AlphaVantageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<BigDecimal> getCurrentPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> extractPriceFromResponse(response));
    }

    private BigDecimal extractPriceFromResponse(String response) {
        try {
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from Alpha Vantage");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Check for rate-limiting or errors
            if (root.has("Note")) {
                throw new RuntimeException("API rate limit reached: " + root.path("Note").asText());
            }
            if (root.has("Error Message")) {
                throw new RuntimeException("Error from Alpha Vantage: " + root.path("Error Message").asText());
            }

            // Extract the price
            JsonNode globalQuote = root.path("Global Quote");
            if (globalQuote.isMissingNode()) {
                throw new RuntimeException("Missing 'Global Quote' node in Alpha Vantage response");
            }
            String price = globalQuote.path("05. price").asText();
            if (price.isEmpty()) {
                throw new RuntimeException("Missing '05. price' in Alpha Vantage response");
            }

            return new BigDecimal(price);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Alpha Vantage response: " + response, e);
        }
    }

}
