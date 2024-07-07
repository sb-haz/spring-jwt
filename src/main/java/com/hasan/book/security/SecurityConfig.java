package com.hasan.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // This is a web security configuration
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@EnableMethodSecurity(securedEnabled = true) // This is to enable method level security
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter; // This is our custom JWT filter
    private final AuthenticationProvider authenticationProvider; // This is our custom authentication provider

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // This is to enable CORS
                .csrf(AbstractHttpConfigurer::disable) // This is to disable CSRF
                .authorizeHttpRequests(req -> // This is to authorize the requests based on the matchers
                        req.requestMatchers(
                                "/auth/**", // We have a controller that has /auth/** endpoints
                                "/v2/api-docs", // This and the below matchers are for Swagger
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                                ).permitAll().anyRequest().authenticated()) // Permit all the requests to the mentioned matchers and authenticate all the other requests
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // This is to make the session stateless
                .authenticationProvider(authenticationProvider) // This is to set the authentication provider to our custom authentication provider class (JwtAuthenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // This is to add our JWT filter before the UsernamePasswordAuthenticationFilter

        return http.build();
    }

}
