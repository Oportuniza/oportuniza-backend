package org.oportuniza.oportunizabackend.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration-time}")
    private long expirationTime;

    /*
    Generates a token from the user details
    Token contains:
    claims - additional information
    subject - username
    issuedAt - time when the token was issued
    expiration - time at which the token will be invalid
     */
    public String generateToken(UserDetails userDetails) {
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
    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey); // Use HMAC-SHA algorithm
    }

    // Extract claims from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract username from the token
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    // Extract expiration from the token
    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    /*
    Get a claims object from the string representation of the jwt
    Claims object allows access for the token fields
     */
    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    // Verify if token is valid (correct username and not expired)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (extractUsername(token).equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Verify if token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}