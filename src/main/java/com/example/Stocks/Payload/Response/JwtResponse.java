package com.example.Stocks.Payload.Response;



import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String type = "Bearer";
    private Long idUser;
    private String firstName;
    private String lastName;
    private String email;

    public JwtResponse(String token, String type, Long idUser, String firstName, String lastName, String email) {
        this.token = token;
        this.type = type;
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    public JwtResponse(String token, Long idUser, String firstName, String lastName, String email) {
        this.token = token;
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getIdUser() {
        return idUser;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }


    public JwtResponse(String token) {
        this.token = token;
    }
}
