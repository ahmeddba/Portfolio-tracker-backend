package com.example.Stocks.Controllers;

import com.example.Stocks.Models.Stock;
import com.example.Stocks.Services.StockServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockServiceImp stockService;

    public StockController(StockServiceImp stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock, @RequestHeader("Authorization") String token) {
        try {
            return stockService.addStock(stock, extractToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(
            @PathVariable Long id,
            @RequestBody Stock updatedStock,
            @RequestHeader("Authorization") String token
    ) {
        try {
            return stockService.updateStock(id, updatedStock, extractToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            return stockService.deleteStock(id, extractToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the stock");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStocks(@RequestHeader("Authorization") String token) {
        try {
            return stockService.getAllStocks(extractToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }
    }

    @GetMapping("/portfolio-metrics")
    public ResponseEntity<StockServiceImp.PortfolioMetrics> getPortfolioMetrics(@RequestHeader("Authorization") String token) {
        try {
            return stockService.calculatePortfolioMetrics(extractToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return an empty response if an error occurs
        }
    }


    /**
     * Helper method to extract the token from the Authorization header.
     */
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
