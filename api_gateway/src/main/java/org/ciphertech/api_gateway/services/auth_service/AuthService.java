package org.ciphertech.api_gateway.services.auth_service;

import org.ciphertech.api_gateway.dto.auth.LoginRequest;
import org.ciphertech.api_gateway.dto.auth.LogoutRequest;
import org.ciphertech.api_gateway.dto.auth.RegisterRequest;
import org.ciphertech.api_gateway.dto.auth.*;
import org.ciphertech.api_gateway.services.auth_service.repositories.UserRepository;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;  // For password hashing

    @Autowired
    private UserRepository userRepository;

    // Example method to validate tokensLoginRequest
    public String validateToken(String token, String username) {
        // Validate the token and return user role or error message
        if (jwtUtil.validateToken(token, username)) {
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

    // Updated method to register users
    public String register(RegisterRequest registerRequest) {
        // Check if the username already exists
        Optional<User> existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (existingUser.isPresent()) {
            return "Username already exists";
        }

        // Encrypt the user password before saving
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create a new user entity
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setRole("USER_ROLE"); // Set the default role for the new user

        // Save the user information in the database
        userRepository.save(newUser);

        // Return a success message upon successful registration
        return "Registration successful";
    }

    // Example method to logout users (this can involve token blacklisting or other mechanisms)
    public String logout(LogoutRequest logoutRequest) {
        // If using token blacklisting, you can mark the token as invalid (e.g., storing it in a blacklist)
        // blacklistToken(logoutRequest.getToken());

        return "Logout successful";
    }

    // Helper method to fetch stored password hash
    private String fetchPasswordHashForUser(String username) {
        // Fetch the user by username from the repository
        Optional<User> userOptional = userRepository.findByUsername(username);

        // Check if the user exists and return the password hash
        if (userOptional.isPresent()) {
            return userOptional.get().getPassword(); // Return the stored password hash
        } else {
            return null; // User not found, return null or handle as needed
        }
    }
}

