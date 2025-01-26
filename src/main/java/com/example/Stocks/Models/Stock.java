package com.example.Stocks.Models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data // This includes @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long ids;
    private String name;
    private String ticker;
    private int quantity;
    private BigDecimal buyPrice;

    // I have an issue in lombok dependency so i'll implement getters and setters manually

    // Getter ids
    public Long getIds() {
        return ids;
    }


    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for ticker
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for buyPrice
    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "idUser")
    private User user;
}
