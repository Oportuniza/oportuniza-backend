package org.oportuniza.oportunizabackend.authentication.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        // Process the request and only then log status
        LOG.warn("Received request: {} {}: {}", request.getMethod(), request.getRequestURI(), response.getStatus());
    }
}

