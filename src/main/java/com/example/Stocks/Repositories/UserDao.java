package com.example.Stocks.Repositories;

import com.example.Stocks.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

}
