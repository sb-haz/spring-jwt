package com.hasan.book.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // This will be injected by Spring since we have the @RequiredArgsConstructor annotation, so it makes a constructor and injects it
    private final JwtService jwtService; // This is our JWT service. We will use this to extract the user email from the JWT token
    private final UserDetailsServiceImpl userDetailsService; // Used this to load the user by email

    @Override // This is the method that will be called for each request
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain // Contains the chain of the filters that will be executed after this filter
    ) throws ServletException, IOException {

        // We do not need to check the JWT token for the /auth/** endpoints
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response); // If the request path contains /api/v1/auth, then we skip and skip to the next filter
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // Get the Authorization header from the request object
        final String jwt;
        final String userEmail;

        // If the Authorization header is null or doesn't start with Bearer, then we skip the JWT token check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // If the request path contains /api/v1/auth, then we skip and skip to the next filter
            return;
        }

        jwt = authHeader.substring(7); // Get the JWT token from the Authorization header, excluding the Bearer part
        userEmail = jwtService.extractUsername(jwt);

        // If the user email is not null and the user is not authenticated, then we authenticate the user
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // Load the user by email
            if (jwtService.isTokenValid(jwt, userDetails)) { // If the JWT token is valid
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( // Authenticate the user
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails( // Set the authentication details
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); // Set the authentication token in the security context
            }
        }
        filterChain.doFilter(request, response); // Continue to the next filter
    }
}