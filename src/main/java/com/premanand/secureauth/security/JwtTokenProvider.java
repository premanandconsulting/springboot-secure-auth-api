package com.premanand.secureauth.security;

import com.premanand.secureauth.config.JwtConfigProperties;
import com.premanand.secureauth.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final JwtConfigProperties jwtConfig;
    private final Key key;

    public JwtTokenProvider(JwtConfigProperties jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    /* =========================================================
       ACCESS TOKEN GENERATION
       ========================================================= */

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtConfig.getAccessTokenExpiry());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("roles",
                        user.getRoles()
                                .stream()
                                .map(Enum::name)
                                .collect(Collectors.toList()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* =========================================================
       STRICT VALIDATION (NORMAL API FLOW)
       ========================================================= */

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // signature + expiry checked
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /* =========================================================
       REFRESH FLOW SUPPORT (NEW)
       ========================================================= */

    /**
     * Extract username from token while:
     * - Verifying signature
     * - Ignoring expiration
     *
     * Used ONLY during refresh-token flow.
     */
    public String getUsernameAllowExpiredToken(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException ex) {
            // Signature already verified before expiry exception
            return ex.getClaims().getSubject();
        }
    }

    /* =========================================================
       INTERNAL PARSER
       ========================================================= */

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
