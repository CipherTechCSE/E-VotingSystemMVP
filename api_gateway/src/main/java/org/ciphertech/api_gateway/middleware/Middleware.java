package org.ciphertech.api_gateway.middleware;

import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.ciphertech.api_gateway.services.auth_service.repositories.UserRepository;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Component
public class Middleware implements Filter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public Middleware(JwtUtil jwtUtil , UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // Remove "Bearer " prefix
            String username = jwtUtil.extractUsername(token);


            if (username != null && jwtUtil.validateToken(token, username)) {
                User userDetails = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response); // Continue with the other filters
    }

    private void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
