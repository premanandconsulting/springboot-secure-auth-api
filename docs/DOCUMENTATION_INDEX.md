# Documentation Index

Welcome to the Secure JWT Authentication API documentation! This guide will help you understand, set up, deploy, and extend the application.

## Quick Navigation

### 📖 For Getting Started
Start here if you're new to the project:

1. **[README.md](../README.md)** ⭐ START HERE
   - Project overview
   - Features summary
   - Quick start (5 minutes)
   - Technology stack
   - API endpoints overview
   - Database schema
   - Common troubleshooting

### 🔧 For Setup & Deployment
Use these guides to set up the environment and deploy:

2. **[SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md)**
   - Development environment setup
   - Building the application
   - Running locally
   - H2 vs PostgreSQL configuration
   - Production deployment checklist
   - Docker & Docker Compose setup
   - Nginx reverse proxy configuration
   - Performance tuning
   - Troubleshooting deployment issues

### 📚 For API Development
Reference these when working with the API:

3. **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)**
   - Complete endpoint reference
   - Request/response examples
   - Data models (DTOs and entities)
   - Service layer documentation
   - Configuration details
   - Error handling guide
   - Unit test examples
   - Environment variables reference

### 🏗️ For Code Understanding
Use these to understand the codebase:

4. **[ARCHITECTURE.md](ARCHITECTURE.md)**
   - Layered architecture explanation
   - Package structure and organization
   - Key components description
   - Data flow diagrams
   - Security architecture details
   - Database schema details
   - How to add new features
   - Testing guidelines
   - Code standards and conventions

---

## Documentation Structure

### By Role

**👨‍💼 Project Managers / Business Analysts**
- Start: [README.md](../README.md) - Features section
- Then: [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md) - Deployment overview

**👨‍💻 Backend Developers**
- Start: [README.md](../README.md)
- Then: [ARCHITECTURE.md](ARCHITECTURE.md)
- Reference: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- Setup: [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md)

**🔐 DevOps / SRE**
- Start: [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md)
- Reference: [README.md](../README.md) - Configuration section

**🧪 QA / Testers**
- Start: [README.md](../README.md) - API Endpoints section
- Reference: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- Deploy locally: [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md)

**📚 Technical Writers / Documentation**
- Use all documents as reference
- [ARCHITECTURE.md](ARCHITECTURE.md) for code structure

---

## Key Topics Quick Links

