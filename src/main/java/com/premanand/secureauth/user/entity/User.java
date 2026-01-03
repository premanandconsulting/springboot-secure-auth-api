package com.premanand.secureauth.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

/**
 * Entity class representing a user in the authentication system.
 *
 * Responsibilities:
 * - Store user credentials (username, email, password)
 * - Maintain user roles/permissions
 * - Track account creation timestamp
 *
 * Database Table: users
 *
 * Constraints:
 * - username: UNIQUE, NOT NULL
 * - email: UNIQUE, NOT NULL
 * - password: NOT NULL (must be BCrypt hashed)
 * - id: PRIMARY KEY, AUTO_INCREMENT
 *
 * Relationships:
 * - One User has many Roles (ElementCollection)
 * - One User has many RefreshTokens (one-to-many, inverse)
 *
 * Security Notes:
 * - Password is always stored as BCrypt hash, never plaintext
 * - Username and email are unique per user
 * - Roles are eagerly loaded for authorization
 * - CreatedAt is immutable (set at creation, cannot be updated)
 *
 * @author Premanand
 * @version 1.0
 * @see Role
 * @see RefreshToken
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     *
     * Auto-generated primary key using IDENTITY strategy.
     * Assigned by the database automatically on insert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's unique login username.
     *
     * Properties:
     * - UNIQUE: Only one user can have this username
     * - NOT NULL: Required field
     * - Used for login and token subject
     *
     * Constraints (enforced in DTOs):
     * - Min: 3 characters
     * - Max: 50 characters
     * - Pattern: Alphanumeric and underscores recommended
     */
    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * User's email address.
     *
     * Properties:
     * - UNIQUE: Only one user can have this email
     * - NOT NULL: Required field
     * - Used for notifications and password recovery
     *
     * Format: Valid email address (RFC 5322)
     */
    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User's password hash.
     *
     * Properties:
     * - NOT NULL: Required field
     * - Always stored as BCrypt hash
     * - Never stored or transmitted as plaintext
     * - Hash cost factor: 10 (default)
     *
     * Security:
     * - Verified using BCrypt.matches() method
     * - Timing-safe comparison
     * - Salt included in hash
     *
     * Example hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/tBy
     */
    @Setter
    @Getter
    @Column(nullable = false)
    private String password;

    /**
     * User's roles/authorities.
     *
     * Properties:
     * - Collection of Role enums (ADMIN, USER)
     * - EAGER fetch: Loaded automatically with user
     * - Stored in separate table: user_roles
     *
     * Join Details:
     * - Table: user_roles
     * - Foreign Key: user_id -> users.id
     * - Column: role (VARCHAR enum)
     *
     * Usage:
     * - Determines user permissions
     * - Used in authorization checks
     * - Included in JWT token claims
     *
     * @see Role
     */
    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<Role> roles;

    /**
     * Account creation timestamp (UTC).
     *
     * Properties:
     * - NOT NULL: Always set
     * - IMMUTABLE: Cannot be updated (updatable = false)
     * - Format: ISO 8601 (java.time.Instant)
     *
     * Usage:
     * - Audit trail
     * - Account age calculation
     * - Account locking rules (e.g., new accounts)
     *
     * Set automatically to:
     * - Current time in UTC at entity creation
     * - Cannot be changed after insert
     */
    @Setter
    @Getter
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}
