package com.example.Stocks.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class FinnhubService {
    private final WebClient webClient;
    private static final String BASE_URL = "https://finnhub.io/api/v1";
    private static final String API_KEY = "cub0hohr01qof06jjaugcub0hohr01qof06jjav0"; // Replace with your Finnhub API key

    public FinnhubService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<BigDecimal> getCurrentPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractPriceFromResponse);
    }

    private BigDecimal extractPriceFromResponse(String response) {
        // Parse the JSON response to extract the current stock price
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            // Finnhub API returns the current price in the "c" field
            String price = root.path("c").asText();
            return new BigDecimal(price);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Finnhub response: " + response, e);
        }
    }
}
