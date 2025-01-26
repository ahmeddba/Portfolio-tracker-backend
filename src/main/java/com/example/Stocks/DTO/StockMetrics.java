package com.example.Stocks.DTO;

import java.math.BigDecimal;

public class StockMetrics {
    private String ticker;
    private BigDecimal buyPrice;
    private BigDecimal currentPrice;
    private BigDecimal profitLoss;

    public StockMetrics(String ticker, BigDecimal buyPrice, BigDecimal currentPrice, BigDecimal profitLoss) {
        this.ticker = ticker;
        this.buyPrice = buyPrice;
        this.currentPrice = currentPrice;
        this.profitLoss = profitLoss;
    }

    // Getters and setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
}