### Getting Started
- [Quick Test](../README.md#quick-test)
- [Installation](SETUP_AND_DEPLOYMENT.md#development-setup)
- [Running Locally](SETUP_AND_DEPLOYMENT.md#running-locally)

### API Usage
- [Login Endpoint](API_DOCUMENTATION.md#1-user-login)
- [Refresh Token](API_DOCUMENTATION.md#2-refresh-token)
- [Using the Token](API_DOCUMENTATION.md#using-the-token)
- [Data Models](API_DOCUMENTATION.md#data-models)

### Deployment
- [Docker Setup](SETUP_AND_DEPLOYMENT.md#docker-deployment)
- [PostgreSQL Setup](SETUP_AND_DEPLOYMENT.md#postgresql-production)
- [Production Deployment](SETUP_AND_DEPLOYMENT.md#production-deployment)

### Development
- [Adding Features](ARCHITECTURE.md#adding-new-features)
- [Testing](ARCHITECTURE.md#testing-guidelines)
- [Code Standards](ARCHITECTURE.md#code-standards)

### Troubleshooting
- [Common Issues](../README.md#troubleshooting)
- [Build Issues](SETUP_AND_DEPLOYMENT.md#troubleshooting)
- [JWT Issues](../README.md#issue-jwt-token-validation-fails)

---

## Document Summaries

### README.md (15 KB)
**Main project documentation**

Contains:
- Feature overview
- Technology stack details
- Installation instructions
- API endpoint overview
- Authentication flow
- Configuration guide
- Database schema
- Project structure
- Security best practices
- Troubleshooting guide

**When to use**: First-time setup, quick reference, feature overview

---

### SETUP_AND_DEPLOYMENT.md (13 KB)
**Setup and deployment guide**

Contains:
- Development environment prerequisites
- Build instructions (Maven)
- Running locally (via Maven or JAR)
- H2 database setup
- PostgreSQL configuration
- Production deployment steps
- Docker containerization
- Systemd service setup
- Nginx reverse proxy configuration
- JVM and performance tuning
- Comprehensive troubleshooting

**When to use**: Environment setup, deployment, performance issues

---

### API_DOCUMENTATION.md (12 KB)
**API reference and data models**

Contains:
- Complete endpoint documentation
- Request/response examples
- Data model documentation (DTOs and entities)
- Service layer documentation
- Core services (AuthService, JwtTokenProvider)
- Configuration classes
- Error handling
- Testing examples
- Environment variables guide

**When to use**: API development, integration, testing

---

### ARCHITECTURE.md (21 KB)
**Code architecture and developer guide**

Contains:
- Layered architecture overview
- Project layer descriptions
- Package structure
- Key component documentation
- Data flow diagrams
- Security architecture details
- Database schema details
- Adding new features guide
- Testing guidelines
- Code standards and conventions

**When to use**: Code review, feature development, refactoring

---

## Default Credentials

For quick testing (development only):

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | ADMIN, USER |
| user | user123 | USER |

> ⚠️ Change these in production!

---

## Technology Stack Quick Reference

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Build Tool | Maven | 3.9+ |
| Database | H2 (dev) / PostgreSQL (prod) | Latest |
| JWT Library | JJWT | 0.11.5 |
| Security | Spring Security | 6.x |
| ORM | Spring Data JPA | 3.2.5 |

---

## Common Tasks

### I want to...

**...start the application**
→ [SETUP_AND_DEPLOYMENT.md - Running Locally](SETUP_AND_DEPLOYMENT.md#running-locally)

**...call the login API**
→ [API_DOCUMENTATION.md - User Login](API_DOCUMENTATION.md#1-user-login)

**...add a new endpoint**
→ [ARCHITECTURE.md - Add a New Endpoint](ARCHITECTURE.md#add-a-new-endpoint)

**...add a new role**
→ [ARCHITECTURE.md - Add a New Role](ARCHITECTURE.md#add-a-new-role)

**...deploy to production**
→ [SETUP_AND_DEPLOYMENT.md - Production Deployment](SETUP_AND_DEPLOYMENT.md#production-deployment)

**...use Docker**
→ [SETUP_AND_DEPLOYMENT.md - Docker Deployment](SETUP_AND_DEPLOYMENT.md#docker-deployment)

**...fix a build error**
→ [SETUP_AND_DEPLOYMENT.md - Troubleshooting](SETUP_AND_DEPLOYMENT.md#troubleshooting)

**...understand the code structure**
→ [ARCHITECTURE.md - Project Layers](ARCHITECTURE.md#project-layers)

**...write tests**
→ [ARCHITECTURE.md - Testing Guidelines](ARCHITECTURE.md#testing-guidelines)

---

## Code Documentation

All source files have been enhanced with comprehensive Javadoc comments:

### Updated Files
- ✅ `AuthController.java` - Endpoint documentation
- ✅ `LoginRequest.java` - DTO validation documentation
- ✅ `LoginResponse.java` - Response model documentation
- ✅ `AuthService.java` - Service logic documentation
- ✅ `User.java` - Entity field documentation
- ✅ `Role.java` - Enum documentation

### Documentation Includes
- Class-level Javadoc with purpose and usage
- Method-level Javadoc with parameters and return values
- Field-level documentation with constraints
- Security considerations and best practices
- Usage examples where applicable
- Related class references

---

## File Structure

```
springboot-secure-auth-api/
├── README.md                           # Main documentation
├── SETUP_AND_DEPLOYMENT.md            # Setup and deployment guide
├── API_DOCUMENTATION.md               # API reference
├── ARCHITECTURE.md                    # Code architecture guide
├── DOCUMENTATION_INDEX.md             # This file
├── pom.xml                            # Maven configuration
│
├── src/main/java/.../
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── dto/{LoginRequest,LoginResponse}.java
│   │   └── service/AuthService.java
│   ├── user/
│   │   ├── entity/{User,Role}.java
│   │   └── repository/UserRepository.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   ├── token/
│   │   ├── entity/RefreshToken.java
│   │   └── repository/RefreshTokenRepository.java
│   └── config/
│       ├── JwtConfigProperties.java
│       ├── PasswordConfig.java
│       ├── SecurityConfig.java
│       └── DataLoader.java
│
└── src/main/resources/
    └── application.yml
```

---

## Getting Help

1. **Error in the code?**
   - Check [SETUP_AND_DEPLOYMENT.md - Troubleshooting](SETUP_AND_DEPLOYMENT.md#troubleshooting)
   - Check [README.md - Troubleshooting](../README.md#troubleshooting)

2. **How do I use the API?**
   - See [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
   - Check [README.md - API Endpoints](../README.md#api-endpoints)

3. **How do I add a feature?**
   - See [ARCHITECTURE.md - Adding New Features](ARCHITECTURE.md#adding-new-features)

4. **How do I deploy?**
   - See [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md)

5. **How does it work internally?**
   - See [ARCHITECTURE.md](ARCHITECTURE.md)

---

## Version Information

- **Project Version**: 0.0.1-SNAPSHOT
- **Java Version**: 17
- **Spring Boot Version**: 3.2.5
- **Last Updated**: January 2026

---

## Next Steps

1. Start with [README.md](../README.md) for an overview
2. Follow [SETUP_AND_DEPLOYMENT.md](SETUP_AND_DEPLOYMENT.md) to get running
3. Use [API_DOCUMENTATION.md](API_DOCUMENTATION.md) to test endpoints
4. Reference [ARCHITECTURE.md](ARCHITECTURE.md) when developing
5. Check individual source files for detailed code documentation

Good luck! 🚀


