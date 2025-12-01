package com.sams.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT utility class - handles all the token stuff
 * learned about JWT from that one youtube tutorial lol
 *
 * IMPORTANT: the secret key should be in env vars for production
 * we just hardcoded it here for development - dont do this in real life
 */
@Component
public class JwtUtil {

    // secret key for signing - needs to be long enough or it throws errors
    // TODO: move this to application.properties or env variable
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "SAMSSecretKeyForJWTTokenGenerationAndValidation2024StudentAcademicManagementSystem".getBytes()
    );

    // token lasts 24 hours - seemed reasonable
    // 24 * 60 * 60 * 1000 = 86400000 ms
    private final long jwtExpirationMs = 86400000;

    /**
     * creates a new JWT token with username and role
     * the role is stored as a claim so we can use it for authorization
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // build the token - jjwt makes this pretty easy
        return Jwts.builder()
                .subject(username)
                .claim("role", role) // custom claim for user role
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // get username from token - the subject field
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // get role from token - we stored it as a claim
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    // validates the token - checks signature and expiration
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // somethings wrong with the token
            return false;
        }
    }

    // chekc if token is expired - kept the typo because it works lol
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            // if we cant parse it, consider it expired
            return true;
        }
    }
}
