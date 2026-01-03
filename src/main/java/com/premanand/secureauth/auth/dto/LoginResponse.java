package com.premanand.secureauth.auth.dto;

/**
 * Data Transfer Object for user login response.
 *
 * This DTO encapsulates the response returned after successful authentication.
 * It contains the JWT access token and the token type.
 *
 * The token should be included in the Authorization header of subsequent requests:
 * Authorization: Bearer <accessToken>
 *
 * Example JSON:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer"
 * }
 *
 * Token Payload (JWT Claims):
 * - sub: Username (subject)
 * - iss: Issuer (secure-auth-api)
 * - iat: Issued at (Unix timestamp)
 * - exp: Expiration (Unix timestamp, 15 minutes from iat)
 * - authorities: User roles/permissions
 *
 * @author Premanand
 * @version 1.0
 * @see LoginRequest
 */
public class LoginResponse {

    /**
     * JWT access token.
     *
     * Properties:
     * - Format: JSON Web Token (JWT)
     * - Signature: HMAC-SHA256
     * - Expiry: 15 minutes (900 seconds, configurable)
     * - Contains user info and roles in payload
     * - Must be validated on each protected endpoint
     *
     * Usage:
     * Include in Authorization header:
     * Authorization: Bearer <accessToken>
     */
    private String accessToken;

    /**
     * Token type.
     *
     * Standard value: "Bearer" (OAuth 2.0 Bearer token format)
     * Used to indicate how the token should be used.
     * Default: "Bearer"
     */
    private String tokenType = "Bearer";

    /**
     * Constructs a LoginResponse with the provided access token.
     *
     * The token type is automatically set to "Bearer".
     *
     * @param accessToken The JWT access token string
     */
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the JWT access token.
     *
     * @return The JWT token string
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets the token type.
     *
     * @return The token type (always "Bearer")
     */
    public String getTokenType() {
        return tokenType;
    }
}
