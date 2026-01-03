package com.premanand.secureauth.auth.controller;

import com.premanand.secureauth.auth.dto.LoginRequest;
import com.premanand.secureauth.auth.dto.RefreshTokenRequest;
import com.premanand.secureauth.auth.dto.TokenResponse;
import com.premanand.secureauth.auth.service.AuthService;
import com.premanand.secureauth.auth.service.RefreshTokenService;
import com.premanand.secureauth.security.JwtTokenProvider;
import com.premanand.secureauth.token.entity.RefreshToken;
import com.premanand.secureauth.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST Controller for authentication operations.
 *
 * Handles user login and token issuance.
 *
 * Base Path: /api/v1/auth
 *
 * @author Premanand
 * @version 1.1
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor for dependency injection.
     *
     * @param authService The authentication service
     */
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticate a user and generate access & refresh tokens.
     *
     * Endpoint: POST /api/v1/auth/login
     *
     * Authentication Flow:
     * 1. Validate login request
     * 2. Authenticate user credentials
     * 3. Generate short-lived JWT access token
     * 4. Generate long-lived refresh token
     * 5. Return both tokens to client
     *
     * @param request LoginRequest containing username and password
     *        - username: User's unique username (required)
     *        - password: User's plaintext password (required)
     *
     * @return ResponseEntity with TokenResponse containing:
     *         - accessToken (JWT, 15 min expiry)
     *         - refreshToken (opaque, 7 days expiry)
     *         - tokenType (Bearer)
     *
     * HTTP Status Codes:
     * - 200 OK: Authentication successful
     * - 401 Unauthorized: Invalid credentials
     * - 422 Unprocessable Entity: Validation failed
     *
     * Example Request:
     * POST /api/v1/auth/login
     * {
     *   "username": "admin",
     *   "password": "Admin@123"
     * }
     *
     * Example Response:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
     *   "tokenType": "Bearer"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse response = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh an expired access token using a valid refresh token.
     *
     * Endpoint: POST /api/v1/auth/refresh
     *
     * Token Refresh Flow:
     * 1. Validate refresh token request
     * 2. Verify refresh token validity and expiration
     * 3. Retrieve associated user from refresh token
     * 4. Generate new short-lived JWT access token
     * 5. Return new access token with existing refresh token
     *
     * @param request RefreshTokenRequest containing refresh token
     *        - refreshToken: Valid opaque refresh token (required)
     *
     * @return ResponseEntity with TokenResponse containing:
     *         - accessToken (new JWT, 15 min expiry)
     *         - refreshToken (same token, unchanged)
     *         - tokenType (Bearer)
     *
     * HTTP Status Codes:
     * - 200 OK: Token refresh successful
     * - 401 Unauthorized: Invalid or expired refresh token
     * - 422 Unprocessable Entity: Validation failed
     *
     * Example Request:
     * POST /api/v1/auth/refresh
     * {
     *   "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c"
     * }
     *
     * Example Response:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
     *   "tokenType": "Bearer"
     * }
     *
     * @throws RuntimeException if refresh token is invalid or expired
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.verify(request.getRefreshToken());

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return ResponseEntity.ok(
                new TokenResponse(newAccessToken, refreshToken.getToken())
        );
    }

}
