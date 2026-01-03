# Code Architecture & Developer Guide

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Layers](#project-layers)
- [Package Structure](#package-structure)
- [Key Components](#key-components)
- [Data Flow Diagrams](#data-flow-diagrams)
- [Security Architecture](#security-architecture)
- [Database Schema](#database-schema)
- [Adding New Features](#adding-new-features)
- [Testing Guidelines](#testing-guidelines)
- [Code Standards](#code-standards)

## Architecture Overview

The application follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────┐
│                    REST Client                       │
└────────────────────┬────────────────────────────────┘
                     │ HTTP Request/Response
                     ▼
┌─────────────────────────────────────────────────────┐
│              Controller Layer                        │
│   - AuthController                                  │
│   - Request validation                              │
│   - Response formatting                             │
└────────────────────┬────────────────────────────────┘
                     │ Service calls
                     ▼
┌─────────────────────────────────────────────────────┐
│              Service Layer                          │
│   - AuthService                                     │
│   - Business logic                                  │
│   - Orchestration                                   │
└────────────────────┬────────────────────────────────┘
                     │ Data access
                     ▼
┌─────────────────────────────────────────────────────┐
│         Repository & Security Layer                 │
│   - UserRepository                                  │
│   - RefreshTokenRepository                          │
│   - JwtTokenProvider                                │
│   - JwtAuthenticationFilter                         │
└────────────────────┬────────────────────────────────┘
                     │ SQL/Entity operations
                     ▼
┌─────────────────────────────────────────────────────┐
│              Database Layer                         │
│   - H2 (Development) / PostgreSQL (Production)      │
│   - User, Roles, RefreshToken tables               │
└─────────────────────────────────────────────────────┘
```

## Project Layers

### 1. Controller Layer (Presentation)

**Location**: `com.premanand.secureauth.auth.controller`

**Responsibility**: 
- Handle HTTP requests/responses
- Validate input data
- Format API responses
- Map to DTOs

**Components**:
- `AuthController` - Authentication endpoints
  - `POST /api/v1/auth/login` - User authentication
  - `POST /api/v1/auth/refresh` - Token refresh

### 2. Service Layer (Business Logic)

**Location**: `com.premanand.secureauth.auth.service`

**Responsibility**:
- Implement core business logic
- Coordinate between controller and repository
- Enforce business rules
- Transaction management

**Components**:
- `AuthService` - Authentication orchestration
  - User lookup
  - Password validation
  - Token generation
  - Refresh token creation
- `RefreshTokenService` - Refresh token management
  - Token verification
  - Token creation
  - Expiry validation
  - Token revocation support

### 3. Repository/Data Access Layer

**Location**: `com.premanand.secureauth.*.repository`

**Responsibility**:
- Database access
- Query execution
- Entity persistence

**Components**:
- `UserRepository` - User CRUD operations
- `RefreshTokenRepository` - Refresh token management

### 4. Security Layer

**Location**: `com.premanand.secureauth.security`

**Responsibility**:
- JWT token generation/validation
- Authentication filtering
- User details loading

**Components**:
- `JwtTokenProvider` - Token creation and validation
- `JwtAuthenticationFilter` - Token verification filter
- `UserDetailsServiceImpl` - Load user from database
- `PasswordConfig` - BCrypt configuration
- `SecurityConfig` - Spring Security configuration

### 5. Configuration Layer

**Location**: `com.premanand.secureauth.config`

**Responsibility**:
- Application configuration
- Bean creation
- Property binding

**Components**:
- `JwtConfigProperties` - JWT configuration binding
- `PasswordConfig` - Password encoder beans
- `SecurityConfig` - Security beans
- `DataLoader` - Sample data initialization

### 6. Model/Entity Layer

**Location**: `com.premanand.secureauth.*.entity`

**Responsibility**:
- Define data structures
- Entity-database mapping
- Relationships

**Components**:
- `User` - User entity
- `Role` - Role enum
- `RefreshToken` - Refresh token entity

## Package Structure

```
com.premanand.secureauth/
│
├── SecureAuthApplication.java                    # Entry point
│
├── auth/                                         # Authentication module
│   ├── controller/
│   │   └── AuthController.java                   # Login endpoint
│   ├── dto/
│   │   ├── LoginRequest.java                     # Login request DTO
│   │   └── LoginResponse.java                    # Login response DTO
│   └── service/
│       └── AuthService.java                      # Auth business logic
│
├── config/                                       # Configuration module
│   ├── DataLoader.java                           # Sample data
│   ├── JwtConfigProperties.java                  # JWT config binding
│   ├── PasswordConfig.java                       # Password encoder beans
│   └── SecurityConfig.java                       # Spring Security config
│
├── security/                                     # Security module
│   ├── JwtAuthenticationFilter.java              # Token filter
│   ├── JwtTokenProvider.java                     # Token provider
│   └── UserDetailsServiceImpl.java                # User details service
│
├── token/                                        # Refresh token module
│   ├── entity/
│   │   └── RefreshToken.java                     # Refresh token entity
│   └── repository/
│       └── RefreshTokenRepository.java           # Token persistence
│
└── user/                                         # User module
    ├── entity/
    │   ├── User.java                             # User entity
    │   └── Role.java                             # Role enum
    └── repository/
        └── UserRepository.java                   # User persistence
```

## Key Components

### AuthController

**Purpose**: Handle login requests

**Flow**:
```
1. Receive LoginRequest (username, password)
2. Validate request (@Valid annotation)
3. Call AuthService.login()
4. Return LoginResponse with token
```

**Endpoint**:
- `POST /api/v1/auth/login`

**Responsibility**:
- Request validation
- Service orchestration
- Response formatting

### AuthService

**Purpose**: Execute authentication logic

**Flow**:
```
1. Find user by username in database
2. Verify password with BCrypt
3. Generate JWT token
4. Return token
```

**Key Methods**:
- `login(username, password)` - Authenticate user

**Responsibility**:
- User lookup
- Password validation
- Token generation

### RefreshTokenService

**Purpose**: Manage refresh token operations

**Flow**:
```
1. Verify refresh token validity
2. Check token expiry date
3. Check if token is revoked
4. Retrieve associated user
5. Return token for new access token generation
```

**Key Methods**:
- `createRefreshToken(user)` - Create new refresh token
- `verify(token)` - Validate refresh token
- `revokeToken(token)` - Revoke a refresh token
- `deleteExpiredTokens()` - Clean up expired tokens

**Responsibility**:
- Token creation and storage
- Token verification and validation
- Expiry management
- Token revocation
- Cleanup of expired tokens

### JwtTokenProvider

**Purpose**: Manage JWT tokens

**Flow**:
```
1. Create JWT with claims
2. Sign with HMAC-SHA256
3. Return token string
```

**Key Methods**:
- `generateAccessToken(user)` - Create access token
- `validateToken(token)` - Verify token
- `getUsername(token)` - Extract username

**Responsibility**:
- Token creation
- Token validation
- Claims management

### JwtAuthenticationFilter

**Purpose**: Validate JWT in requests

**Flow**:
```
1. Extract token from Authorization header
2. Validate token signature and expiry
3. Load user details
4. Create Authentication object
5. Set in SecurityContext
6. Continue filter chain
```

**Responsibility**:
- Token extraction
- Token validation
- User authentication

### SecurityConfig

**Purpose**: Configure Spring Security

**Responsibility**:
- Filter chain configuration
- CORS setup
- CSRF configuration
- HTTPS redirect

## Data Flow Diagrams

### Login Flow

```
User                    AuthController         AuthService            Database
 │                           │                      │                    │
 ├─POST /login────────────►  │                      │                    │
 │  {user, pass}             │                      │                    │
 │                           │  login()──────────►  │                    │
 │                           │                      │  findByUsername()──►│
 │                           │                      │◄──user object───────│
 │                           │                      │                    │
 │                           │                      │ BCrypt.matches()    │
 │                           │                      │ (verify password)   │
 │                           │                      │                    │
 │                           │                      │ generateAccessToken()
 │                           │◄──token string──────│                    │
 │                           │                      │                    │
 │◄──{token, Bearer}─────────│                      │                    │
 │

```

### Subsequent Request Flow

```
User (with JWT)         JwtAuthenticationFilter    JwtTokenProvider     Database
 │                              │                         │               │
 ├─GET /protected──────────────►│                         │               │
 │ Authorization: Bearer xxx    │                         │               │
 │                              │ validateToken()────────►│               │
 │                              │                         │ verify sig    │
 │                              │                    check expiry         │
 │                              │◄──valid──────────────────              │
 │                              │                         │               │
 │                              │ getUsername()──────────►│               │
 │                              │◄──username──────────────│               │
 │                              │                         │               │
 │                              │ loadUserByUsername()────────────────►  │
 │                              │                         │  find user───│
 │                              │◄──UserDetails──────────────────────────│
 │                              │                         │               │
 │                              │ Create Authentication   │               │
 │                              │ Set in SecurityContext  │               │
 │                              │                         │               │
 │◄──200 OK + response──────────│                         │               │
 │

```

### Token Refresh Flow

```
User (with expired AccessToken)  AuthController    RefreshTokenService    Database
 │                                     │                  │                    │
 ├─POST /refresh─────────────────────►│                  │                    │
 │ {refreshToken}                      │                  │                    │
 │                                     │  verify()───────►│                    │
 │                                     │                  │  findByToken()───►│
 │                                     │                  │◄──RefreshToken────│
 │                                     │                  │                    │
 │                                     │                  │ checkExpiry()      │
 │                                     │                  │ checkRevoked()     │
 │                                     │                  │                    │
 │                                     │                  │ getUser()          │
 │                                     │◄──token object──│                    │
 │                                     │                  │                    │
 │                                     │ generateAccessToken(user)            │
 │                                     │ (JwtTokenProvider)                   │
 │                                     │                  │                    │
 │◄──{newAccessToken, refreshToken}──│                  │                    │
 │

```

## Security Architecture

### Authentication Flow

1. **Request Login**
   - Client sends username/password
   - AuthController validates input
   - AuthService authenticates

2. **Credential Verification**
   - Look up user in database
   - Compare plaintext password with BCrypt hash
   - Fail securely on mismatch

3. **Token Generation**
   - Create JWT with user info
   - Add roles to claims
   - Sign with HMAC-SHA256
   - Return to client

4. **Token Validation** (on protected endpoints)
   - Extract from Authorization header
   - Verify signature
   - Check expiration
   - Load user details
   - Continue to endpoint

### Refresh Token Management

1. **Token Creation** (on login)
   - Generate UUID refresh token
   - Store in database with user reference
   - Set expiry date (7 days default)
   - Return to client

2. **Token Validation** (on refresh request)
   - Look up token in database
   - Verify token not revoked
   - Check expiry date
   - Confirm associated user exists
   - Proceed with new access token generation

3. **Token Revocation**
   - Mark token as revoked in database
   - Prevents further use of token
   - User must login again

4. **Cleanup** (optional scheduled task)
   - Delete expired tokens from database
   - Maintain clean state
   - Prevent database bloat

### Password Security

```
User Password (plaintext)
        │
        ▼
   BCrypt Encoder
   (salt: random)
   (cost: 10)
        │
        ▼
Hashed Password (stored in DB)
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/tBy

On Login:
Plaintext password
        │
        ▼
BCrypt.matches() -> Compare with stored hash
        │
        ├─► Match: Continue
        └─► No match: Reject
```

### JWT Structure

```
Header.Payload.Signature

Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "admin",                    // username
  "iss": "secure-auth-api",          // issuer
  "iat": 1704110000,                 // issued at
  "exp": 1704110900,                 // expiration (15 min)
  "authorities": ["ADMIN", "USER"]   // roles
}

Signature:
HMAC-SHA256(
  base64(header) + "." + base64(payload),
  secret_key
)
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

### User Roles Table

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Refresh Tokens Table

```sql
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id)
);
```

## Adding New Features

### Add a New Endpoint

1. **Create DTO** (if needed)
   ```java
   // src/main/java/.../dto/UserProfileRequest.java
   public class UserProfileRequest {
       @NotBlank
       private String firstName;
       private String lastName;
   }
   ```

2. **Create Service Method**
   ```java
   // src/main/java/.../service/UserService.java
   @Service
   public class UserService {
       public void updateProfile(Long userId, UserProfileRequest request) {
           User user = userRepository.findById(userId)
               .orElseThrow(() -> new ResourceNotFoundException("User not found"));
           user.setFirstName(request.getFirstName());
           userRepository.save(user);
       }
   }
   ```

3. **Add Controller Method**
   ```java
   // src/main/java/.../controller/UserController.java
   @PutMapping("/{id}/profile")
   public ResponseEntity<Void> updateProfile(
           @PathVariable Long id,
           @Valid @RequestBody UserProfileRequest request) {
       userService.updateProfile(id, request);
       return ResponseEntity.ok().build();
   }
   ```

4. **Add Tests**
   ```java
   // src/test/java/.../UserControllerTest.java
   @Test
   public void testUpdateProfile() throws Exception {
       mockMvc.perform(put("/api/v1/users/1/profile")
           .contentType(MediaType.APPLICATION_JSON)
           .content(json))
           .andExpect(status().isOk());
   }
   ```

### Add a New Role

1. **Update Role Enum**
   ```java
   public enum Role {
       USER,
       ADMIN,
       MODERATOR  // New role
   }
   ```

2. **Create Role-Based Endpoint**
   ```java
   @PostMapping("/admin/reports")
   @PreAuthorize("hasAnyRole('ADMIN')")
   public ResponseEntity<Report> generateReport() {
       // Admin-only endpoint
   }
   ```

3. **Assign to Users**
   - Via database: `INSERT INTO user_roles VALUES (user_id, 'MODERATOR')`
   - Via service: `user.getRoles().add(Role.MODERATOR)`

## Testing Guidelines

### Unit Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {
    
    @Autowired
    private AuthService authService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Test
    public void testLoginSuccess() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$..."); // BCrypt hash
        
        when(userRepository.findByUsername("admin"))
            .thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(user))
            .thenReturn("token123");
        
        String token = authService.login("admin", "password");
        
        assertEquals("token123", token);
        verify(userRepository).findByUsername("admin");
    }
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testLoginEndpoint() throws Exception {
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

## Code Standards

### Naming Conventions

**Classes**: PascalCase
```java
public class AuthService { }
public class LoginRequest { }
```

**Methods**: camelCase
```java
public String generateAccessToken() { }
private void validateToken() { }
```

**Constants**: UPPER_SNAKE_CASE
```java
private static final String BEARER_PREFIX = "Bearer ";
private static final long TOKEN_EXPIRY = 900L;
```

**Variables**: camelCase
```java
String accessToken = "...";
Long userId = 1L;
```

### Javadoc Documentation

**Class Documentation** (required)
```java
/**
 * Brief description of the class.
 * 
 * Detailed explanation of responsibilities and usage.
 * 
 * @author Your Name
 * @version 1.0
 * @see RelatedClass
 */

```

**Method Documentation** (required for public methods)
```java
/**
 * Brief description of what the method does.
 * 
 * Detailed explanation of behavior, side effects, etc.
 * 
 * @param paramName Description of the parameter
 * @return Description of return value
 * @throws ExceptionType Description of when this is thrown
 */

``````

### Code Style

**Import Organization**
1. java.* packages
2. javax.* packages  
3. org.springframework.* packages
4. com.* packages (company code)

**Braces**
```java
public class CodeStyleExample {
    // Method example
    public void exampleMethod() {
        if (condition) {
            // Always use braces, even for single statements
            doSomething();
        }
    }

    public String method() {
        // Opening brace on same line
        return "value";
    }
}

```

**Line Length**: Max 100-120 characters

**Indentation**: 4 spaces (not tabs)

---

## Related Documents

- [README.md](../README.md) - Project overview
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - API endpoints
- [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md) - Setup guide


