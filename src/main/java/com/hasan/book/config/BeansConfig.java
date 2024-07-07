package com.hasan.book.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // This is a configuration class
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
public class BeansConfig {

    private final UserDetailsService userDetailsService; // This is our custom UserDetailsService

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // We are creating a DaoAuthenticationProvider
        authProvider.setUserDetailsService(userDetailsService); // We are setting the userDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // We are setting the passwordEncoder
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // We are returning a BCryptPasswordEncoder
    }

}
