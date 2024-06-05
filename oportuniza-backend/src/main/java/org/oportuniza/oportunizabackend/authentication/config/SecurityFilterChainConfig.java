package org.oportuniza.oportunizabackend.authentication.config;

import org.oportuniza.oportunizabackend.authentication.filters.JwtAuthenticationFilter;
import org.oportuniza.oportunizabackend.authentication.filters.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // indicating that an object is a source of bean definitions
public class SecurityFilterChainConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // filter requests based on validation of jwt token
    private final LoggingFilter loggingFilter; // log requests that come to the server

    // Dep inj by constructor
    public SecurityFilterChainConfig(JwtAuthenticationFilter jwtAuthenticationFilter, LoggingFilter loggingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.loggingFilter = loggingFilter;
    }

    // Basic configuration of the filter
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Cross-Site Request Forgery (why to disable this?)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll();
                    registry.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                    registry.anyRequest().authenticated();
                })
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class) // logging filter before jwt to log even invalid requests
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}