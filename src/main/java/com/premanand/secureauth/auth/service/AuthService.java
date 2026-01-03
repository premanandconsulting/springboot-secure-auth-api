package com.premanand.secureauth.auth.service;

import com.premanand.secureauth.auth.dto.TokenResponse;
import com.premanand.secureauth.security.JwtTokenProvider;
import com.premanand.secureauth.token.entity.RefreshToken;
import com.premanand.secureauth.user.entity.User;
import com.premanand.secureauth.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for authentication operations.
 *
 * Handles user login logic including:
 * - User lookup by username
 * - Password validation using BCrypt
 * - JWT access token generation
 * - Refresh token generation and persistence
 *
 * Security Notes:
 * - Uses BCrypt for secure password verification
 * - Does not store plaintext passwords
 * - Validates credentials before token generation
 * - Issues short-lived access tokens and revocable refresh tokens
 *
 * @author Premanand
 * @version 1.1
 * @see JwtTokenProvider
 * @see RefreshTokenService
 * @see UserRepository
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder BCrypt password encoder
     * @param jwtTokenProvider JWT token generation provider
     * @param refreshTokenService Refresh token lifecycle service
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Authenticates a user by username and password.
     *
     * Authentication Flow:
     * 1. Retrieve user from database by username
     * 2. Verify plaintext password against BCrypt hash
     * 3. Generate short-lived JWT access token
     * 4. Generate long-lived refresh token (stored server-side)
     * 5. Return both tokens to caller
     *
     * Security Considerations:
     * - User lookup happens first (no error info leakage)
     * - Password comparison uses timing-safe BCrypt
     * - Generic error message for both username and password
     * - Refresh token can be revoked (logout support)
     * - Access token remains stateless
     *
     * @param username User's unique username
     * @param rawPassword User's plaintext password (not hashed)
     *
     * @return TokenResponse containing:
     *         - accessToken (JWT, expires in 15 minutes)
     *         - refreshToken (opaque token, expires in 7 days)
     *
     * @throws RuntimeException if credentials are invalid
     *
     * @see JwtTokenProvider#generateAccessToken(User)
     * @see RefreshTokenService#create(User)
     */
    public TokenResponse login(String username, String rawPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate access token (stateless)
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        // Generate refresh token (stateful, revocable)
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }
}
