# Setup & Deployment Guide

## Table of Contents
- [Development Setup](#development-setup)
- [Building the Application](#building-the-application)
- [Running Locally](#running-locally)
- [Database Setup](#database-setup)
- [Production Deployment](#production-deployment)
- [Docker Deployment](#docker-deployment)
- [Environment Configuration](#environment-configuration)
- [Performance Tuning](#performance-tuning)
- [Troubleshooting](#troubleshooting)

## Development Setup

### Prerequisites

**Required**:
- Java Development Kit (JDK) 17 or higher
- Maven 3.9.0 or higher
- Git

**Optional**:
- Docker & Docker Compose
- PostgreSQL (for production-like testing)
- Postman or Insomnia (API testing)

### Verify Installation

```bash
# Check Java version
java -version
# Expected: openjdk version "17" or higher

# Check Maven version
mvn -version
# Expected: Apache Maven 3.9.0 or higher

# Check Git
git --version
```

### Clone and Build

```bash
# Clone repository
git clone <repository-url>
cd springboot-secure-auth-api

# Download dependencies
mvn clean install

# Verify build success
mvn clean verify
```

## Building the Application

### Development Build

```bash
# Build without running tests
mvn clean install -DskipTests

# Build with all tests
mvn clean install
```

### Production Build

```bash
# Create JAR file
mvn clean package

# Output: target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar
```

### Build Properties

Edit `pom.xml` to customize:

```xml
<properties>
    <java.version>17</java.version>                    <!-- Java version -->
    <jjwt.version>0.11.5</jjwt.version>               <!-- JWT library -->
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

## Running Locally

### Method 1: Maven Plugin

```bash
# Start application (default port 8081)
mvn spring-boot:run

# With custom port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"

# With custom profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Method 2: Run JAR File

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar

# Run with custom port
java -Dserver.port=8082 -jar target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar

# Run with environment variables
java -DJWT_SECRET=my-secret-key -jar target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar
```

### Verify Application

```bash
# Health check
curl http://localhost:8081/actuator/health

# Try login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Database Setup

### H2 Database (Default - Development)

Automatically configured. No setup needed.

**Access Console**:
```
URL: http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:authdb
Username: sa
Password: (leave empty)
```

### PostgreSQL (Production)

**Prerequisites**:
```bash
# Install PostgreSQL
# macOS
brew install postgresql@15

# Windows
# Download from https://www.postgresql.org/download/windows/

# Verify installation
psql --version
```

**Setup Database**:

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE authdb;

# Create user
CREATE USER authuser WITH PASSWORD 'secure_password';

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE authdb TO authuser;

# Quit
\q
```

**Configure Application**:

Create `application-prod.yml`:

```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/authdb
    driver-class-name: org.postgresql.Driver
    username: authuser
    password: secure_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000

  jpa:
    database: POSTGRESQL
    hibernate:
      ddl-auto: validate  # Use 'validate' in production
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        show_sql: false

security:
  jwt:
    secret: ${JWT_SECRET:change-me-in-production}
    access-token-expiry: ${JWT_ACCESS_TOKEN_EXPIRY:900}
    refresh-token-expiry: ${JWT_REFRESH_TOKEN_EXPIRY:604800}
    issuer: ${JWT_ISSUER:secure-auth-api}
```

**Run with PostgreSQL**:

```bash
java -Dspring.profiles.active=prod \
     -DJWT_SECRET=your-secret-key \
     -jar target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar
```

### Database Migration (Flyway)

Optional: Add for schema versioning.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Create migration files:
```
src/main/resources/db/migration/
├── V1__Initial_schema.sql
├── V2__Add_refresh_tokens.sql
└── V3__Add_audit_columns.sql
```

## Production Deployment

### Pre-Deployment Checklist

- [ ] Update JWT_SECRET to a strong value (32+ characters)
- [ ] Configure PostgreSQL (not H2)
- [ ] Set appropriate logging level (WARN)
- [ ] Disable H2 console
- [ ] Configure SSL/TLS certificates
- [ ] Set up firewall rules
- [ ] Configure backup strategy
- [ ] Review security configurations
- [ ] Load test application
- [ ] Plan monitoring/alerting

### Deployment Steps

**1. Build Production JAR**

```bash
mvn clean package -DskipTests -P prod
```

**2. Prepare Environment**

```bash
# Create deployment directory
sudo mkdir -p /opt/springboot-auth-api
cd /opt/springboot-auth-api

# Copy JAR
sudo cp target/springboot-secure-auth-api-0.0.1-SNAPSHOT.jar .
```

**3. Create .env File** (for environment variables)

```bash
sudo nano /opt/springboot-auth-api/.env

# Add:
JWT_SECRET=your-very-secret-key-min-32-chars
JWT_ACCESS_TOKEN_EXPIRY=900
JWT_REFRESH_TOKEN_EXPIRY=604800
JWT_ISSUER=prod-auth-api
DATASOURCE_URL=jdbc:postgresql://prod-db-host:5432/authdb
DATASOURCE_USERNAME=authuser
DATASOURCE_PASSWORD=db-secure-password
SERVER_PORT=8081
```

**4. Create Systemd Service** (Linux)

```bash
sudo nano /etc/systemd/system/springboot-auth-api.service
```

```ini
[Unit]
Description=Spring Boot Auth API
After=network.target

[Service]
Type=simple
User=springboot
WorkingDirectory=/opt/springboot-auth-api
EnvironmentFile=/opt/springboot-auth-api/.env
ExecStart=/usr/bin/java -jar springboot-secure-auth-api-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=append:/var/log/springboot-auth-api/app.log
StandardError=append:/var/log/springboot-auth-api/error.log

[Install]
WantedBy=multi-user.target
```

**5. Enable and Start Service**

```bash
sudo systemctl enable springboot-auth-api
sudo systemctl start springboot-auth-api

# Check status
sudo systemctl status springboot-auth-api

# View logs
sudo journalctl -u springboot-auth-api -f
```

### Nginx Reverse Proxy

```nginx
upstream springboot {
    server localhost:8081;
}

server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/api.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.yourdomain.com/privkey.pem;

    client_max_body_size 10M;

    location / {
        proxy_pass http://springboot;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
    }
}
```

## Docker Deployment

### Docker Build

Create `Dockerfile`:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/springboot-secure-auth-api-*.jar app.jar

EXPOSE 8081
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.9'

services:
  db:
    image: postgres:15-alpine
    container_name: auth-db
    environment:
      POSTGRES_DB: authdb
      POSTGRES_USER: authuser
      POSTGRES_PASSWORD: secure_password
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U authuser"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: auth-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/authdb
      SPRING_DATASOURCE_USERNAME: authuser
      SPRING_DATASOURCE_PASSWORD: secure_password
      JWT_SECRET: your-secret-key-min-32-chars
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    ports:
      - "8081:8081"
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped

volumes:
  postgres-data:
```

### Build and Run Docker

```bash
# Build image
docker build -t springboot-auth-api:latest .

# Run container
docker run -p 8081:8081 \
  -e JWT_SECRET=my-secret \
  springboot-auth-api:latest

# Using Docker Compose
docker-compose up -d

# Check logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## Environment Configuration

### Application Properties

**Environment Variable Mapping**:

| Environment Variable | application.yml | Default |
|---------------------|-----------------|---------|
| SERVER_PORT | server.port | 8081 |
| JWT_SECRET | security.jwt.secret | dev-secret-key-... |
| JWT_ACCESS_TOKEN_EXPIRY | security.jwt.access-token-expiry | 900 |
| JWT_REFRESH_TOKEN_EXPIRY | security.jwt.refresh-token-expiry | 604800 |
| JWT_ISSUER | security.jwt.issuer | secure-auth-api |
| DATASOURCE_URL | spring.datasource.url | jdbc:h2:mem:authdb |
| DATASOURCE_USERNAME | spring.datasource.username | sa |
| DATASOURCE_PASSWORD | spring.datasource.password | (empty) |

### Spring Profiles

**Development** (`application-dev.yml`):
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    com.premanand.secureauth: DEBUG
```

**Production** (`application-prod.yml`):
```yaml
spring:
  jpa:
    show-sql: false
logging:
  level:
    com.premanand.secureauth: WARN
```

**Activate Profile**:
```bash
java -Dspring.profiles.active=prod -jar app.jar
```

## Performance Tuning

### JVM Tuning

```bash
java -Xmx1g \           # Max heap size
     -Xms512m \         # Initial heap size
     -XX:+UseG1GC \     # Use G1 garbage collector
     -XX:MaxGCPauseMillis=200 \
     -jar app.jar
```

### Connection Pool

Update `application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

### Cache Configuration

Add Spring Cache:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users");
    }
}
```

## Troubleshooting

### Issue: Port 8081 Already in Use

```bash
# Find process using port
lsof -i :8081
# or Windows:
netstat -ano | findstr :8081

# Kill process
kill -9 <PID>
# or Windows:
taskkill /PID <PID> /F
```

### Issue: Database Connection Failed

```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Test connection
psql -h localhost -U authuser -d authdb
```

### Issue: JWT Secret Too Short

Error: "The specified key size (128 bits) is less than the minimum"

Solution: Use at least 32 characters:
```bash
openssl rand -base64 32
```

### Issue: OutOfMemory Exception

```bash
# Increase heap size
java -Xmx2g -jar app.jar
```

### Enable Debug Logging

```bash
java -Dlogging.level.com.premanand.secureauth=DEBUG \
     -jar app.jar
```

### Check Application Health

```bash
# If actuator enabled
curl http://localhost:8081/actuator/health

# Output:
# {
#   "status": "UP",
#   "components": {
#     "db": { "status": "UP" },
#     "diskSpace": { "status": "UP" }
#   }
# }
```

---

## Next Steps

1. Review [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
2. Check [README.md](../README.md) for feature overview
3. Set up CI/CD pipeline
4. Configure monitoring (e.g., Prometheus, ELK)
5. Implement rate limiting
6. Add API versioning strategy
7. Document custom extensions


