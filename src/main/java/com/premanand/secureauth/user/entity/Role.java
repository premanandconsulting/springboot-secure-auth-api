package com.premanand.secureauth.user.entity;

/**
 * Enum representing user roles/authorities in the system.
 *
 * Roles define what actions and resources a user can access.
 * Each user can have multiple roles.
 *
 * Usage:
 * - Stored in database as VARCHAR ENUM
 * - Included in JWT token claims
 * - Used in @PreAuthorize and @Secured annotations
 * - Checked during authorization
 *
 * Security:
 * - Each role grants specific permissions
 * - Default users have at minimum USER role
 * - Changing roles requires database update
 *
 * @author Premanand
 * @version 1.0
 * @see User
 */
public enum Role {
    /**
     * Regular user role.
     *
     * Permissions:
     * - View own profile
     * - Use core application features
     * - Cannot perform admin operations
     *
     * Default role assigned to new users.
     */
    USER,

    /**
     * Administrator role.
     *
     * Permissions:
     * - All USER permissions
     * - Manage users
     * - Access admin endpoints
     * - Manage system configuration
     * - View audit logs
     *
     * Requires explicit assignment.
     * Only assign to trusted users.
     */
    ADMIN
}
