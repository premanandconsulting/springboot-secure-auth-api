# API Documentation

## Authentication Service

### Overview
The authentication service handles user login, JWT token generation, and credential validation.

## Endpoints

### 1. User Login

**Endpoint**: `POST /api/v1/auth/login`

**Purpose**: Authenticate a user and receive a JWT access token.

**Request**:
```http
POST /api/v1/auth/login HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6InNlY3VyZS1hdXRoLWFwaSIsImlhdCI6MTcwNDExMDAwMCwiZXhwIjoxNzA0MTEwOTAwLCJhdXRob3JpdGllcyI6WyJBRE1JTiIsIlVTRVIiXX0.abc123...",
  "tokenType": "Bearer"
}
```

**Response** (401 Unauthorized):
```json
{
  "error": "Invalid username or password",
  "timestamp": "2026-01-01T10:00:00Z"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | String | Yes | User's username (min: 3, max: 50 chars) |
| password | String | Yes | User's password (min: 6 chars) |

**Response Fields**:
| Field | Type | Description |
|-------|------|-------------|
| token | String | JWT access token (valid for 15 minutes) |
| tokenType | String | Token type (always "Bearer") |

**Error Codes**:
| Code | Message | Meaning |
|------|---------|---------|
| 401 | Invalid username or password | Authentication failed |
| 400 | Bad Request | Invalid request format |
| 422 | Validation failed | Missing required fields |

**Example with cURL**:
```bash
# Successful login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Failed login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

**Using the Token**:

After receiving the token, include it in subsequent requests:

