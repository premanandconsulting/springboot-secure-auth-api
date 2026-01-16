# Running the Secure Auth API Application

## Summary
Your Spring Boot application is now fully configured and ready to run! All necessary dependencies have been added to the `pom.xml` file, and the configuration issues have been resolved.

---

## Quick Start Commands

### The Simplest Way (Recommended)
```bash
mvn spring-boot:run
```

### Or Build and Run the JAR
```bash
mvn clean package
java -jar target/springboot-secure-auth-api-1.0.0.jar
```

---

## All Available Options

### 1. **Run with Maven** (requires Maven installed)
```bash
mvn spring-boot:run
```
- Easy to run directly from source code
- Good for development
- Slower startup time than JAR

### 2. **Build JAR and Run** (recommended for production)
```bash
# First build
mvn clean package

# Then run the JAR
java -jar target/springboot-secure-auth-api-1.0.0.jar
```
- Faster startup
- Independent of Maven
- Better for deployment

### 3. **Run on Different Port** (if 8081 is already in use)
```bash
# With Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"

# With JAR
java -jar target/springboot-secure-auth-api-1.0.0.jar --server.port=8080
```

### 4. **Run with Debug Logging**
```bash
# With Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# With JAR
java -jar target/springboot-secure-auth-api-1.0.0.jar --debug
```

### 5. **Run in Background** (Linux/macOS/Git Bash)
```bash
nohup java -jar target/springboot-secure-auth-api-1.0.0.jar &
```

### 6. **Run with Custom JVM Settings**
```bash
java -Xmx512m -Xms256m -jar target/springboot-secure-auth-api-1.0.0.jar
```
- `-Xmx512m` - Maximum heap size: 512 MB
- `-Xms256m` - Initial heap size: 256 MB
- Adjust based on your system resources

### 7. **Run with Custom Properties File**
```bash
java -jar target/springboot-secure-auth-api-1.0.0.jar \
  --spring.config.location=classpath:application.yml
```

---

## Default Configuration

| Setting | Value |
|---------|-------|
| Server Port | 8081 |
| Server Context | http://localhost:8081 |
| Database Type | H2 (In-Memory) |
| H2 Console | http://localhost:8081/h2-console |
| Database Name | authdb |
| Session Mode | Stateless (JWT) |

---

## Access Points

Once the application is running:

### API Base URL
```
http://localhost:8081/api/v1/
```

### H2 Database Console
```
http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:authdb
- Username: sa
- Password: (leave blank)
```

---

## Testing the API

### 1. Login to Get Access Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 2. Use Token in Protected Requests
```bash
curl -X GET http://localhost:8081/api/v1/protected-endpoint \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Refresh Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

## Troubleshooting

### Issue: Port 8081 is already in use
**Solution:** Run on a different port:
```bash
java -jar target/springboot-secure-auth-api-1.0.0.jar --server.port=8080
```

### Issue: Java not found
**Solution:** Ensure Java 17+ is installed:
```bash
java -version
```

### Issue: Maven not found
**Solution:** Install Maven or use the `mvn` wrapper if available:
```bash
./mvnw spring-boot:run   # On Linux/macOS
mvnw.cmd spring-boot:run # On Windows
```

### Issue: Out of Memory
**Solution:** Increase JVM memory:
```bash
java -Xmx1024m -jar target/springboot-secure-auth-api-1.0.0.jar
```

### Issue: Build fails
**Solution:** Clean build:
```bash
mvn clean install -DskipTests
```

---

## Project Setup Summary

### Files Created
- **pom.xml** - Maven configuration with Spring Boot 3.1.5 and all dependencies

### Files Modified
- **SecurityConfig.java** - Fixed to use `AntPathRequestMatcher` for multiple servlets
- **JwtTokenProvider.java** - Updated JJWT API for version 0.12.3
- **README.md** - Added comprehensive running instructions

### Dependencies Added
- Spring Boot Web & Security
- Spring Data JPA
- Jakarta Bean Validation
- JJWT 0.12.3 (JWT support)
- H2 Database
- Lombok
- Testing frameworks

---

## Performance Tips

1. **Skip Tests During Build** (faster build):
   ```bash
   mvn clean package -DskipTests
   ```

2. **Use JAR for Production** (faster startup):
   ```bash
   java -jar target/springboot-secure-auth-api-1.0.0.jar
   ```

3. **Allocate More Memory** (for large datasets):
   ```bash
   java -Xmx2048m -jar target/springboot-secure-auth-api-1.0.0.jar
   ```

---

## Next Steps

1. ✅ Run the application using one of the commands above
2. ✅ Test the login endpoint to verify authentication works
3. ✅ Review API documentation in `docs/API_DOCUMENTATION.md`
4. ✅ Check the architecture overview in `docs/ARCHITECTURE.md`
5. ✅ Configure database for production in `src/main/resources/application.yml`

---

## Useful Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- Project Documentation: See `docs/` directory

---

**Generated:** January 15, 2026  
**Version:** 1.0.0  
**Spring Boot:** 3.1.5  
**Java:** 17+

