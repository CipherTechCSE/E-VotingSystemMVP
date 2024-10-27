package org.ciphertech.api_gateway.services.auth_service.config;

import org.ciphertech.api_gateway.middleware.Middleware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ROUTES = {
            "/public/**",
            "/api/auth/**",
            "/admin/register",
            "/employee/register",
            "/employee/verify",
            "/admin/login",
            "/employee/login",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui/index.html",
            "/swagger-ui",
            "/favicon.ico",
            "/error"
    };

    private final Middleware middleware;

    public SecurityConfig(Middleware middleware) {
        this.middleware = middleware;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // Disable CSRF
                .cors(AbstractHttpConfigurer::disable)            // Disable CORS (adjust if needed)
                .authorizeHttpRequests(auth -> auth      // Configure authorization
                        .requestMatchers(PUBLIC_ROUTES).permitAll()   // Allows public access to specified URLs
                        .requestMatchers("/api/authority/admin/**", "/api/verify/admin/**").hasRole("ADMIN_ROLE")  // Requires ADMIN role for specified URLs
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session    // Set stateless session management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(middleware, UsernamePasswordAuthenticationFilter.class);  // Add middleware filter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCrypt for password hashing
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui/**", "/v3/api-docs/**"
        );
    }

}
