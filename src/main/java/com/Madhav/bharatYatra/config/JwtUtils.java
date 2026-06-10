package com.Madhav.bharatYatra.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // Generate token using NEW API
    public String generateToken(String email) {
        return Jwts.builder()
            .subject(email)                    
            .issuedAt(new Date())              // was setIssuedAt() — deprecated
            .expiration(new Date(             //was setExpiration() — deprecated
                System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getKey())               //algorithm auto-detected from key
            .compact();
    }

    // ✅ Extract email using NEW API
    public String extractEmail(String token) {
        return Jwts.parser()                  //was parserBuilder() — deprecated
            .verifyWith(getKey())            
            .build()
            .parseSignedClaims(token)         //was parseClaimsJws() — deprecated
            .getPayload()
            .getSubject();
    }

    // Validate token using NEW API
    public boolean validateToken(String token) {
        try {
            Jwts.parser()                    
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);   
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT EXPIRED: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT UNSUPPORTED: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("JWT MALFORMED: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("JWT SIGNATURE INVALID: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT EMPTY/NULL: " + e.getMessage());
        }
        return false;
    }

    //Returns SecretKey (not Key) — required by new JJWT API
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}