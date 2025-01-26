package com.example.Stocks.Repositories;

import com.example.Stocks.Models.Stock;
import com.example.Stocks.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockDao extends JpaRepository<Stock, Long> {
    Optional<Stock> findByTicker(String ticker);
    List<Stock> findByUser(User user);

}
