package org.ciphertech.api_gateway.services.auth_service;

import org.ciphertech.api_gateway.dto.auth.LoginRequest;
import org.ciphertech.api_gateway.dto.auth.LogoutRequest;
import org.ciphertech.api_gateway.dto.auth.RegisterRequest;
import org.ciphertech.api_gateway.dto.auth.*;
import org.ciphertech.api_gateway.services.auth_service.repositories.UserRepository;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;  // For password hashing

    @Autowired
    private UserRepository userRepository;

    // Example method to validate tokensLoginRequest
    public String validateToken(String token , String username) {
        // Validate the token and return user role or error message
        if (jwtUtil.validateToken(token , username)) {
            return jwtUtil.extractRole(token);
        } else {
            return "Invalid token";
        }
    }

    // Example method to login users (this can include more logic, like fetching user from a database)
    public String login(LoginRequest loginRequest) {
        // For demonstration, let's assume loginRequest contains username and password

        // In a real-world scenario, you would fetch the user details from a database
        String storedPasswordHash = fetchPasswordHashForUser(loginRequest.getUsername());

        // Check if the provided password matches the stored password hash
        if (passwordEncoder.matches(loginRequest.getPassword(), storedPasswordHash)) {
            // Generate and return JWT token upon successful login
            return jwtUtil.generateToken(loginRequest.getUsername(), "USER_ROLE");
        } else {
            return "Invalid credentials";
        }
    }

    // Example method to register users (this can include storing user data in the database)
    public String register(RegisterRequest registerRequest) {
        // Encrypt the user password before saving
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Store the user information in the database (not implemented here)
        // saveUser(registerRequest.getUsername(), encodedPassword);

        // Return a success message upon successful registration
        return "Registration successful";
    }

    // Example method to logout users (this can involve token blacklisting or other mechanisms)
    public String logout(LogoutRequest logoutRequest) {
        // If using token blacklisting, you can mark the token as invalid (e.g., storing it in a blacklist)
        // blacklistToken(logoutRequest.getToken());

        return "Logout successful";
    }

    // Helper method to fetch stored password hash (this is just an example; replace with DB logic)
    private String fetchPasswordHashForUser(String username) {
        // For demo purposes, let's assume all users have this password hash (hashed value for "password")
        return "$2a$10$D4J2KlE.1uKMhJ6UOWswY.2IjjsNkOT9/7Ykls4BpKmSlt8RnS3eS";
    }
}