```bash
curl -X GET http://localhost:8081/api/v1/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Token Payload** (decoded):
```json
{
  "sub": "admin",
  "iss": "secure-auth-api",
  "iat": 1704110000,
  "exp": 1704110900,
  "authorities": ["ADMIN", "USER"]
}
```

**Token Fields**:
| Field | Description |
|-------|-------------|
| sub | Subject (username) |
| iss | Issuer (always "secure-auth-api") |
| iat | Issued At (Unix timestamp) |
| exp | Expiration Time (Unix timestamp, 15 min from iat) |
| authorities | User's roles/authorities |

---

### 2. Refresh Token

**Endpoint**: `POST /api/v1/auth/refresh`

**Purpose**: Refresh an expired access token using a valid refresh token.

**Request**:
```http
POST /api/v1/auth/refresh HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6InNlY3VyZS1hdXRoLWFwaSIsImlhdCI6MTcwNDExMDkwMCwiZXhwIjoxNzA0MTExODAwLCJhdXRob3JpdGllcyI6WyJBRE1JTiIsIlVTRVIiXX0.def456...",
  "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
  "tokenType": "Bearer"
}
```

**Response** (401 Unauthorized):
```json
{
  "error": "Invalid or expired refresh token",
  "timestamp": "2026-01-01T10:00:00Z"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| refreshToken | String | Yes | Valid refresh token (UUID format) |

**Response Fields**:
| Field | Type | Description |
|-------|------|-------------|
| accessToken | String | New JWT access token (valid for 15 minutes) |
| refreshToken | String | Same refresh token (unchanged) |
| tokenType | String | Token type (always "Bearer") |

**Error Codes**:
| Code | Message | Meaning |
|------|---------|---------|
| 401 | Invalid or expired refresh token | Token verification failed |
| 400 | Bad Request | Invalid request format |
| 422 | Validation failed | Missing required fields |

**Refresh Token Flow**:
1. Client calls `/login` endpoint with credentials
2. Server returns `accessToken` (15 min) and `refreshToken` (7 days)
3. Client stores both tokens
4. When access token expires, client calls `/refresh` with refresh token
5. Server validates refresh token and issues new access token
6. Client continues using new access token

**Example with cURL**:
```bash
# Request new access token
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c"}'

# Response with new access token
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
  "tokenType": "Bearer"
}
```

**Token Lifecycle**:
```
1. User logs in
   ├─ Receives Access Token (15 min)
   └─ Receives Refresh Token (7 days)

2. Access Token expires after 15 minutes

3. User calls /refresh endpoint
   ├─ Sends Refresh Token
   ├─ Server validates and issues new Access Token
   └─ Same Refresh Token is returned

4. After 7 days, Refresh Token expires
   └─ User must login again
```

---

## Data Models

### LoginRequest DTO

```java
/**
 * Request object for user authentication.
 * 
 * Example:
 * {
 *   "username": "admin",
 *   "password": "admin123"
 * }
 */
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

### LoginResponse DTO

```java
/**
 * Response object after successful authentication.
 * 
 * Example:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer"
 * }
 */
public class LoginResponse {
    private String token;              // JWT access token
    private String tokenType = "Bearer"; // Always "Bearer" for JWT
}
```

### RefreshTokenRequest DTO

```java
/**
 * Request object for refreshing an expired access token.
 * 
 * Example:
 * {
 *   "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c"
 * }
 */
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
```

### TokenResponse DTO

```java
/**
 * Response object for both login and refresh token endpoints.
 * Contains access token, refresh token, and token type.
 * 
 * Example:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
 *   "tokenType": "Bearer"
 * }
 */
public class TokenResponse {
    private String accessToken;        // JWT access token (15 min expiry)
    private String refreshToken;       // Opaque refresh token (7 days expiry)
    private String tokenType = "Bearer"; // Always "Bearer" for JWT
}
```

### User Entity

```java
/**
 * Represents a user in the system.
 * 
 * Fields:
 * - id: Unique identifier (auto-generated)
 * - username: Unique username (unique constraint)
 * - email: User's email address
 * - password: BCrypt hashed password
 * - roles: Set of roles/authorities (USER, ADMIN)
 * - createdAt: Account creation timestamp
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;  // BCrypt hashed
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
```

### Role Enum

```java
/**
 * User roles/authorities.
 * 
 * ADMIN - Administrative access
 * USER  - Regular user access
 */
public enum Role {
    USER,    // Regular user
    ADMIN    // Administrator
}

```

### RefreshToken Entity

```java
/**
 * Represents a refresh token for obtaining new access tokens.
 * 
 * Fields:
 * - id: Unique identifier
 * - token: The refresh token string (unique)
 * - user: Associated user
 * - expiryDate: When the token expires
 * - revoked: Whether the token has been revoked
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    @Column(nullable = false)
    private boolean revoked;
}

```

---

## Core Services

### AuthService

**Class**: `com.premanand.secureauth.auth.service.AuthService`

**Responsibility**: Handles user authentication logic.

**Methods**:

#### login(String username, String rawPassword)

Authenticates a user and returns a JWT access token.

```java
/**
 * Authenticates a user by username and password.
 * 
 * @param username The user's username
 * @param rawPassword The user's plaintext password
 * @return JWT access token
 * @throws RuntimeException if credentials are invalid
 */
public String login(String username, String rawPassword) {
    // implementation
}

```

**Flow**:
1. Find user by username in database
2. Verify password using BCrypt
3. If valid, generate JWT access token
4. Return token to client

**Throws**:
- `RuntimeException`: "Invalid username or password" when credentials don't match

---

### JwtTokenProvider

**Class**: `com.premanand.secureauth.security.JwtTokenProvider`

**Responsibility**: JWT token generation and validation.

**Methods**:

#### generateAccessToken(User user)

Generates a JWT access token for a user.

```java
/**
 * Generates a JWT access token for the given user.
 * Token expires in 15 minutes (configurable).
 * 
 * @param user The user to generate token for
 * @return Signed JWT access token
 */
public String generateAccessToken(User user) {
    // implementation
}

```

**Token Claims**:
- `sub`: Username
- `iss`: Issuer ("secure-auth-api")
- `iat`: Issued at timestamp
- `exp`: Expiration timestamp
- `authorities`: User's roles

#### validateToken(String token)

Validates a JWT token.

```java
/**
 * Validates a JWT token.
 * Checks signature, expiry, and format.
 * 
 * @param token The JWT token to validate
 * @return true if valid, false otherwise
 */
public boolean validateToken(String token) {
    // implementation
}
```

#### getUsername(String token)

Extracts username from a JWT token.

```java
/**
 * Extracts the username from a JWT token.
 * 
 * @param token The JWT token
 * @return The username (subject claim)
 * @throws Exception if token is invalid
 */
public String getUsername(String token) {
    return username;
}
```

---

### UserDetailsServiceImpl

**Class**: `com.premanand.secureauth.security.UserDetailsServiceImpl`

**Responsibility**: Loads user details from database for Spring Security.

**Methods**:

#### loadUserByUsername(String username)

Loads user details by username.

```java
/**
 * Loads user details from database by username.
 * 
 * @param username The username to load
 * @return UserDetails object with authorities
 */
@Override
public UserDetails loadUserByUsername(String username) {
    // implementation
}

```

---

## Configuration Classes

### SecurityConfig

**Class**: `com.premanand.secureauth.config.SecurityConfig`

**Responsibility**: Spring Security configuration.

**Key Configuration**:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
```

Configures:
- Authentication filter chain
- JWT authentication filter
- CORS (if enabled)
- CSRF (if enabled)
- HTTPS redirect (if configured)

### JwtConfigProperties

**Class**: `com.premanand.secureauth.config.JwtConfigProperties`

**Responsibility**: Binds JWT configuration from `application.yml`.

**Properties**:
```yaml
security:
  jwt:
    secret: dev-secret-key-dev-secret-key-1234567890
    access-token-expiry: 900           # seconds
    refresh-token-expiry: 604800       # seconds
    issuer: secure-auth-api
```

### PasswordConfig

**Class**: `com.premanand.secureauth.config.PasswordConfig`

**Responsibility**: Configures BCrypt password encoder.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## Filters

### JwtAuthenticationFilter

**Class**: `com.premanand.secureauth.security.JwtAuthenticationFilter`

**Responsibility**: Validates JWT tokens in incoming requests.

**Flow**:
1. Extract token from `Authorization: Bearer <token>` header
2. Validate token using `JwtTokenProvider`
3. Load user details using `UserDetailsService`
4. Create `Authentication` object
5. Set in `SecurityContext`
6. Continue filter chain

**Processed For**: All requests except `/auth/login` and `/h2-console`

---

## Default Users

Sample users created on application startup via `DataLoader`:

| Username | Password | Email | Roles |
|----------|----------|-------|-------|
| admin | admin123 | admin@example.com | ADMIN, USER |
| user | user123 | user@example.com | USER |

---

## Error Handling

### Standard Error Response

```json
{
  "error": "Error message",
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 401
}
```

### Common Errors

| Status | Error | Cause |
|--------|-------|-------|
| 400 | Bad Request | Invalid request format |
| 401 | Unauthorized | Invalid credentials or expired token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 422 | Unprocessable Entity | Validation failure |
| 500 | Internal Server Error | Server error |

---

## Testing

### Unit Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "username": "admin",
                    "password": "admin123"
                }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
```

---

## Environment Variables

For production, set these environment variables:

```bash
# JWT Configuration
JWT_SECRET=your-secret-key-min-32-chars
JWT_ACCESS_TOKEN_EXPIRY=900          # 15 minutes
JWT_REFRESH_TOKEN_EXPIRY=604800      # 7 days
JWT_ISSUER=your-app-name

# Database (if not using H2)
DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=password

# Server
SERVER_PORT=8081
```

---

## References

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [JWT.io](https://jwt.io)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

