package org.oportuniza.oportunizabackend.authentication.config;

import org.oportuniza.oportunizabackend.authentication.filters.JwtAuthenticationFilter;
import org.oportuniza.oportunizabackend.authentication.filters.LoggingFilter;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthenticationConfiguration {
    private final UserService userDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoggingFilter loggingFilter;

    public AuthenticationConfiguration(UserService userDetailService,
                                       JwtAuthenticationFilter jwtAuthenticationFilter, LoggingFilter loggingFilter) {
        this.userDetailService = userDetailService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.loggingFilter = loggingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // todo: NOT SAFE, ALTHOUGH REMOVING DOESN'T RECEIVE REQUEST
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/home", "api/auth/register", "api/auth/login").permitAll();
                    // registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    // registry.requestMatchers("/user/**").hasRole("USER");
                    registry.anyRequest().authenticated();
                })
                //.formLogin(AbstractAuthenticationFilterConfigurer::permitAll) VIEW IS IN VUE
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // validate jwt and set securityContext
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Retrieves user details from a UserDetailsService and uses a PasswordEncoder to verify passwords.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}