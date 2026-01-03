package com.premanand.secureauth.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for user login request.
 *
 * This DTO encapsulates the credentials required for user authentication.
 * It is used to receive login data from clients via REST API.
 *
 * Validation:
 * - username: Required, must not be blank
 * - password: Required, must not be blank
 *
 * Example JSON:
 * {
 *   "username": "admin",
 *   "password": "admin123"
 * }
 *
 * @author Premanand
 * @version 1.0
 * @see LoginResponse
 */
public class LoginRequest {

    /**
     * User's unique username.
     *
     * Constraints:
     * - Required (must not be blank)
     * - Min: 3 characters (enforced in AuthService)
     * - Max: 50 characters (enforced in AuthService)
     * - Should be unique in database
     */
    @NotBlank(message = "Username is required")
    @Getter
    @Setter
    private String username;

    /**
     * User's plaintext password.
     *
     * Constraints:
     * - Required (must not be blank)
     * - Min: 6 characters (enforced in AuthService)
     * - Must match BCrypt hashed password in database
     * - Never stored in plaintext - always hashed via BCrypt
     */
    @NotBlank(message = "Password is required")
    @Getter
    @Setter
    private String password;
}
