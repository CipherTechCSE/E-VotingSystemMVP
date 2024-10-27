package org.ciphertech.api_gateway.services.auth_service;

import org.ciphertech.api_gateway.dto.auth.LoginRequest;
import org.ciphertech.api_gateway.dto.auth.LogoutRequest;
import org.ciphertech.api_gateway.dto.auth.RegisterRequest;
import org.ciphertech.api_gateway.dto.auth.AuthResponse;
import org.ciphertech.api_gateway.services.auth_service.repositories.UserRepository;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;  // For password hashing

    private final UserRepository userRepository;

    @Autowired
    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

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
    public AuthResponse login(LoginRequest loginRequest) {

        String[] userCredentials = fetchPasswordHashForUser(loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            return null; // or handle the case where user is not found
        }
        if (userCredentials == null) {
            return null; // or handle the case where user is not found
        }
        String storedPasswordHash = userCredentials[0];
        String salt = userCredentials[1];

        // Check if the provided password matches the stored password hash
        if (passwordEncoder.matches(loginRequest.getPassword() + salt, storedPasswordHash)) {
            String username = loginRequest.getUsername();
            String role = user.getRole();
            String token= jwtUtil.generateToken(username, role);
            return new AuthResponse(token);
        } else {
            return null; }
    }

    // Updated method to register users
    public String register(RegisterRequest registerRequest) {
        // Check if the username already exists
        Optional<User> existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (existingUser.isPresent()) {
            return "Username already exists";
        }

        // Generate a salt and hash the user password before saving
        String salt = jwtUtil.generateSalt(); // Assuming JwtUtil has a method to generate a salt
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword() + salt);

        // Create a new user entity
        User newUser = new User(
                registerRequest.getUsername(),
                encodedPassword,
                "USER_ROLE",
                registerRequest.getDeviceFingerprint(),
                registerRequest.getEmail(),
                registerRequest.getPhoneNumber(),
                registerRequest.getFullName(),
                registerRequest.getAddress(),
                registerRequest.getNic(),
                salt
        );

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

    // Helper method to fetch stored password hash and salt for a given username
    private String[] fetchPasswordHashForUser(String username) {
        // Fetch the user by username from the repository
        Optional<User> userOptional = userRepository.findByUsername(username);

        // Check if the user exists and return the password hash
        // Return the stored password hash and salt
        // User not found, return null or handle as needed
        return userOptional.map(user -> new String[]{user.getPassword(), user.getSalt()}).orElse(null);
    }
}

