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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final LoggingFilter loggingFilter;

    public SecurityConfiguration(JwtFilter jwtFilter, LoggingFilter loggingFilter) {
        this.jwtFilter = jwtFilter;
        this.loggingFilter = loggingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(HttpMethod.POST, "api/auth/register").permitAll();
                    registry.requestMatchers(HttpMethod.POST, "api/auth/login").permitAll();
                    registry.anyRequest().authenticated();
                    // registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    // registry.requestMatchers("/user/**").hasRole("USER");
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // validate jwt and set securityContext
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}