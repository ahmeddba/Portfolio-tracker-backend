package com.example.Stocks.Controllers;

import com.example.Stocks.Models.User;
import com.example.Stocks.Payload.Request.LoginRequest;
import com.example.Stocks.Payload.Request.SignUpRequest;
import com.example.Stocks.Payload.Response.JwtResponse;
import com.example.Stocks.Payload.Response.MessageResponse;
import com.example.Stocks.Repositories.UserDao;
import com.example.Stocks.Services.UserDetailsImpl;
import com.example.Stocks.secConfig.Jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDao userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        String token = UUID.randomUUID().toString();

        User user = new User(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            String jwt = jwtUtils.generateJwtToken(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getIdUser(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(401)
                    .body(new MessageResponse("Error: Invalid email or password!"));
        }
    }
}