# Secure JWT Authentication API

A production-ready Spring Boot application demonstrating secure JWT-based authentication with refresh token support, role-based access control, and best practices for REST API security.

## Features

- ✅ JWT-based authentication with access and refresh tokens
- ✅ Secure password hashing using BCrypt
- ✅ Role-based access control (RBAC)
- ✅ Refresh token mechanism for token rotation
- ✅ Token expiry and validation
- ✅ SQL-based user and role management
- ✅ Spring Security integration
- ✅ H2 database (development) / PostgreSQL support (production)
- ✅ Comprehensive API documentation
- ✅ Docker & Docker Compose support
- ✅ Production-ready configuration

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- Git

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/springboot-secure-auth-api.git
cd springboot-secure-auth-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8081`

### Quick Test

1. **Login** (get access token):
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

2. **Use the Token** (in API requests):
```bash
curl -X GET http://localhost:8081/api/v1/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Build Tool | Maven | 3.9+ |
| Security | Spring Security | 6.x |
| JWT Library | JJWT | 0.11.5 |
| ORM | Spring Data JPA | 3.2.5 |
| Database | H2 (dev) / PostgreSQL (prod) | Latest |

## API Endpoints

### Authentication Endpoints

#### 1. User Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Purpose**: Authenticate user and receive JWT token
- **Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

#### 2. Refresh Token
- **Endpoint**: `POST /api/v1/auth/refresh`
- **Purpose**: Refresh expired access token
- **Request Body**:
```json
{
  "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c"
}
```
- **Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "9c9a2d4e-7c42-4e3e-bb0b-6b7d3a0e9f1c",
  "tokenType": "Bearer"
}
```

## Default Credentials

For development and testing:

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | ADMIN, USER |
| user | user123 | USER |

**⚠️ IMPORTANT**: Change these credentials in production!

## Configuration

### Environment Variables

Key configuration properties (in `application.yml`):

```yaml
security:
  jwt:
    secret: your-secret-key-min-32-chars
    access-token-expiry: 900           # 15 minutes in seconds
    refresh-token-expiry: 604800       # 7 days in seconds
    issuer: secure-auth-api

server:
  port: 8081
  servlet:
    context-path: /

spring:
  application:
    name: secure-auth-api
  jpa:
    hibernate:
      ddl-auto: create-drop             # create-drop for dev, validate for prod
  datasource:
    url: jdbc:h2:mem:testdb             # H2 for dev
    # url: jdbc:postgresql://localhost:5432/secure_auth  # PostgreSQL for prod
    driverClassName: org.h2.Driver
    # driverClassName: org.postgresql.Driver
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User Roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, roles),
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## Project Structure

```
springboot-secure-auth-api/
├── README.md                              # This file
├── docs/
│   ├── DOCUMENTATION_INDEX.md            # Documentation navigation
│   ├── API_DOCUMENTATION.md              # Complete API reference
│   ├── ARCHITECTURE.md                   # Code architecture guide
│   ├── SETUP_AND_DEPLOYMENT.md           # Setup and deployment guide
│   └── CHANGELOG.md                      # Version history
├── src/
│   ├── main/
│   │   ├── java/com/premanand/secureauth/
│   │   │   ├── SecureAuthApplication.java
│   │   │   ├── auth/
│   │   │   │   ├── controller/AuthController.java
│   │   │   │   ├── dto/{LoginRequest,LoginResponse}.java
│   │   │   │   └── service/{AuthService,RefreshTokenService}.java
│   │   │   ├── user/
│   │   │   │   ├── entity/{User,Role}.java
│   │   │   │   └── repository/UserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── token/
│   │   │   │   ├── entity/RefreshToken.java
│   │   │   │   └── repository/RefreshTokenRepository.java
│   │   │   └── config/
│   │   │       ├── SecurityConfig.java
│   │   │       ├── JwtConfigProperties.java
│   │   │       ├── PasswordConfig.java
│   │   │       └── DataLoader.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/...
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── .gitignore
```

## Security Best Practices

1. **Token Storage**
   - Store tokens in secure HTTP-only cookies or secure storage
   - Never store tokens in localStorage (XSS vulnerability)
   - Always use HTTPS in production

2. **Password Security**
   - Passwords are hashed using BCrypt (cost factor: 12)
   - Never log or expose plaintext passwords
   - Enforce strong password requirements in production

3. **JWT Configuration**
   - Use a strong secret key (minimum 32 characters)
   - Rotate secrets periodically
   - Set appropriate token expiry times
   - Implement token refresh mechanism

4. **API Security**
   - Use HTTPS/TLS for all communications
   - Implement CORS properly
   - Rate limiting on authentication endpoints
   - Log suspicious activities

## Troubleshooting

### Issue: JWT Token Validation Fails

**Problem**: "Invalid token" or "Token expired" errors

**Solutions**:
1. Ensure the JWT secret key matches between server and validation
2. Check token expiry time hasn't exceeded `access-token-expiry` setting
3. Verify the `Authorization` header format: `Bearer <token>`
4. Check system time synchronization between client and server

### Issue: Login Returns 401 Unauthorized

**Problem**: Even correct credentials fail to authenticate

**Solutions**:
1. Verify username and password are correct (case-sensitive)
2. Check that the user exists in the database
3. Ensure the default DataLoader has executed during startup
4. Check application logs for detailed error messages

### Issue: Build Fails with Maven

**Problem**: `mvn clean install` fails

**Solutions**:
1. Verify Java 17+ is installed: `java -version`
2. Clear Maven cache: `mvn clean`
3. Check Maven configuration: `mvn -v`
4. See [SETUP_AND_DEPLOYMENT.md - Troubleshooting](docs/SETUP_AND_DEPLOYMENT.md#troubleshooting)

### Issue: Port 8081 Already in Use

**Problem**: "Address already in use" error

**Solutions**:
1. Change port in `application.yml`: `server.port: 8082`
2. Kill process using port 8081:
   - Linux/Mac: `lsof -i :8081 | grep LISTEN | awk '{print $2}' | xargs kill -9`
   - Windows: `netstat -ano | findstr :8081` then `taskkill /PID <PID> /F`

## Documentation

Complete documentation is available in the `docs/` directory:

1. **[DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md)** - Navigation guide for all docs
2. **[API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** - Complete API reference
3. **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Code architecture and developer guide
4. **[SETUP_AND_DEPLOYMENT.md](docs/SETUP_AND_DEPLOYMENT.md)** - Setup, deployment, and Docker guide
5. **[CHANGELOG.md](docs/CHANGELOG.md)** - Version history and changes

## Development

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage report
mvn test jacoco:report
```

### Building JAR

```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar
```

### Docker Support

```bash
# Build Docker image
docker build -t secure-auth-api:latest .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app
```

See [SETUP_AND_DEPLOYMENT.md - Docker Deployment](docs/SETUP_AND_DEPLOYMENT.md#docker-deployment) for detailed instructions.

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -am 'Add feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Submit a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Premanand - Secure Authentication API

## Support

For issues, questions, or suggestions:

1. Check [Troubleshooting](README.md#troubleshooting) section
2. Review documentation in `docs/` directory
3. Check [ARCHITECTURE.md](docs/ARCHITECTURE.md) for code structure
4. Open an issue on GitHub

---

**Last Updated**: January 2026  
**Version**: 0.0.1-SNAPSHOT

