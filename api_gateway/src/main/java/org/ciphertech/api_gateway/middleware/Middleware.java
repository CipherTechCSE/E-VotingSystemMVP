package org.ciphertech.api_gateway.middleware;

import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.ciphertech.api_gateway.services.auth_service.repositories.UserRepository;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.Set;

@Order(1)
@Component
public class Middleware implements Filter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private static final Set<String> PUBLIC_ROUTES = Set.of(
            "/public/login",
            "/public/register",
            "/public/health-check",
            "/api/auth/register",
            "/api/auth/login"
    );

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

        String requestURI = httpRequest.getRequestURI();

        // Allow public routes without any authentication
        if (PUBLIC_ROUTES.contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"error\": \"Unauthorized: Missing or invalid token\"}");
            return;
        }

        token = token.substring(7);  // Remove "Bearer " from token
        String username = jwtUtil.extractUsername(token);

        if (username != null && jwtUtil.validateToken(token, username)) {
            // Load user details (optional step)
            User userDetails = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Set the authentication in the security context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"error\": \"Unauthorized: Invalid token\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    // Optional init and destroy methods can be removed if not used
}
