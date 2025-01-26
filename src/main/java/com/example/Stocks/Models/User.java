package com.example.Stocks.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idUser;

    private String firstName ;
    private String lastName ;
    private String email;
    private String password;

    //relation avec stock entity
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Stock> stocks = new HashSet<>();

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
