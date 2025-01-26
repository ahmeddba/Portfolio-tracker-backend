package com.example.Stocks.Services;

import com.example.Stocks.Models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;


public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long idUser;

    private String firstName;
    private String lastName;

    private String email;
    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;


    ////
    public UserDetailsImpl(Long idUser, String firstName ,String lastName, String email, String password) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    ////
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getIdUser(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
