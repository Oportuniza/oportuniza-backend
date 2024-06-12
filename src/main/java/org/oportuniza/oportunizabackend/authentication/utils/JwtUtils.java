package org.oportuniza.oportunizabackend.authentication.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@Configuration
public class JwtUtils {

    private static String secretKey;
    private static long expirationTime;

    @Value("${jwt.secret}")
    private String configSecretKey;

    @Value("${jwt.expiration-time}")
    private long configExpirationTime;

    @PostConstruct
    public void init() {
        JwtUtils.secretKey = this.configSecretKey;
        JwtUtils.expirationTime = this.configExpirationTime;
    }
    /*
    Generates a token from the user details
    Token contains:
    claims - additional information
    subject - username
    issuedAt - time when the token was issued
    expiration - time at which the token will be invalid
     */
    public static String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>(); // Can pass additional properties to the token if needed

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationTime)))
                .signWith(generateKey())
                .compact(); // to JSON representation
    }

    // Generate a SecretKey object from the String secretKey
    private static SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey); // Use HMAC-SHA algorithm
    }

    // Extract username from the token
    public static Optional<String> extractUsername(String jwt) {
        var claimsOptional = parseToken(jwt);
        return claimsOptional.map(Claims::getSubject);
    }

    /*
    Get a claims object from the string representation of the jwt
    Claims object allows access for the token fields
     */
    private static Optional<Claims> parseToken(String jwt) {
        var jwtParser = Jwts.parser()
                .verifyWith(generateKey())
                .build();

        try {
            return Optional.of(jwtParser.parseSignedClaims(jwt)
                    .getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage());
            return Optional.empty();
        }

    }


    public static boolean validateToken(String jwt) {
        return parseToken(jwt).isPresent();
    }
}