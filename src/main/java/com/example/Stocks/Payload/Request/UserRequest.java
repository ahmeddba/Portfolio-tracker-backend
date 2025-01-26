package com.example.Stocks.Payload.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "first name is mandatory")
    private String firstName;
    @NotBlank(message = "last name is mandatory")
    private String lastName;
    @NotBlank(message = "email is mandatory")
    private String email;
    private String password;

}
