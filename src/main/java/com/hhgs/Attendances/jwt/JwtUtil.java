package com.hhgs.Attendances.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final String SECRET_STRING = "HHGS8433MyVerySecretKeyForJWTToken1234567890";
    private final SecretKey SECRET_KEY;

    public JwtUtil() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String name, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", name);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .signWith(SECRET_KEY)
                .compact();
    }

     public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract username (email) from token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract name from token
    public String extractName(String token) {
        return extractClaim(token, claims -> (String) claims.get("name"));
    }

    // Extract role from token
    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    // Extract expiration date - THIS WAS MISSING
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate token with email
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Validate token without comparing email
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}