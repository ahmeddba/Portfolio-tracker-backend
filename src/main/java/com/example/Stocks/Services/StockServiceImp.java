package com.example.Stocks.Services;

import com.example.Stocks.DTO.StockMetrics;
import com.example.Stocks.Models.Stock;
import com.example.Stocks.Models.User;
import com.example.Stocks.Repositories.StockDao;
import com.example.Stocks.Repositories.UserDao;
import com.example.Stocks.secConfig.Jwt.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockServiceImp implements IStock {

    private final StockDao stockRepository;
    private final UserDao userRepository;
    private final JwtUtils tokenUtils;

    public StockServiceImp(StockDao stockRepository, UserDao userRepository, JwtUtils tokenUtils) {
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
    }

    private Optional<User> getAuthenticatedUser(String token) {
        String userEmail = tokenUtils.getUserNameFromJwtToken(token);
        if (userEmail == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public ResponseEntity<Stock> addStock(Stock stock, String token) {
        Optional<User> userOptional = getAuthenticatedUser(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        User user = userOptional.get();
        stock.setUser(user);
        Stock savedStock = stockRepository.save(stock);
        return ResponseEntity.ok(savedStock);
    }

    @Override
    public ResponseEntity<Stock> updateStock(Long id, Stock updatedStock, String token) {
        Optional<User> userOptional = getAuthenticatedUser(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        User user = userOptional.get();
        Optional<Stock> existingStock = stockRepository.findById(id);
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            // Ensure the stock belongs to the authenticated user
            if (!stock.getUser().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(403).body(null); // Forbidden
            }

            stock.setTicker(updatedStock.getTicker());
            stock.setName(updatedStock.getName());
            stock.setBuyPrice(updatedStock.getBuyPrice());
            stock.setQuantity(updatedStock.getQuantity());
            Stock savedStock = stockRepository.save(stock);
            return ResponseEntity.ok(savedStock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> deleteStock(Long id, String token) {
        Optional<User> userOptional = getAuthenticatedUser(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userOptional.get();
        Optional<Stock> stockOptional = stockRepository.findById(id);
        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            // Ensure the stock belongs to the authenticated user
            if (!stock.getUser().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            stockRepository.deleteById(id);
            return ResponseEntity.ok("Stock deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<Stock>> getAllStocks(String token) {
        Optional<User> userOptional = getAuthenticatedUser(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        User user = userOptional.get();
        List<Stock> userStocks = stockRepository.findByUser(user);
        return ResponseEntity.ok(userStocks);
    }

    // Portfolio Metrics

    public ResponseEntity<PortfolioMetrics> calculatePortfolioMetrics(String token) {
        Optional<User> userOptional = getAuthenticatedUser(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        User user = userOptional.get();
        List<Stock> userStocks = stockRepository.findByUser(user);

        if (userStocks.isEmpty()) {
            return ResponseEntity.ok(new PortfolioMetrics(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new ArrayList<>(), null, new HashMap<>()
            ));
        }

        // Randomly pick up to 5 stocks
        Collections.shuffle(userStocks);
        userStocks = userStocks.stream().limit(5).collect(Collectors.toList());

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalProfitLoss = BigDecimal.ZERO;
        BigDecimal totalBuyPrice = BigDecimal.ZERO;
        int validStockCount = 0;
        List<StockMetrics> stockMetricsList = new ArrayList<>();
        Map<String, BigDecimal> portfolioDistribution = new HashMap<>();
        StockMetrics topPerformingStock = null;
        FinnhubService finnhubService = new FinnhubService(WebClient.builder());

        for (Stock stock : userStocks) {
            try {
                // Fetch current price using FinnhubService
                BigDecimal currentPrice = finnhubService.getCurrentPrice(stock.getTicker()).block();
                if (currentPrice == null) {
                    throw new RuntimeException("Current price not available for stock: " + stock.getTicker());
                }

                // Ensure buyPrice is not null
                if (stock.getBuyPrice() == null) {
                    return ResponseEntity.status(400).body(null); // Bad request if buy price is missing
                }

                BigDecimal stockValue = currentPrice.multiply(BigDecimal.ONE); // Assume quantity = 1
                BigDecimal stockProfitLoss = currentPrice.subtract(stock.getBuyPrice());

                // Update total portfolio metrics
                totalValue = totalValue.add(stockValue);
                totalProfitLoss = totalProfitLoss.add(stockProfitLoss);

                // Add to stock metrics list
                StockMetrics stockMetrics = new StockMetrics(stock.getTicker(), stock.getBuyPrice(), currentPrice, stockProfitLoss);
                stockMetricsList.add(stockMetrics);

                // Track top-performing stock
                if (topPerformingStock == null || stockProfitLoss.compareTo(topPerformingStock.getProfitLoss()) > 0) {
                    topPerformingStock = stockMetrics;
                }

                // Sum the buy price for average calculation
                totalBuyPrice = totalBuyPrice.add(stock.getBuyPrice());
                validStockCount++;
            } catch (RuntimeException e) {
                // Log error details for debugging
                System.err.println("Error calculating portfolio metrics for stock " + stock.getTicker() + ": " + e.getMessage());
            }
        }

        // Calculate average buy price
        BigDecimal averageBuyPrice = validStockCount > 0
                ? totalBuyPrice.divide(BigDecimal.valueOf(validStockCount), MathContext.DECIMAL64)
                : BigDecimal.ZERO;

        // Calculate portfolio distribution percentages
        for (StockMetrics stockMetric : stockMetricsList) {
            BigDecimal stockValue = stockMetric.getCurrentPrice().multiply(BigDecimal.ONE); // Assume quantity = 1
            BigDecimal percentage = stockValue.divide(totalValue, MathContext.DECIMAL64).multiply(BigDecimal.valueOf(100));
            portfolioDistribution.put(stockMetric.getTicker(), percentage);
        }

        // Calculate diversification (basic implementation as a placeholder)
        BigDecimal diversification = BigDecimal.valueOf(portfolioDistribution.size());

        // Create and return PortfolioMetrics with average buy price
        PortfolioMetrics metrics = new PortfolioMetrics(
                totalValue,
                totalProfitLoss,
                averageBuyPrice,
                diversification,
                stockMetricsList,
                topPerformingStock,
                portfolioDistribution
        );

        return ResponseEntity.ok(metrics);
    }



    public class PortfolioMetrics {
        private BigDecimal totalValue;
        private BigDecimal totalProfitLoss;
        private BigDecimal averageBuyPrice;
        private BigDecimal diversification;
        private List<StockMetrics> stockMetricsList;
        private StockMetrics topPerformingStock;
        private Map<String, BigDecimal> portfolioDistribution;

        // Default Constructor
        public PortfolioMetrics() {
        }

        // Parameterized Constructor
        public PortfolioMetrics(
                BigDecimal totalValue,
                BigDecimal totalProfitLoss,
                BigDecimal averageBuyPrice,
                BigDecimal diversification,
                List<StockMetrics> stockMetricsList,
                StockMetrics topPerformingStock,
                Map<String, BigDecimal> portfolioDistribution
        ) {
            this.totalValue = totalValue;
            this.totalProfitLoss = totalProfitLoss;
            this.averageBuyPrice = averageBuyPrice;
            this.diversification = diversification;
            this.stockMetricsList = stockMetricsList;
            this.topPerformingStock = topPerformingStock;
            this.portfolioDistribution = portfolioDistribution;
        }

        // Getters and Setters
        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        public BigDecimal getTotalProfitLoss() {
            return totalProfitLoss;
        }

        public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
            this.totalProfitLoss = totalProfitLoss;
        }

        public BigDecimal getAverageBuyPrice() {
            return averageBuyPrice;
        }

        public void setAverageBuyPrice(BigDecimal averageBuyPrice) {
            this.averageBuyPrice = averageBuyPrice;
        }

        public BigDecimal getDiversification() {
            return diversification;
        }

        public void setDiversification(BigDecimal diversification) {
            this.diversification = diversification;
        }

        public List<StockMetrics> getStockMetricsList() {
            return stockMetricsList;
        }

        public void setStockMetricsList(List<StockMetrics> stockMetricsList) {
            this.stockMetricsList = stockMetricsList;
        }

        public StockMetrics getTopPerformingStock() {
            return topPerformingStock;
        }

        public void setTopPerformingStock(StockMetrics topPerformingStock) {
            this.topPerformingStock = topPerformingStock;
        }

        public Map<String, BigDecimal> getPortfolioDistribution() {
            return portfolioDistribution;
        }

        public void setPortfolioDistribution(Map<String, BigDecimal> portfolioDistribution) {
            this.portfolioDistribution = portfolioDistribution;
        }

        // Optional: toString() method for debugging
        @Override
        public String toString() {
            return "PortfolioMetrics{" +
                    "totalValue=" + totalValue +
                    ", totalProfitLoss=" + totalProfitLoss +
                    ", averageBuyPrice=" + averageBuyPrice +
                    ", diversification=" + diversification +
                    ", stockMetricsList=" + stockMetricsList +
                    ", topPerformingStock=" + topPerformingStock +
                    ", portfolioDistribution=" + portfolioDistribution +
                    '}';
        }
    }

}
