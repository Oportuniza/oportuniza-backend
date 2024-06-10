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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*"); // Allow all origins
        configuration.addAllowedHeader("*"); // Allow all headers
        configuration.addAllowedMethod("*"); // Allow all methods

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}