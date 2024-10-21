package org.ciphertech.api_gateway.middleware;

import org.ciphertech.api_gateway.services.auth_service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Component
public class Middleware implements Filter {

    private final AuthService authService;

    @Autowired
    public Middleware(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract the token from the Authorization header
        String token = httpRequest.getHeader("Authorization");

        // Validate the token using AuthService
        if (token == null || !authService.validateToken(token)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized");
            return;
        }

        // If token is valid, continue processing the request
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional initialization logic if needed
    }

    @Override
    public void destroy() {
        // Optional cleanup logic if needed
    }
}