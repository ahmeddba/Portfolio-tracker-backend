package com.example.Stocks.Services;

import com.example.Stocks.Models.Stock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IStock {
    public ResponseEntity<Stock> addStock(Stock stock , String token);

    public ResponseEntity<Stock> updateStock(Long id, Stock updatedStock , String token);

    public ResponseEntity<?> deleteStock(Long id , String token);

    public ResponseEntity<List<Stock>> getAllStocks(String token);

    ResponseEntity<StockServiceImp.PortfolioMetrics> calculatePortfolioMetrics(String token);
}
