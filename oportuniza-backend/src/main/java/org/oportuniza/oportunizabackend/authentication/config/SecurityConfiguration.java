package org.oportuniza.oportunizabackend.authentication.config;

import org.oportuniza.oportunizabackend.authentication.filters.JwtFilter;
import org.oportuniza.oportunizabackend.authentication.filters.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // indicating that an object is a source of bean definitions
@EnableWebSecurity // to override the default spring security configuration
public class SecurityConfiguration {

    private final JwtFilter jwtFilter; // filter requests based on validation of jwt token
    private final LoggingFilter loggingFilter; // log requests that come to the server

    // Dep inj by constructor
    public SecurityConfiguration(JwtFilter jwtFilter, LoggingFilter loggingFilter) {
        this.jwtFilter = jwtFilter;
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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // AuthenticationManager attempts to authenticate the passed Authentication object
    // AuthenticationConfiguration from the current SecurityConfiguration class
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Can use other encoders such as argon2
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}