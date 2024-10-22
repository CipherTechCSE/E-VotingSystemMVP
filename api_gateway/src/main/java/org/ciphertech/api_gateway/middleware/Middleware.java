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

    private static final Set<String> PUBLIC_ROUTES = Set.of(
            "/public/login",
            "/public/register",
            "/public/health-check",
            "/api/auth/register",
            "/api/auth/login"
    );

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

        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (!jwtUtil.validateToken(token, username)) {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"error\": \"Unauthorized: Invalid token\"}");
            return;
        }

        String role = jwtUtil.extractRole(token);
        Set<String> adminRoutes = Set.of("/admin/dashboard", "/admin/manage-users");
        Set<String> voterRoutes = Set.of("/voter/vote", "/voter/profile");

        if (adminRoutes.contains(requestURI) && !"ADMIN".equals(role)) {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("{\"error\": \"Forbidden: Admin access required\"}");
            return;
        }

        if (voterRoutes.contains(requestURI) && !"VOTER".equals(role)) {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("{\"error\": \"Forbidden: Voter access required\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    // Optional init and destroy methods can be removed if not used
}
