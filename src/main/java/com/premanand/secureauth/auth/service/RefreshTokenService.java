package com.premanand.secureauth.auth.service;

import com.premanand.secureauth.config.JwtConfigProperties;
import com.premanand.secureauth.token.entity.RefreshToken;
import com.premanand.secureauth.token.repository.RefreshTokenRepository;
import com.premanand.secureauth.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service responsible for refresh token lifecycle management.
 *
 * Responsibilities:
 * - Create refresh tokens
 * - Validate refresh tokens
 * - Revoke refresh tokens (logout)
 *
 * Security Notes:
 * - Refresh tokens are opaque (UUID-based)
 * - Stored server-side and can be revoked
 * - Expiry is enforced strictly
 *
 * @author Premanand
 * @version 1.0
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfigProperties jwtConfigProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtConfigProperties jwtConfigProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtConfigProperties = jwtConfigProperties;
    }

    /**
     * Create and persist a refresh token for a user.
     */
    public RefreshToken create(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plusSeconds(jwtConfigProperties.getRefreshTokenExpiry())
        );
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    /**
     * Verify refresh token validity.
     */
    public RefreshToken verify(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()
                || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    /**
     * Revoke all refresh tokens for a user (logout).
     */
    public void revoke(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
