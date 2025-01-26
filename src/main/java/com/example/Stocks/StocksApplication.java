package com.example.Stocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.Stocks")
public class StocksApplication {

	public static void main(String[] args) {
		SpringApplication.run(StocksApplication.class, args);
	}

}
