package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.dto.auth.AuthResponse;
import org.ciphertech.api_gateway.dto.auth.LogoutRequest;
import org.ciphertech.api_gateway.dto.auth.RegisterRequest;
import org.ciphertech.api_gateway.services.auth_service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ciphertech.api_gateway.dto.auth.LoginRequest;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("Register request received: " + registerRequest);
        return authService.register(registerRequest);
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@RequestBody RegisterRequest registerRequest) {

        if (!authService.validateAdminAccessToken(registerRequest.getAccessToken())) {
            return "Invalid token";
        }

        System.out.println("Register request received: " + registerRequest);
        return authService.registerAdmin(registerRequest);
    }

    @PostMapping("/logout")
    public String logout() {
        return authService.logout(null);
    }
}

