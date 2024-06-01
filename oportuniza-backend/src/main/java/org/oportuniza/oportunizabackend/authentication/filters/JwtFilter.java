package org.oportuniza.oportunizabackend.authentication.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.oportuniza.oportunizabackend.authentication.service.JwtService;
import org.oportuniza.oportunizabackend.users.service.DetailsUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;  // allows creating and validating tokens
    private final DetailsUserService userDetailService;    // user-related methods such as get user from userid

    public JwtFilter(JwtService jwtService, DetailsUserService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    /*
    Filter to validate token
    Sets the authentication context if the token is valid
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // If token is not present pass to the next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        // Checks if the security context is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Get user from DB
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            // Check if user exists and token is valid
            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Marks the user as authenticated in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
