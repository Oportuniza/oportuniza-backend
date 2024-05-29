package org.oportuniza.oportunizabackend.services.webtoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    // Get secret from environment variable (safer than storing in plain-text)
    private static final String SECRET = loadSecretFromFile("src/main/resources/secretKey.txt");
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);

    private static String loadSecretFromFile(String filePath) {
        try {
            //String currentDirectory = System.getProperty("user.dir");
            //System.out.println("Current working directory: " + currentDirectory);
            return new String(Files.readAllBytes(Paths.get(filePath))).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JWT secret from file", e);
        }
    }

    /*
    Generates a token from the user details
    Token contains:
    claims - additional information
    subject - username
    issuedAt - time when the token was issued
    expiration - time at which the token will be invalid
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact(); // to JSON representation
    }

    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
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

    public boolean isTokenValid(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}