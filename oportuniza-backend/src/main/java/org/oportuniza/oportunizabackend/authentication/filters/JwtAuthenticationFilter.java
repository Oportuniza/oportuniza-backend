package org.oportuniza.oportunizabackend.authentication.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.oportuniza.oportunizabackend.authentication.utils.JwtUtils;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final UserService userService;    // user-related methods such as get user from userid

    public JwtAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    /*
    Filter to validate token
    Sets the authentication context if the token is valid
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var jwtOptional = getTokenFromRequest(request);

        if (jwtOptional.isPresent()) {
            var jwtToken = jwtOptional.get();
            if (JwtUtils.validateToken(jwtToken)) {
                // Extract username from token
                var usernameOptional = JwtUtils.extractUsername(jwtToken);
                if (usernameOptional.isPresent()) {
                    var username = usernameOptional.get();
                    User user = userService.loadUserByUsername(username);
                    var authenticationToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        // Extract authorization header
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Bearer <JWT>
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }


}
