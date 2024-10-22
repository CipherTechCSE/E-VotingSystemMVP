package org.ciphertech.api_gateway.middleware;

import org.ciphertech.api_gateway.services.auth_service.AuthService;
import org.ciphertech.api_gateway.services.auth_service.utils.JwtUtil;
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
import java.util.Set;

@Order(1)
@Component
public class Middleware implements Filter {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // List of public routes that don't require authorization
    private static final Set<String> PUBLIC_ROUTES = Set.of("/public/login", "/public/register", "/public/health-check");

    @Autowired
    public Middleware(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract the request URI
        String requestURI = httpRequest.getRequestURI();

        // Allow public routes without any authentication
        if (PUBLIC_ROUTES.contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract the token from the Authorization header
        String token = httpRequest.getHeader("Authorization");

        // If no token is provided, return unauthorized
        if (token == null || !token.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized: Missing or invalid token");
            return;
        }

        // Remove "Bearer " prefix from the token
        token = token.substring(7);

        // Extract username from the token
        String username = jwtUtil.extractUsername(token);

        // Validate token with the extracted username
        if (!jwtUtil.validateToken(token, username)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized: Invalid token");
            return;
        }

        // Extract user role after token validation
        String role = jwtUtil.extractRole(token);

        // Define admin and voter specific routes
        Set<String> adminRoutes = Set.of("/admin/dashboard", "/admin/manage-users");
        Set<String> voterRoutes = Set.of("/voter/vote", "/voter/profile");

        // Route validation based on role
        if (adminRoutes.contains(requestURI) && !"ADMIN".equals(role)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Forbidden: Admin access required");
            return;
        }

        if (voterRoutes.contains(requestURI) && !"VOTER".equals(role)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Forbidden: Voter access required");
            return;
        }

        // If token is valid and the user is authorized for the requested route, continue
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
